package com.smart.framework;

import com.smart.framework.helper.DBHelper;
import com.smart.framework.helper.SQLHelper;
import com.smart.framework.util.CastUtil;
import com.smart.framework.util.ObjectUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSet {

    private static final DBHelper dbHelper = DBHelper.getInstance();
    private static final SQLHelper sqlHelper = SQLHelper.getInstance();

    // 查询单条数据，并转为对象
    public static <T> T select(Class<T> cls, String condition, Object... params) {
        String sql = sqlHelper.generateSelectSQL(cls, condition, params);
        return dbHelper.queryBean(cls, sql);
    }

    // 查询多条数据，并转为列表
    public static <T> List<T> selectList(Class<T> cls, String condition, String order, Object... params) {
        String sql = sqlHelper.generateSelectSQL(cls, condition, order, params);
        return dbHelper.queryBeanList(cls, sql);
    }

    // 插入一条数据
    public static boolean insert(Class<?> cls, Map<String, Object> fieldMap) {
        String sql = sqlHelper.generateInsertSQL(cls, fieldMap);
        int rows = dbHelper.update(sql);
        return rows > 0;
    }

    // 更新相关数据
    public static boolean update(Class<?> cls, Map<String, Object> fieldMap, String condition, Object... params) {
        String sql = sqlHelper.generateUpdateSQL(cls, fieldMap, condition, params);
        int rows = dbHelper.update(sql);
        return rows > 0;
    }

    // 删除相关数据
    public static boolean delete(Class<?> cls, String condition, Object... params) {
        String sql = sqlHelper.generateDeleteSQL(cls, condition, params);
        int rows = dbHelper.update(sql);
        return rows > 0;
    }

    // 查询数据条数
    public static int selectCount(Class<?> cls, String condition, Object... params) {
        String sql = sqlHelper.generateSelectSQLForCount(cls, condition, params);
        return dbHelper.queryCount(cls, sql);
    }

    // 查询多条数据，并转为列表（分页方式）
    public static <T> List<T> selectListForPager(int pageNumber, int pageSize, Class<T> cls, String condition, String sort, Object... params) {
        String sql = sqlHelper.generateSelectSQLForPager(pageNumber, pageSize, cls, condition, sort, params);
        return dbHelper.queryBeanList(cls, sql);
    }

    // 查询多条数据，并转为映射
    public static <T> Map<Long, T> selectMap(Class<T> cls, String condition, Object... params) {
        Map<Long, T> map = new HashMap<Long, T>();
        List<T> list = selectList(cls, condition, "", params);
        for (T obj : list) {
            Long id = CastUtil.castLong(ObjectUtil.getFieldValue(obj, "id"));
            map.put(id, obj);
        }
        return map;
    }
}
