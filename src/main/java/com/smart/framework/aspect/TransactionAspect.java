package com.smart.framework.aspect;

import com.smart.framework.annotation.Transaction;
import com.smart.framework.base.BaseAspect;
import com.smart.framework.helper.DBHelper;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class TransactionAspect extends BaseAspect {

    private static final Logger logger = Logger.getLogger(TransactionAspect.class);

    private static final DBHelper dbHelper = DBHelper.getInstance();

    @Override
    public boolean intercept(Class<?> cls, Method method, Object[] params) {
        return method.isAnnotationPresent(Transaction.class);
    }

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Exception {
        // 开启事务
        dbHelper.beginTransaction();
        if (logger.isDebugEnabled()) {
            logger.debug("[Begin Transaction]");
        }

        // 设置事务隔离级别
        setTransactionIsolation(method);
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Exception {
        // 提交事务
        dbHelper.commitTransaction();
        if (logger.isDebugEnabled()) {
            logger.debug("[Commit Transaction]");
        }
    }

    @Override
    public void error(Class<?> cls, Method method, Object[] params, Exception e) {
        // 回滚事务
        dbHelper.rollbackTransaction();
        if (logger.isDebugEnabled()) {
            logger.debug("[Rollback Transaction]");
        }
    }

    private void setTransactionIsolation(Method method) throws SQLException {
        // 缺省使用数据库默认隔离级别，可在 @Transaction 注解上设置特定的隔离级别
        Transaction transaction = method.getAnnotation(Transaction.class);
        int currentIsolation = transaction.isolation();
        int defaultIsolation = dbHelper.getDefaultIsolationLevel();
        if (currentIsolation != defaultIsolation) {
            Connection conn = dbHelper.getConnectionFromThreadLocal();
            conn.setTransactionIsolation(currentIsolation);
            if (logger.isDebugEnabled()) {
                logger.debug("[Set Transaction Isolation] Isolation: " + currentIsolation);
            }
        }
    }
}
