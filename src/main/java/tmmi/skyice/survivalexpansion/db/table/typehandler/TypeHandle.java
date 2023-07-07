package tmmi.skyice.survivalexpansion.db.table.typehandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeHandle<T> {
    T parse(ResultSet rs ,String columnName, int columnIndex) throws SQLException;

    String to(T value);
}
