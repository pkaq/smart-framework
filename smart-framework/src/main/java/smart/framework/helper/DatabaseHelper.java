package smart.framework.helper;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.BeanMapHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.KeyedHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smart.framework.util.ArrayUtil;
import smart.framework.util.MapUtil;
import smart.framework.util.StringUtil;

public class DatabaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);

    // 定义一个局部线程变量（使每个线程都拥有自己的连接）
    private static final ThreadLocal<Connection> connContainer = new ThreadLocal<Connection>();

    private static String databaseType;
    private static DataSource dataSource;
    private static QueryRunner queryRunner;

    static {
        databaseType = ConfigHelper.getConfigString("jdbc.type");
        if (StringUtil.isNotEmpty(databaseType)) {
            dataSource = getDataSource();
            queryRunner = new QueryRunner(dataSource);
        }
    }

    public static String getDatabaseType() {
        return databaseType;
    }

    public static DataSource getDataSource() {
        // 从配置文件中读取 JDBC 配置项
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
        // 解决 java.sql.SQLException: Already closed. 的问题（连接池会自动关闭长时间没有使用的连接）
        ds.setValidationQuery("select 1 from dual");
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
                // 将 Connection 放入 ThreadLocal 中
                if (conn != null) {
                    connContainer.set(conn);
                }
            }
        } catch (SQLException e) {
            logger.error("获取数据库连接出错！", e);
            throw new RuntimeException(e);
        }
        return conn;
    }

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

    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        T result;
        try {
            Map<String, String> fieldMap = EntityHelper.getEntityMap().get(entityClass);
            if (MapUtil.isNotEmpty(fieldMap)) {
                result = queryRunner.query(sql, new BeanHandler<T>(entityClass, new BasicRowProcessor(new BeanProcessor(fieldMap))), params);
            } else {
                result = queryRunner.query(sql, new BeanHandler<T>(entityClass), params);
            }
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        List<T> result;
        try {
            Map<String, String> fieldMap = EntityHelper.getEntityMap().get(entityClass);
            if (MapUtil.isNotEmpty(fieldMap)) {
                result = queryRunner.query(sql, new BeanListHandler<T>(entityClass, new BasicRowProcessor(new BeanProcessor(fieldMap))), params);
            } else {
                result = queryRunner.query(sql, new BeanListHandler<T>(entityClass), params);
            }
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    public static <K, V> Map<K, V> queryEntityMap(Class<V> entityClass, String sql, Object... params) {
        Map<K, V> entityMap;
        try {
            entityMap = queryRunner.query(sql, new BeanMapHandler<K, V>(entityClass), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return entityMap;
    }

    public static Object[] queryArray(String sql, Object... params) {
        Object[] array;
        try {
            array = queryRunner.query(sql, new ArrayHandler(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return array;
    }

    public static List<Object[]> queryArrayList(String sql, Object... params) {
        List<Object[]> arrayList;
        try {
            arrayList = queryRunner.query(sql, new ArrayListHandler(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return arrayList;
    }

    public static Map<String, Object> queryMap(String sql, Object... params) {
        Map<String, Object> map;
        try {
            map = queryRunner.query(sql, new MapHandler(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return map;
    }

    public static List<Map<String, Object>> queryMapList(String sql, Object... params) {
        List<Map<String, Object>> fieldMapList;
        try {
            fieldMapList = queryRunner.query(sql, new MapListHandler(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return fieldMapList;
    }

    public static <T> T queryColumn(String sql, Object... params) {
        T entity;
        try {
            entity = queryRunner.query(sql, new ScalarHandler<T>(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return entity;
    }

    public static <T> List<T> queryColumnList(String sql, Object... params) {
        List<T> list;
        try {
            list = queryRunner.query(sql, new ColumnListHandler<T>(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return list;
    }

    public static <T> Set<T> queryColumnSet(String sql, Object... params) {
        List<T> list = queryColumnList(sql, params);
        return new LinkedHashSet<T>(list);
    }

    public static <T> Map<T, Map<String, Object>> queryColumnMap(String column, String sql, Object... params) {
        Map<T, Map<String, Object>> map;
        try {
            map = queryRunner.query(sql, new KeyedHandler<T>(column), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return map;
    }

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
        logger.debug("[Smart] SQL - {}", sql);
    }
}
