package com.smart.framework.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.KeyedHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {

    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);

    // 查询（返回 Array）
    public static Object[] queryArray(QueryRunner runner, String sql, Object... params) {
        Object[] result;
        try {
            result = runner.query(sql, new ArrayHandler(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询（返回 ArrayList）
    public static List<Object[]> queryArrayList(QueryRunner runner, String sql, Object... params) {
        List<Object[]> result;
        try {
            result = runner.query(sql, new ArrayListHandler(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询（返回 Map）
    public static Map<String, Object> queryMap(QueryRunner runner, String sql, Object... params) {
        Map<String, Object> result;
        try {
            result = runner.query(sql, new MapHandler(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询（返回 MapList）
    public static List<Map<String, Object>> queryMapList(QueryRunner runner, String sql, Object... params) {
        List<Map<String, Object>> result;
        try {
            result = runner.query(sql, new MapListHandler(), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询（返回 Bean）
    public static <T> T queryBean(QueryRunner runner, Class<T> cls, Map<String, String> map, String sql, Object... params) {
        T result;
        try {
            if (MapUtil.isNotEmpty(map)) {
                result = runner.query(sql, new BeanHandler<T>(cls, new BasicRowProcessor(new BeanProcessor(map))), params);
            } else {
                result = runner.query(sql, new BeanHandler<T>(cls), params);
            }
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询（返回 BeanList）
    public static <T> List<T> queryBeanList(QueryRunner runner, Class<T> cls, Map<String, String> map, String sql, Object... params) {
        List<T> result;
        try {
            if (MapUtil.isNotEmpty(map)) {
                result = runner.query(sql, new BeanListHandler<T>(cls, new BasicRowProcessor(new BeanProcessor(map))), params);
            } else {
                result = runner.query(sql, new BeanListHandler<T>(cls), params);
            }
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询指定列名的值（单条数据）
    public static <T> T queryColumn(QueryRunner runner, String column, String sql, Object... params) {
        T result;
        try {
            result = runner.query(sql, new ScalarHandler<T>(column), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询指定列名的值（多条数据）
    public static <T> List<T> queryColumnList(QueryRunner runner, String column, String sql, Object... params) {
        List<T> result;
        try {
            result = runner.query(sql, new ColumnListHandler<T>(column), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 查询指定列名对应的记录映射
    public static <T> Map<T, Map<String, Object>> queryKeyMap(QueryRunner runner, String column, String sql, Object... params) {
        Map<T, Map<String, Object>> result;
        try {
            result = runner.query(sql, new KeyedHandler<T>(column), params);
        } catch (SQLException e) {
            logger.error("查询出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 更新（包括 UPDATE、INSERT、DELETE，返回受影响的行数）
    public static int update(QueryRunner runner, Connection conn, String sql, Object... params) {
        int result;
        try {
            if (conn != null) {
                result = runner.update(conn, sql, params);
            } else {
                result = runner.update(sql, params);
            }
        } catch (SQLException e) {
            logger.error("更新出错！", e);
            throw new RuntimeException(e);
        }
        printSQL(sql);
        return result;
    }

    // 插入（返回自动生成的主键）
    public static Serializable insertReturnPK(Connection conn, String sql, Object... params) {
        Serializable key = null;
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
