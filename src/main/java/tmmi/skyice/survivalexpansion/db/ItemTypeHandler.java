package tmmi.skyice.survivalexpansion.db;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import tmmi.skyice.survivalexpansion.db.table.typehandler.TypeHandle;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemTypeHandler implements TypeHandle<Item> {
    @Override
    public Item parse(ResultSet rs, String columnName, int columnIndex) throws SQLException {
        return Registries.ITEM.get(Identifier.tryParse(rs.getString(columnIndex)));
    }

    @Override
    public String to(Item value) {
        return Registries.ITEM.getId(value).toString();
    }
}
