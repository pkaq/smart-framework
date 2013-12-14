package com.smart.framework.helper;

import com.smart.framework.util.CastUtil;
import com.smart.framework.util.DBUtil;
import com.smart.framework.util.StringUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

public class DBHelper {

    private static final Logger logger = Logger.getLogger(DBHelper.class);

    private static final BasicDataSource ds = new BasicDataSource();
    private static final QueryRunner runner = new QueryRunner(ds);

    // 定义一个局部线程变量（使每个线程都拥有自己的连接）
    private static final ThreadLocal<Connection> connContainer = new ThreadLocal<Connection>();

    static {
        // 从配置文件中获取配置项
        String driver = ConfigHelper.getStringProperty("jdbc.driver");
        String url = ConfigHelper.getStringProperty("jdbc.url");
        String username = ConfigHelper.getStringProperty("jdbc.username");
        String password = ConfigHelper.getStringProperty("jdbc.password");
        int maxActive = ConfigHelper.getNumberProperty("jdbc.max.active");
        int maxIdle = ConfigHelper.getNumberProperty("jdbc.max.idle");
        // 设置数据源相关字段
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
    }

    // 从数据源中获取数据库连接
    public static Connection getConnectionFromDataSource() {
        Connection conn;
        try {
            conn = ds.getConnection();
        } catch (Exception e) {
            logger.error("从数据源中获取数据库连接出错！", e);
            throw new RuntimeException(e);
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
                logger.error("开启事务出错！", e);
                throw new RuntimeException(e);
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
                logger.error("提交事务出错！", e);
                throw new RuntimeException(e);
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
            level = getConnectionFromThreadLocal().getMetaData().getDefaultTransactionIsolation();
        } catch (Exception e) {
            logger.error("获取数据库默认事务隔离级别出错！", e);
            throw new RuntimeException(e);
        }
        return level;
    }

    // 获取数据库类型
    public static String getDBType() {
        String dbType;
        try {
            dbType = ds.getConnection().getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            logger.error("获取数据库类型出错！", e);
            throw new RuntimeException(e);
        }
        return dbType;
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
    public static int queryCount(String sql, Object... params) {
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
