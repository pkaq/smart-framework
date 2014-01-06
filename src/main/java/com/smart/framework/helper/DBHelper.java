package com.smart.framework.helper;

import com.smart.framework.util.CastUtil;
import com.smart.framework.util.DBUtil;
import com.smart.framework.util.StringUtil;
import com.smart.framework.util.XMLUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBHelper {

    private static final Logger logger = LoggerFactory.getLogger(DBHelper.class);

    // 定义一个局部线程变量（使每个线程都拥有自己的连接）
    private static final ThreadLocal<Connection> connContainer = new ThreadLocal<Connection>();

    // 创建数据源
    private static BasicDataSource ds = createDataSource();

    private static BasicDataSource createDataSource() {
        // 读取 XML 配置文件
        Document doc = XMLUtil.loadDocument("db.xml");
        if (doc != null) {
            // 获取根元素
            Element db = doc.getRootElement();
            // 获取相关配置项
            String driver = db.elementTextTrim("driver");
            String url = db.elementTextTrim("url");
            String username = db.elementTextTrim("username");
            String password = db.elementTextTrim("password");
            int maxActive = CastUtil.castInt(db.elementTextTrim("maxActive"), 0);
            int maxIdle = CastUtil.castInt(db.elementTextTrim("maxIdle"), 0);
            // 创建并初始化 DBCP 数据源
            ds = new BasicDataSource();
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
        return ds;
    }

    public static Connection getConnection() {
        Connection conn;
        try {
            // 先从 ThreadLocal 中获取 Connection
            conn = connContainer.get();
            if (conn == null) {
                // 若不存在，则从 DataSource 中获取 Connection
                conn = ds.getConnection();
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
            dbType = ds.getConnection().getMetaData().getDatabaseProductName();
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

    // 查询单列数据（返回一个对象）
    public static <T> T queryColumn(String column, String sql, Object... params) {
        return DBUtil.queryColumn(getQueryRunner(), column, sql, params);
    }

    // 查询单列数据（返回多个对象）
    public static <T> List<T> queryColumnList(String column, String sql, Object... params) {
        return DBUtil.queryColumnList(getQueryRunner(), column, sql, params);
    }

    private static QueryRunner getQueryRunner() {
        return new QueryRunner(ds);
    }

    private static Map<String, String> getEntityMap(Class<?> cls) {
        return EntityHelper.getEntityMap().get(cls);
    }
}
