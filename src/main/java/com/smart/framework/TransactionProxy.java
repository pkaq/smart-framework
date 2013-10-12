package com.smart.framework;

import com.smart.framework.annotation.Transaction;
import com.smart.framework.helper.DBHelper;
import java.lang.reflect.Method;
import java.sql.Connection;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Logger;

public class TransactionProxy implements MethodInterceptor {

    private static final Logger logger = Logger.getLogger(TransactionProxy.class);

    private static final TransactionProxy instance = new TransactionProxy();

    private TransactionProxy() {
    }

    public static TransactionProxy getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> cls) {
        return (T) Enhancer.create(cls, this);
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object result;
        if (method.isAnnotationPresent(Transaction.class)) {
            try {
                // 开启事务
                DBHelper.beginTransaction();

                // 设置事务隔离级别
                Transaction transaction = method.getAnnotation(Transaction.class);
                int currentIsolation = transaction.isolation();
                int defaultIsolation = DBHelper.getDefaultIsolationLevel();
                if (currentIsolation != defaultIsolation) {
                    Connection conn = DBHelper.getConnectionFromThreadLocal();
                    conn.setTransactionIsolation(currentIsolation);
                }

                // 执行操作
                method.setAccessible(true);
                result = methodProxy.invokeSuper(proxy, args);

                // 提交事务
                DBHelper.commitTransaction();
            } catch (Exception e) {
                // 回滚事务
                DBHelper.rollbackTransaction();

                logger.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            result = methodProxy.invokeSuper(proxy, args);
        }
        return result;
    }
}
