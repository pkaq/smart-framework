package com.smart.framework;

import com.smart.framework.base.BaseEntity;
import com.smart.framework.helper.DBHelper;
import com.smart.framework.helper.SQLHelper;
import com.smart.framework.util.ArrayUtil;
import com.smart.framework.util.CastUtil;
import com.smart.framework.util.ObjectUtil;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataSet {

    // 查询单条数据，并转为相应类型的对象
    public static <T> T select(Class<T> cls, String condition, Object... params) {
        String sql = SQLHelper.generateSelectSQL(cls, condition, "");
        return DBHelper.queryBean(cls, sql, params);
    }

    // 查询多条数据，并转为相应类型的列表
    public static <T> List<T> selectList(Class<T> cls, String condition, String sort, Object... params) {
        String sql = SQLHelper.generateSelectSQL(cls, condition, sort);
        return DBHelper.queryBeanList(cls, sql, params);
    }

    // 插入一条数据
    public static boolean insert(Class<?> cls, Map<String, Object> fieldMap) {
        String sql = SQLHelper.generateInsertSQL(cls, fieldMap.keySet());
        int rows = DBHelper.update(sql, fieldMap.values().toArray());
        return rows > 0;
    }

    // 插入一个实体对象
    public static boolean insert(BaseEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> cls = entity.getClass();
        Map<String, Object> fieldMap = createFieldMap(entity);
        return insert(cls, fieldMap);
    }

    // 更新相关数据
    public static boolean update(Class<?> cls, Map<String, Object> fieldMap, String condition, Object... params) {
        String sql = SQLHelper.generateUpdateSQL(cls, fieldMap, condition);
        int rows = DBHelper.update(sql, ArrayUtil.concat(fieldMap.values().toArray(), params));
        return rows > 0;
    }

    // 更新一个实体对象
    public static boolean update(BaseEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> cls = entity.getClass();
        Map<String, Object> fieldMap = createFieldMap(entity);
        String condition = "id = ?";
        Object[] params = {ObjectUtil.getFieldValue(entity, "id")};
        return update(cls, fieldMap, condition, params);
    }

    // 删除相关数据
    public static boolean delete(Class<?> cls, String condition, Object... params) {
        String sql = SQLHelper.generateDeleteSQL(cls, condition);
        int rows = DBHelper.update(sql, params);
        return rows > 0;
    }

    // 删除一个实体对象
    public static boolean delete(BaseEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> cls = entity.getClass();
        String condition = "id = ?";
        Object[] params = {ObjectUtil.getFieldValue(entity, "id")};
        return delete(cls, condition, params);
    }

    // 查询数据条数
    public static long selectCount(Class<?> cls, String condition, Object... params) {
        String sql = SQLHelper.generateSelectSQLForCount(cls, condition);
        return DBHelper.queryCount(sql, params);
    }

    // 查询多条数据，并转为列表（分页方式）
    public static <T> List<T> selectListForPager(int pageNumber, int pageSize, Class<T> cls, String condition, String sort, Object... params) {
        String sql = SQLHelper.generateSelectSQLForPager(pageNumber, pageSize, cls, condition, sort);
        return DBHelper.queryBeanList(cls, sql, params);
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

    // 根据列名查询单条数据，并转为相应类型的对象
    public static <T> T selectColumn(Class<T> cls, String column, String condition, Object... params) {
        String sql = SQLHelper.generateSelectSQL(cls, condition, "");
        return DBHelper.queryColumn(column, sql, params);
    }

    // 根据列名查询多条数据，并转为相应类型的列表
    public static <T> List<T> selectColumnList(Class<?> cls, String column, String condition, String sort, Object... params) {
        String sql = SQLHelper.generateSelectSQL(cls, condition, sort);
        return DBHelper.queryColumnList(column, sql, params);
    }

    private static Map<String, Object> createFieldMap(BaseEntity obj) {
        Map<String, Object> fieldMap = new LinkedHashMap<String, Object>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            Object fieldValue = ObjectUtil.getFieldValue(obj, fieldName);
            fieldMap.put(fieldName, fieldValue);
        }
        return fieldMap;
    }
}
