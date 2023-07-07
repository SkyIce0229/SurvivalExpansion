package tmmi.skyice.survivalexpansion.db.util;

import tmmi.skyice.survivalexpansion.db.annotation.TableField;
import tmmi.skyice.survivalexpansion.db.table.typehandler.TypeHandle;
import tmmi.skyice.survivalexpansion.util.StringUtil;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlTemplate {

    public static String selectByIdSql(String tableName, long id) {
        return "SELECT * FROM " + tableName + " WHERE id = " + id;
    }

    public static String deleteByIdSql(String tableName, long id) {
        return "DELETE FROM " + tableName + " WHERE id = " + id;
    }

    public static String updateByIdSql(String tableName, Object obj) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName + " SET ");

        int count = 0;
        long id = -2147483648;

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (value != null) {
                String fieldName = StringUtil.camelToUnderline(field.getName());
                if ("id".equals(fieldName)) {
                    id = Long.parseLong(value.toString());
                    continue;
                }

                count++;
                sqlBuilder.append("`").append(fieldName).append("`").append(" = ").append(fromValue(field, value)).append(", ");
            }
        }

        if (count == 0) {
            return null;
        }

        sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
        sqlBuilder.append(" WHERE `id` = ").append(id);

        return sqlBuilder.toString();
    }

    public static String insertSql(String tableName, Object obj) {
        List<String> columnList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();

        int count = 0;

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(obj);
                if (value != null) {
                    count++;
                    columnList.add("`" + StringUtil.camelToUnderline(field.getName()) + "`");
                    valueList.add(fromValue(field, value));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        if (count == 0) {
            return null;
        }

        String column = String.join(", ", columnList);
        String values = String.join(", ", valueList);

        return "INSERT INTO " + tableName + " (" + column + ") VALUES (" + values + ")";
    }

    private static String fromValue(Field field, Object value) {
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && tableField.typeHandle() != null) {
            try {
                TypeHandle typeHandle = tableField.typeHandle().getConstructor().newInstance();
                value = typeHandle.to(value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        if (value instanceof LocalDate || value instanceof LocalDateTime) {
            return "'" + value + "'";
        }
        if (value instanceof Timestamp timestamp) {
            return "'" + timestamp.toLocalDateTime().toString() + "'";
        }
        return value.toString();
    }
}
