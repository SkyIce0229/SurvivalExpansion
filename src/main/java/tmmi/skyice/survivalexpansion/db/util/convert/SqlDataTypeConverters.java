package tmmi.skyice.survivalexpansion.db.util.convert;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SqlDataTypeConverters {
    public static final Map<Class<?>, SqlDataTypeConverter<?>> convertorMap = new HashMap<>();
    static {
        convertorMap.put(Timestamp.class, new TimestampSqlDataTypeConverter());
        convertorMap.put(LocalDate.class, new LocalDateSqlDataTypeConverter());
        convertorMap.put(LocalDateTime.class, new LocalDateTimeSqlDataTypeConverter());
    }
    public static Object convert (Object obj,Class<?> targetType) {
        SqlDataTypeConverter<?> convertor = convertorMap.get(targetType);
        if (convertor == null) {
            return obj;
        }
        return convertor.convert(obj);
    }

    public static class TimestampSqlDataTypeConverter implements SqlDataTypeConverter<Timestamp> {
        @Override
        public Timestamp convert(Object value) {
            if (value instanceof Date date) {
                return Timestamp.valueOf(date.toLocalDate().atStartOfDay());
            }
            return null;
        }
    }

    public static class LocalDateTimeSqlDataTypeConverter implements SqlDataTypeConverter<LocalDateTime> {
        @Override
        public LocalDateTime convert(Object value) {
            if (value instanceof Timestamp timestamp) {
                return timestamp.toLocalDateTime();
            }
            if (value instanceof Date date) {
                return date.toLocalDate().atStartOfDay();
            }
            return null;
        }
    }

    public static class LocalDateSqlDataTypeConverter implements SqlDataTypeConverter<LocalDate> {
        @Override
        public LocalDate convert(Object value) {
            if (value instanceof Timestamp timestamp) {
                return timestamp.toLocalDateTime().toLocalDate();
            }
            if (value instanceof Date date) {
                return date.toLocalDate();
            }
            return null;
        }
    }
}
