package com.smart.framework.helper;

import com.smart.framework.util.CastUtil;
import com.smart.framework.util.DBUtil;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

public class DBHelper {

    private static final Logger logger = Logger.getLogger(DBHelper.class);

    private static final BasicDataSource ds = new BasicDataSource();
    private static final QueryRunner runner = new QueryRunner(ds);

    private static String databaseType;

    // 定义一个局部线程变量（使每个线程都拥有自己的连接）
    private static ThreadLocal<Connection> connContainer = new ThreadLocal<Connection>();

    static {
        if (logger.isInfoEnabled()) {
            logger.info("Init DBHelper...");
        }

        // 初始化数据源
        ds.setDriverClassName(ConfigHelper.getStringProperty("jdbc.driver"));
        ds.setUrl(ConfigHelper.getStringProperty("jdbc.url"));
        ds.setUsername(ConfigHelper.getStringProperty("jdbc.username"));
        ds.setPassword(ConfigHelper.getStringProperty("jdbc.password"));
        ds.setMaxActive(ConfigHelper.getNumberProperty("jdbc.max.active"));
        ds.setMaxIdle(ConfigHelper.getNumberProperty("jdbc.max.idle"));

        // 获取数据库类型
        try {
            databaseType = ds.getConnection().getMetaData().getDatabaseProductName();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // 获取数据源
    public static DataSource getDataSource() {
        return ds;
    }

    // 从数据源中获取数据库连接
    public static Connection getConnectionFromDataSource() {
        Connection conn;
        try {
            conn = ds.getConnection();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return conn;
    }

    // 从线程局部变量中获取数据库连接
    public static Connection getConnectionFromThreadLocal() {
        return connContainer.get();
    }

    // 开启事务
    public static void beginTransaction() {
        Connection conn = getConnectionFromThreadLocal();
        if (conn == null) {
            try {
                conn = getConnectionFromDataSource();
                conn.setAutoCommit(false);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                connContainer.set(conn);
            }
        }
    }

    // 提交事务
    public static void commitTransaction() {
        Connection conn = getConnectionFromThreadLocal();
        if (conn != null) {
            try {
                conn.commit();
                conn.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                connContainer.remove();
            }
        }
    }

    // 回滚事务
    public static void rollbackTransaction() {
        Connection conn = getConnectionFromThreadLocal();
        if (conn != null) {
            try {
                conn.rollback();
                conn.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                connContainer.remove();
            }
        }
    }

    // 获取数据库默认事务隔离级别
    public static int getDefaultIsolationLevel() {
        int level;
        try {
            level = getConnectionFromThreadLocal().getMetaData().getDefaultTransactionIsolation();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return level;
    }

    // 获取数据库类型
    public static String getDBType() {
        return databaseType;
    }

    // 执行查询（返回一个对象）
    public static <T> T queryBean(Class<T> cls, String sql, Object... params) {
        Map<String, String> map = EntityHelper.getEntityMap().get(cls);
        return DBUtil.queryBean(runner, cls, map, sql, params);
    }

    // 执行查询（返回多个对象）
    public static <T> List<T> queryBeanList(Class<T> cls, String sql, Object... params) {
        Map<String, String> map = EntityHelper.getEntityMap().get(cls);
        return DBUtil.queryBeanList(runner, cls, map, sql, params);
    }

    // 执行更新（包括 UPDATE、INSERT、DELETE）
    public static int update(String sql, Object... params) {
        // 若当前线程中存在连接，则传入（用于事务处理），否则将从数据源中获取连接
        Connection conn = getConnectionFromThreadLocal();
        return DBUtil.update(runner, conn, sql, params);
    }

    // 执行查询（返回 count 结果）
    public static int queryCount(Class<?> cls, String sql, Object... params) {
        return CastUtil.castInt(DBUtil.queryColumn(runner, "count(*)", sql, params));
    }

    // 查询映射列表
    public static List<Map<String, Object>> queryMapList(String sql, Object... params) {
        return DBUtil.queryMapList(runner, sql, params);
    }

    // 查询单列数据
    public static Object queryColumn(String column, String sql, Object... params) {
        return DBUtil.queryColumn(runner, column, sql, params);
    }
}
