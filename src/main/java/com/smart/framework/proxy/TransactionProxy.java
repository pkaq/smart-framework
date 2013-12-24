package com.smart.framework.proxy;

import com.smart.framework.annotation.Transaction;
import com.smart.framework.helper.DBHelper;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionProxy implements Proxy {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProxy.class);

    @Override
    public Object doProxy(ProxyChain proxyChain) throws Exception {
        Object result;
        try {
            Method method = proxyChain.getTargetMethod();
            if (method.isAnnotationPresent(Transaction.class)) {
                // 开启事务
                DBHelper.beginTransaction();
                if (logger.isDebugEnabled()) {
                    logger.debug("[Smart] begin transaction");
                }
                // 执行操作
                result = proxyChain.doProxyChain();
                // 提交事务
                DBHelper.commitTransaction();
                if (logger.isDebugEnabled()) {
                    logger.debug("[Smart] commit transaction");
                }
            } else {
                // 执行操作
                result = proxyChain.doProxyChain();
            }
        } catch (Exception e) {
            // 回滚事务
            DBHelper.rollbackTransaction();
            if (logger.isDebugEnabled()) {
                logger.debug("[Smart] rollback transaction");
            }
            // 向上抛出异常
            throw e;
        }
        return result;
    }
}
