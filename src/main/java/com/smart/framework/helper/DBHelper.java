package com.smart.framework.helper;

import com.smart.framework.util.CastUtil;
import com.smart.framework.util.DBUtil;
import com.smart.framework.util.StringUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBHelper {

    private static final Logger logger = LoggerFactory.getLogger(DBHelper.class);

    // 定义一个局部线程变量（使每个线程都拥有自己的连接）
    private static final ThreadLocal<Connection> connContainer = new ThreadLocal<Connection>();

    // 从配置文件中获取配置项
    private static final String driver = ConfigHelper.getStringProperty("jdbc.driver");
    private static final String url = ConfigHelper.getStringProperty("jdbc.url");
    private static final String username = ConfigHelper.getStringProperty("jdbc.username");
    private static final String password = ConfigHelper.getStringProperty("jdbc.password");
    private static final int maxActive = ConfigHelper.getNumberProperty("jdbc.max.active");
    private static final int maxIdle = ConfigHelper.getNumberProperty("jdbc.max.idle");

    // 获取数据源
    public static DataSource getDataSource() {
        BasicDataSource ds = new BasicDataSource();
        if (StringUtil.isNotEmpty(driver)) {
            ds.setDriverClassName(driver);
        }
        if (StringUtil.isNotEmpty(url)) {
            ds.setUrl(url);
        }
        if (StringUtil.isNotEmpty(username)) {
            ds.setUsername(username);
        }
        if (StringUtil.isNotEmpty(password)) {
            ds.setPassword(password);
        }
        if (maxActive != 0) {
            ds.setMaxActive(maxActive);
        }
        if (maxIdle != 0) {
            ds.setMaxIdle(maxIdle);
        }
        return ds;
    }

    public static Connection getConnection() {
        Connection conn;
        try {
            // 先从 ThreadLocal 中获取 Connection
            conn = connContainer.get();
            if (conn == null) {
                // 若不存在，则从 DataSource 中获取 Connection
                conn = getDataSource().getConnection();
            }
        } catch (SQLException e) {
            logger.error("获取数据库连接出错！", e);
            throw new RuntimeException(e);
        }
        return conn;
    }

    // 开启事务
    public static void beginTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                logger.error("开启事务出错！", e);
                throw new RuntimeException(e);
            } finally {
                connContainer.set(conn);
            }
        }
    }

    // 提交事务
    public static void commitTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.commit();
                conn.close();
            } catch (SQLException e) {
                logger.error("提交事务出错！", e);
                throw new RuntimeException(e);
            } finally {
                connContainer.remove();
            }
        }
    }

    // 回滚事务
    public static void rollbackTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.rollback();
                conn.close();
            } catch (SQLException e) {
                logger.error("回滚事务出错！", e);
                throw new RuntimeException(e);
            } finally {
                connContainer.remove();
            }
        }
    }

    // 获取数据库默认事务隔离级别
    public static int getDefaultIsolationLevel() {
        int level;
        try {
            level = getConnection().getMetaData().getDefaultTransactionIsolation();
        } catch (SQLException e) {
            logger.error("获取数据库默认事务隔离级别出错！", e);
            throw new RuntimeException(e);
        }
        return level;
    }

    // 获取数据库类型
    public static String getDBType() {
        String dbType;
        try {
            dbType = getDataSource().getConnection().getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            logger.error("获取数据库类型出错！", e);
            throw new RuntimeException(e);
        }
        return dbType;
    }

    // 执行查询（返回一个对象）
    public static <T> T queryBean(Class<T> cls, String sql, Object... params) {
        return DBUtil.queryBean(getQueryRunner(), cls, getEntityMap(cls), sql, params);
    }

    // 执行查询（返回多个对象）
    public static <T> List<T> queryBeanList(Class<T> cls, String sql, Object... params) {
        return DBUtil.queryBeanList(getQueryRunner(), cls, getEntityMap(cls), sql, params);
    }

    // 执行更新（包括 UPDATE、INSERT、DELETE）
    public static int update(String sql, Object... params) {
        // 更新操作需使用 ThreadLocal 中的 Connection（为了保证在同一个事务中）
        return DBUtil.update(getQueryRunner(), getConnection(), sql, params);
    }

    // 执行查询（返回 count 结果）
    public static int queryCount(String sql, Object... params) {
        return CastUtil.castInt(DBUtil.queryColumn(getQueryRunner(), "count(*)", sql, params));
    }

    // 查询映射列表
    public static List<Map<String, Object>> queryMapList(String sql, Object... params) {
        return DBUtil.queryMapList(getQueryRunner(), sql, params);
    }

    // 查询单列数据
    public static Object queryColumn(String column, String sql, Object... params) {
        return DBUtil.queryColumn(getQueryRunner(), column, sql, params);
    }

    private static QueryRunner getQueryRunner() {
        return new QueryRunner(getDataSource());
    }

    private static Map<String, String> getEntityMap(Class<?> cls) {
        return EntityHelper.getEntityMap().get(cls);
    }
}
