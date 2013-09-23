package com.smart.framework.base;

import java.sql.Connection;

public abstract class BaseService {

    // 事务隔离级别
    protected static final int ISOLATION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;
    protected static final int ISOLATION_READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;
    protected static final int ISOLATION_REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;
    protected static final int ISOLATION_SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;
}
