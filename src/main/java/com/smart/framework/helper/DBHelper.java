package com.smart.framework.helper;

import com.smart.framework.util.ArrayUtil;
import com.smart.framework.util.MapUtil;
import com.smart.framework.util.StringUtil;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBHelper {

    private static final Logger logger = LoggerFactory.getLogger(DBHelper.class);

    // 定义一个局部线程变量（使每个线程都拥有自己的连接）
    private static final ThreadLocal<Connection> connContainer = new ThreadLocal<Connection>();

    private static final DataSource dataSource = getDataSource();

    private static final QueryRunner queryRunner = new QueryRunner(dataSource);

    // 获取数据源
    public static DataSource getDataSource() {
        // 从 config.properties 文件中读取 JDBC 配置项
        String driver = ConfigHelper.getConfigString("jdbc.driver");
        String url = ConfigHelper.getConfigString("jdbc.url");
        String username = ConfigHelper.getConfigString("jdbc.username");
        String password = ConfigHelper.getConfigString("jdbc.password");
        // 创建 DBCP 数据源
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
        return ds;
    }

    public static Connection getConnection() {
        Connection conn;
        try {
            // 先从 ThreadLocal 中获取 Connection
            conn = connContainer.get();
            if (conn == null) {
                // 若不存在，则从 DataSource 中获取 Connection
                conn = dataSource.getConnection();
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

    // 获取数据库类型
    public static String getDBType() {
        return ConfigHelper.getConfigString("jdbc.type");
    }

    // 执行查询（返回一个对象）
    public static <T> T queryBean(Class<T> cls, String sql, Object... params) {
        T result;
        try {
            Map<String, String> fieldMap = EntityHelper.getEntityMap().get(cls);
            if (MapUtil.isNotEmpty(fieldMap)) {
                result = queryRunner.query(sql, new BeanHandler<T>(cls, new BasicRowProcessor(new BeanProcessor(fieldMap))), params);
            } else {
                result = queryRunner.query(sql, new BeanHandler<T>(cls), params);
            }
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 执行查询（返回多个对象）
    public static <T> List<T> queryBeanList(Class<T> cls, String sql, Object... params) {
        List<T> result;
        try {
            Map<String, String> fieldMap = EntityHelper.getEntityMap().get(cls);
            if (MapUtil.isNotEmpty(fieldMap)) {
                result = queryRunner.query(sql, new BeanListHandler<T>(cls, new BasicRowProcessor(new BeanProcessor(fieldMap))), params);
            } else {
                result = queryRunner.query(sql, new BeanListHandler<T>(cls), params);
            }
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 执行更新（包括 UPDATE、INSERT、DELETE）
    public static int update(String sql, Object... params) {
        int result;
        try {
            Connection conn = getConnection();
            result = queryRunner.update(conn, sql, params);
        } catch (SQLException e) {
            logger.error("更新出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 执行查询（返回 count 结果）
    public static long queryCount(String sql, Object... params) {
        long result;
        try {
            result = queryRunner.query(sql, new ScalarHandler<Long>("count(*)"), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询映射列表
    public static List<Map<String, Object>> queryMapList(String sql, Object... params) {
        List<Map<String, Object>> result;
        try {
            result = queryRunner.query(sql, new MapListHandler(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询单列数据（返回一个对象）
    public static <T> T queryColumn(String column, String sql, Object... params) {
        T result;
        try {
            result = queryRunner.query(sql, new ScalarHandler<T>(column), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询单列数据（返回多个对象）
    public static <T> List<T> queryColumnList(String column, String sql, Object... params) {
        List<T> result;
        try {
            result = queryRunner.query(sql, new ColumnListHandler<T>(column), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 插入（返回自动生成的主键）
    public static Serializable insertReturnPK(String sql, Object... params) {
        Serializable key = null;
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            if (ArrayUtil.isNotEmpty(params)) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            int rows = pstmt.executeUpdate();
            if (rows == 1) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    key = (Serializable) rs.getObject(1);
                }
            }
        } catch (SQLException e) {
            logger.error("插入出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return key;
    }

    private static void printSQL(String sql) {
        if (logger.isDebugEnabled()) {
            logger.debug("[Smart] SQL - {}", sql);
        }
    }
}
