package tmmi.skyice.survivalexpansion.db.util.convert;

public interface SqlDataTypeConverter<T>  {
    T convert(Object obj);
}
