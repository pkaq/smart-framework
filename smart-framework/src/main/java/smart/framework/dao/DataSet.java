package smart.framework.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import smart.framework.orm.BaseEntity;
import smart.framework.util.ArrayUtil;
import smart.framework.util.CastUtil;
import smart.framework.util.ObjectUtil;

public class DataSet {

    // 查询单条数据，并转为相应类型的对象
    public static <T> T select(Class<T> entityClass, String condition, Object... params) {
        String sql = SQLHelper.generateSelectSQL(entityClass, condition, "");
        return DatabaseHelper.queryEntity(entityClass, sql, params);
    }

    // 查询多条数据，并转为相应类型的列表
    public static <T> List<T> selectList(Class<T> entityClass, String condition, String sort, Object... params) {
        String sql = SQLHelper.generateSelectSQL(entityClass, condition, sort);
        return DatabaseHelper.queryEntityList(entityClass, sql, params);
    }

    // 插入一条数据
    public static boolean insert(Class<?> entityClass, Map<String, Object> fieldMap) {
        String sql = SQLHelper.generateInsertSQL(entityClass, fieldMap.keySet());
        int rows = DatabaseHelper.update(sql, fieldMap.values().toArray());
        return rows > 0;
    }

    // 插入一个实体对象
    public static boolean insert(BaseEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entity.getClass();
        Map<String, Object> fieldMap = createFieldMap(entity);
        return insert(entityClass, fieldMap);
    }

    // 更新相关数据
    public static boolean update(Class<?> entityClass, Map<String, Object> fieldMap, String condition, Object... params) {
        String sql = SQLHelper.generateUpdateSQL(entityClass, fieldMap, condition);
        int rows = DatabaseHelper.update(sql, ArrayUtil.concat(fieldMap.values().toArray(), params));
        return rows > 0;
    }

    // 更新一个实体对象
    public static boolean update(BaseEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entity.getClass();
        Map<String, Object> fieldMap = createFieldMap(entity);
        String condition = "id = ?";
        Object[] params = {ObjectUtil.getFieldValue(entity, "id")};
        return update(entityClass, fieldMap, condition, params);
    }

    // 删除相关数据
    public static boolean delete(Class<?> entityClass, String condition, Object... params) {
        String sql = SQLHelper.generateDeleteSQL(entityClass, condition);
        int rows = DatabaseHelper.update(sql, params);
        return rows > 0;
    }

    // 删除一个实体对象
    public static boolean delete(BaseEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entity.getClass();
        String condition = "id = ?";
        Object[] params = {ObjectUtil.getFieldValue(entity, "id")};
        return delete(entityClass, condition, params);
    }

    // 查询数据条数
    public static long selectCount(Class<?> entityClass, String condition, Object... params) {
        String sql = SQLHelper.generateSelectSQLForCount(entityClass, condition);
        return DatabaseHelper.queryCount(sql, params);
    }

    // 查询多条数据，并转为列表（分页方式）
    public static <T> List<T> selectListForPager(int pageNumber, int pageSize, Class<T> entityClass, String condition, String sort, Object... params) {
        String sql = SQLHelper.generateSelectSQLForPager(pageNumber, pageSize, entityClass, condition, sort);
        return DatabaseHelper.queryEntityList(entityClass, sql, params);
    }

    // 查询多条数据，并转为映射
    public static <T> Map<Long, T> selectMap(Class<T> entityClass, String condition, Object... params) {
        Map<Long, T> map = new HashMap<Long, T>();
        List<T> list = selectList(entityClass, condition, "", params);
        for (T obj : list) {
            Long id = CastUtil.castLong(ObjectUtil.getFieldValue(obj, "id"));
            map.put(id, obj);
        }
        return map;
    }

    // 根据列名查询单条数据，并转为相应类型的对象
    public static <T> T selectColumn(Class<T> entityClass, String column, String condition, Object... params) {
        String sql = SQLHelper.generateSelectSQL(entityClass, condition, "");
        return DatabaseHelper.queryColumn(column, sql, params);
    }

    // 根据列名查询多条数据，并转为相应类型的列表
    public static <T> List<T> selectColumnList(Class<?> entityClass, String column, String condition, String sort, Object... params) {
        String sql = SQLHelper.generateSelectSQL(entityClass, condition, sort);
        return DatabaseHelper.queryColumnList(column, sql, params);
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
