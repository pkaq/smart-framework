package org.smart4j.framework.orm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smart4j.framework.dao.DatabaseHelper;
import org.smart4j.framework.dao.SqlHelper;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.framework.util.ObjectUtil;

/**
 * 提供与实体相关的数据库操作
 *
 * @author huangyong
 * @since 1.0
 */
public class DataSet {

    /**
     * 查询单条数据，并转为相应类型的对象
     */
    public static <T> T select(Class<T> entityClass, Conditions conditions, Object... params) {
        String conditionStr = getConditionStr(conditions);
        String sql = SqlHelper.generateSelectSql(entityClass, conditionStr, "");
        return DatabaseHelper.queryEntity(entityClass, sql, params);
    }

    /**
     * 查询多条数据，并转为相应类型的列表
     */
    public static <T> List<T> selectList(Class<T> entityClass) {
        return selectList(entityClass, null, null);
    }

    /**
     * 查询多条数据，并转为相应类型的列表（带有条件、排序、参数）
     */
    public static <T> List<T> selectList(Class<T> entityClass, Conditions conditions, Sorts sorts, Object... params) {
        String conditionStr = getConditionStr(conditions);
        String sortStr = getSortStr(sorts);
        String sql = SqlHelper.generateSelectSql(entityClass, conditionStr, sortStr);
        return DatabaseHelper.queryEntityList(entityClass, sql, params);
    }

    /**
     * 插入一条数据
     */
    public static boolean insert(Class<?> entityClass, Map<String, Object> fieldMap) {
        String sql = SqlHelper.generateInsertSql(entityClass, fieldMap.keySet());
        int rows = DatabaseHelper.update(sql, fieldMap.values().toArray());
        return rows > 0;
    }

    /**
     * 插入一个实体对象
     */
    public static boolean insert(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entity.getClass();
        Map<String, Object> fieldMap = ObjectUtil.getFieldMap(entity);
        return insert(entityClass, fieldMap);
    }

    /**
     * 更新相关数据
     */
    public static boolean update(Class<?> entityClass, Map<String, Object> fieldMap, Conditions conditions, Object... params) {
        String conditionStr = getConditionStr(conditions);
        String sql = SqlHelper.generateUpdateSql(entityClass, fieldMap, conditionStr);
        int rows = DatabaseHelper.update(sql, ArrayUtil.concat(fieldMap.values().toArray(), params));
        return rows > 0;
    }

    /**
     * 更新一个实体对象
     */
    public static boolean update(Object entity) {
        return update(entity, "id");
    }

    /**
     * 更新一个实体对象（可指定主键字段名）
     */
    public static boolean update(Object entityObject, String pkName) {
        if (entityObject == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entityObject.getClass();
        Map<String, Object> fieldMap = ObjectUtil.getFieldMap(entityObject);
        Conditions conditions = new Conditions().condition(pkName, "=", "?");
        Object[] params = {ObjectUtil.getFieldValue(entityObject, pkName)};
        return update(entityClass, fieldMap, conditions, params);
    }

    /**
     * 删除相关数据
     */
    public static boolean delete(Class<?> entityClass, Conditions conditions, Object... params) {
        String conditionStr = getConditionStr(conditions);
        String sql = SqlHelper.generateDeleteSql(entityClass, conditionStr);
        int rows = DatabaseHelper.update(sql, params);
        return rows > 0;
    }

    /**
     * 删除一个实体对象
     */
    public static boolean delete(Object entityObject) {
        return delete(entityObject, "id");
    }

    /**
     * 删除一个实体对象（可指定主键字段名）
     */
    public static boolean delete(Object entityObject, String pkName) {
        if (entityObject == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entityObject.getClass();
        Conditions conditions = new Conditions().condition(pkName, "=", "?");
        Object[] params = {ObjectUtil.getFieldValue(entityObject, pkName)};
        return delete(entityClass, conditions, params);
    }

    /**
     * 查询数据条数
     */
    public static long selectCount(Class<?> entityClass, Conditions conditions, Object... params) {
        String conditionStr = getConditionStr(conditions);
        String sql = SqlHelper.generateSelectSqlForCount(entityClass, conditionStr);
        return DatabaseHelper.queryCount(sql, params);
    }

    /**
     * 查询多条数据，并转为列表（分页方式）
     */
    public static <T> List<T> selectListForPager(int pageNumber, int pageSize, Class<T> entityClass, Conditions conditions, Sorts sorts, Object... params) {
        String conditionStr = getConditionStr(conditions);
        String sortStr = getSortStr(sorts);
        String sql = SqlHelper.generateSelectSqlForPager(pageNumber, pageSize, entityClass, conditionStr, sortStr);
        return DatabaseHelper.queryEntityList(entityClass, sql, params);
    }

    /**
     * 查询多条数据，并转为映射
     */
    public static <T> Map<Long, T> selectMap(Class<T> entityClass) {
        return selectMap(entityClass, "id", null);
    }

    /**
     * 查询多条数据，并转为映射（带有条件、参数）
     */
    public static <T> Map<Long, T> selectMap(Class<T> entityClass, Conditions conditions, Object... params) {
        return selectMap(entityClass, "id", conditions, params);
    }

    /**
     * 查询多条数据，并转为映射（可指定主键字段名）
     */
    @SuppressWarnings("unchecked")
    public static <PK, T> Map<PK, T> selectMap(Class<T> entityClass, String pkName, Conditions conditions, Object... params) {
        Map<PK, T> map = new HashMap<PK, T>();
        List<T> list = selectList(entityClass, conditions, null, params);
        for (T obj : list) {
            PK pk = (PK) ObjectUtil.getFieldValue(obj, pkName);
            map.put(pk, obj);
        }
        return map;
    }

    /**
     * 根据列名查询单条数据，并转为相应类型的对象
     */
    public static <T> T selectColumn(Class<T> entityClass, String columnName, Conditions conditions, Object... params) {
        String conditionStr = getConditionStr(conditions);
        String sql = SqlHelper.generateSelectSql(entityClass, conditionStr, "");
        return DatabaseHelper.queryColumn(columnName, sql, params);
    }

    /**
     * 根据列名查询多条数据，并转为相应类型的列表
     */
    public static <T> List<T> selectColumnList(Class<?> entityClass, String columnName, Conditions conditions, Sorts sorts, Object... params) {
        String conditionStr = getConditionStr(conditions);
        String sortStr = getSortStr(sorts);
        String sql = SqlHelper.generateSelectSql(entityClass, conditionStr, sortStr);
        return DatabaseHelper.queryColumnList(columnName, sql, params);
    }

    private static String getConditionStr(Conditions conditions) {
        return (conditions != null) ? conditions.toString() : "";
    }

    private static String getSortStr(Sorts sorts) {
        return (sorts != null) ? sorts.toString() : "";
    }
}
