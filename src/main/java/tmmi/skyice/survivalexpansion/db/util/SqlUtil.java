package tmmi.skyice.survivalexpansion.db.util;

import tmmi.skyice.survivalexpansion.util.StringUtil;

import java.lang.reflect.Field;

public class SqlUtil {
    public static String toSqlSet(Object obj) {
        StringBuilder sb = new StringBuilder();
        Field[] fields = obj.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                if ("id".equals(field.getName().toLowerCase())){
                    continue;
                }
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(obj);

                if (value != null) {
                    sb.append("`").append(StringUtil.camelToUnderline(name)).append("`")
                            .append("=")
                            .append("'").append(value).append("'")
                            .append(", ");
                }
            }

            if (sb.length() > 0) {
                sb.delete(sb.length() - 2, sb.length());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("转换 SQL SET 语句失败", e);
        }

        return sb.toString();
    }
}
