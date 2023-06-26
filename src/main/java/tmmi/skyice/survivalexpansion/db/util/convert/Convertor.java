package tmmi.skyice.survivalexpansion.db.util.convert;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Convertor {
    public static final Map<Class<?>,DateTypeConvertor<?>> convertorMap = new HashMap<>();
    static {
        convertorMap.put(Timestamp.class,new TimestampConvertor());
    }
    public static Object convert (Object obj,Class<?> targetType) {
        DateTypeConvertor<?> convertor = convertorMap.get(targetType);
        if (convertor == null) {
            return obj;
        }
        return convertor.convert(obj);
    }

    public static class TimestampConvertor implements DateTypeConvertor<Timestamp>{

        @Override
        public Timestamp convert(Object obj) {
            if (obj instanceof Timestamp timestamp){
                return timestamp;
            } else if (obj instanceof LocalDate localDate) {
                return Timestamp.valueOf(LocalDateTime.of(localDate, LocalTime.of(0,0,0)));
            } else if (obj instanceof LocalDateTime localDateTime) {
                return Timestamp.valueOf(localDateTime);
            }
            throw new DateTypeConvertException("Can't convert %s value to TimeStamp".formatted(obj.getClass().getSimpleName()));
        }
    }
}
