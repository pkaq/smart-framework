package com.smart.framework.aspect;

import com.smart.framework.annotation.Transaction;
import com.smart.framework.base.BaseAspect;
import com.smart.framework.helper.DBHelper;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionAspect extends BaseAspect {

    private static final Logger logger = LoggerFactory.getLogger(TransactionAspect.class);

    @Override
    public boolean intercept(Class<?> cls, Method method, Object[] params) {
        return method.isAnnotationPresent(Transaction.class);
    }

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Exception {
        // 开启事务
        DBHelper.beginTransaction();
        if (logger.isDebugEnabled()) {
            logger.debug("[Smart] begin transaction");
        }
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Exception {
        // 提交事务
        DBHelper.commitTransaction();
        if (logger.isDebugEnabled()) {
            logger.debug("[Smart] commit transaction");
        }
    }

    @Override
    public void error(Class<?> cls, Method method, Object[] params, Exception e) {
        // 回滚事务
        DBHelper.rollbackTransaction();
        if (logger.isDebugEnabled()) {
            logger.debug("[Smart] rollback transaction");
        }
    }
}
