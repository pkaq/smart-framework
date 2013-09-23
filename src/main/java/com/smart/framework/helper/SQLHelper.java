package com.smart.framework.helper;

import com.smart.framework.annotation.Table;
import com.smart.framework.util.ArrayUtil;
import com.smart.framework.util.FileUtil;
import com.smart.framework.util.MapUtil;
import com.smart.framework.util.StringUtil;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLHelper {

    private static final Properties sqlProperties = FileUtil.loadPropFile("sql.properties");

    public static String getSQL(String key) {
        String value = "";
        if (sqlProperties.containsKey(key)) {
            value = sqlProperties.getProperty(key);
        } else {
            System.err.println("Can not get property [" + key + "] in sql.properties file.");
        }
        return value;
    }

    public static String generateSelectSQL(Class<?> cls, String condition, Object... params) {
        StringBuilder sql = new StringBuilder("select * from ").append(getTable(cls));
        sql.append(generateWhere(condition, params));
        return sql.toString();
    }

    public static String generateInsertSQL(Class<?> cls, Map<String, Object> fieldMap) {
        StringBuilder sql = new StringBuilder("insert into ").append(getTable(cls));
        if (MapUtil.isNotEmpty(fieldMap)) {
            int i = 0;
            StringBuilder columns = new StringBuilder(" ");
            StringBuilder values = new StringBuilder(" values ");
            for (Map.Entry<String, ?> fieldEntry : fieldMap.entrySet()) {
                String columnName = StringUtil.toUnderline(fieldEntry.getKey());
                Object columnValue = fieldEntry.getValue();
                if (i == 0) {
                    columns.append("(").append(columnName);
                    values.append("('").append(columnValue).append("'");
                } else if (i == fieldMap.size() - 1) {
                    columns.append(", ").append(columnName).append(")");
                    values.append(", '").append(columnValue).append("')");
                } else {
                    columns.append(", ").append(columnName);
                    values.append(", '").append(columnValue).append("'");
                }
                i++;
            }
            sql.append(columns).append(values);
        }
        return sql.toString();
    }

    public static String generateDeleteSQL(Class<?> cls, String condition, Object... params) {
        StringBuilder sql = new StringBuilder("delete from ").append(getTable(cls));
        sql.append(generateWhere(condition, params));
        return sql.toString();
    }

    public static String generateUpdateSQL(Class<?> cls, Map<String, Object> fieldMap, String condition, Object... params) {
        StringBuilder sql = new StringBuilder("update ").append(getTable(cls));
        if (MapUtil.isNotEmpty(fieldMap)) {
            sql.append(" set ");
            int i = 0;
            for (Map.Entry<String, ?> fieldEntry : fieldMap.entrySet()) {
                String columnName = StringUtil.toUnderline(fieldEntry.getKey());
                Object columnValue = fieldEntry.getValue();
                if (i == 0) {
                    sql.append(columnName).append(" = '").append(columnValue).append("'");
                } else {
                    sql.append(", ").append(columnName).append(" = '").append(columnValue).append("'");
                }
                i++;
            }
        }
        sql.append(generateWhere(condition, params));
        return sql.toString();
    }

    public static String generateSelectSQLForCount(Class<?> cls, String condition, Object... params) {
        StringBuilder sql = new StringBuilder("select count(*) from ").append(getTable(cls));
        sql.append(generateWhere(condition, params));
        return sql.toString();
    }

    public static String generateSelectSQLForPager(int pageNumber, int pageSize, Class<?> cls, String condition, String sort, Object... params) {
        StringBuilder sql = new StringBuilder();
        String table = getTable(cls);
        String where = generateWhere(condition, params);
        String order = generateOrder(sort);
        String dbType = DBHelper.getDBType();
        if (dbType.equalsIgnoreCase("mysql")) {
            int pageStart = (pageNumber - 1) * pageSize;
            int pageEnd = pageSize;
            appendSQLForMySQL(sql, table, where, order, pageStart, pageEnd);
        } else if (dbType.equalsIgnoreCase("oracle")) {
            int pageStart = (pageNumber - 1) * pageSize + 1;
            int pageEnd = pageStart + pageSize;
            appendSQLForOracle(sql, table, where, order, pageStart, pageEnd);
        } else if (dbType.equalsIgnoreCase("mssql")) {
            int pageStart = (pageNumber - 1) * pageSize;
            int pageEnd = pageSize;
            appendSQLForSQLServer(sql, table, where, order, pageStart, pageEnd);
        } else if (dbType.equalsIgnoreCase("db2")) {
            // DB2
        } else if (dbType.equalsIgnoreCase("sybase")) {
            // Sybase
        } else if (dbType.equalsIgnoreCase("derby")) {
            // Derby
        } else if (dbType.equalsIgnoreCase("postgre")) {
            // Postgre
        } else if (dbType.equalsIgnoreCase("hsql")) {
            // HSQL
        } else if (dbType.equalsIgnoreCase("h2")) {
            // H2
        }
        return sql.toString();
    }

    private static String getTable(Class<?> cls) {
        String tableName;
        if (cls.isAnnotationPresent(Table.class)) {
            tableName = cls.getAnnotation(Table.class).value();
        } else {
            tableName = StringUtil.toUnderline(cls.getSimpleName());
        }
        return tableName;
    }

    private static String generateWhere(String condition, Object[] params) {
        StringBuilder builder = new StringBuilder();
        if (StringUtil.isNotEmpty(condition)) {
            StringBuffer buffer = new StringBuffer();
            if (ArrayUtil.isNotEmpty(params)) {
                Matcher matcher = Pattern.compile("\\?").matcher(condition);
                for (int i = 0; matcher.find(); i++) {
                    String param = params[i].toString();
                    if (StringUtil.isNumber(param)) {
                        matcher.appendReplacement(buffer, param);
                    } else {
                        matcher.appendReplacement(buffer, "'" + param + "'");
                    }
                }
                matcher.appendTail(buffer);
                builder.append(" where ").append(buffer);
            } else {
                builder.append(" where ").append(condition);
            }
        }
        return builder.toString();
    }

    private static String generateOrder(String order) {
        StringBuilder builder = new StringBuilder();
        if (StringUtil.isNotEmpty(order)) {
            builder.append(" order by ").append(order);
        }
        return builder.toString();
    }

    private static void appendSQLForMySQL(StringBuilder sql, String table, String where, String order, int pageStart, int pageEnd) {
        /*
            select * from 表名 where 条件 order by 排序 limit 开始位置, 结束位置
         */
        sql.append("select * from ").append(table);
        sql.append(where);
        sql.append(order);
        sql.append(" limit ").append(pageStart).append(", ").append(pageEnd);
    }

    private static void appendSQLForOracle(StringBuilder sql, String table, String where, String order, int pageStart, int pageEnd) {
        /*
            select a.* from (
                select rownum rn, t.* from 表名 t where 条件 order by 排序
            ) a
            where a.rn >= 开始位置 and a.rn < 结束位置
        */
        sql.append("select a.* from (select rownum rn, t.* from ").append(table).append(" t");
        sql.append(where);
        sql.append(order);
        sql.append(") a where a.rn >= ").append(pageStart).append(" and a.rn < ").append(pageEnd);
    }

    private static void appendSQLForSQLServer(StringBuilder sql, String table, String where, String order, int pageStart, int pageEnd) {
        /*
            select top 结束位置 * from 表名 where 条件 and id not in (
                select top 开始位置 id from 表名 where 条件 order by 排序
            ) order by 排序
        */
        sql.append("select top ").append(pageEnd).append(" * from ").append(table);
        if (StringUtil.isNotEmpty(where)) {
            sql.append(where).append(" and ");
        } else {
            sql.append(" where ");
        }
        sql.append("id not in (select top ").append(pageStart).append(" id from ").append(table);
        sql.append(where);
        sql.append(order);
        sql.append(") ").append(order);
    }
}
