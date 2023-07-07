package tmmi.skyice.survivalexpansion.db.table;

import tmmi.skyice.survivalexpansion.db.util.DB;
import tmmi.skyice.survivalexpansion.db.util.SqlTemplate;
import tmmi.skyice.survivalexpansion.db.annotation.TableName;

import java.lang.reflect.Field;
import java.util.Optional;

public abstract class ActiveRecordModel<T> implements ActiveRecord<T> {
    private String tableName;

    @Override
    public T init() {
        //noinspection unchecked
        return (T) this;
    }

    @Override
    public T copy() {
        try {
            //noinspection rawtypes
            Class<? extends ActiveRecordModel> clazz = this.getClass();

            Field field = clazz.getDeclaredField("id");
            field.setAccessible(true);

            Object id = field.get(this);

            //noinspection rawtypes
            ActiveRecordModel obj = clazz.getConstructor().newInstance();

            field.set(obj, id);

            //noinspection unchecked
            return (T) obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T selectById() {
        return Optional.ofNullable(fetchId()).map(this::selectById).orElse(null);
    }

    @Override
    public T selectById(long id) {
        //noinspection unchecked
        return DB.executeQuery((Class<T>) this.getClass(), SqlTemplate.selectByIdSql(getTableName(), id));
    }

    @Override
    public T selectByIdOrDefault() {
        return Optional.ofNullable(selectById()).orElseGet(this::init);
    }

    @Override
    public T selectByIdOrDefault(long id) {
        return Optional.ofNullable(selectById(id)).orElseGet(this::init);
    }

    @Override
    public int deleteById() {
        return Optional.ofNullable(fetchId()).map(this::deleteById).orElse(-1);
    }

    @Override
    public int deleteById(long id) {
        return DB.executeUpdate(SqlTemplate.deleteByIdSql(getTableName(), id));
    }

    @Override
    public int insert() {
        return Optional.ofNullable(SqlTemplate.insertSql(getTableName(), this)).map(DB::executeUpdate).orElse(-1);
    }

    @Override
    public int updateById() {
        return Optional.ofNullable(SqlTemplate.updateByIdSql(getTableName(), this)).map(DB::executeUpdate).orElse(-1);
    }

    @Override
    public int insertOrUpdate() {
        return fetchId() == null ? insert() : updateById();
    }

    protected Long fetchId() {
        try {
            Field field = this.getClass().getDeclaredField("id");
            field.setAccessible(true);

            Object value = field.get(this);
            Long id;
            if (value != null) {
                id = Long.parseLong(value.toString());
            } else {
                id = null;
            }
            return id;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTableName() {
        if (this.tableName != null) {
            return this.tableName;
        }
        TableName tableName = this.getClass().getAnnotation(TableName.class);
        if (tableName == null || tableName.value() == null) {
            throw new IllegalStateException(this.getClass().getName() + " is not defined '@TableName'");
        }
        return this.tableName = tableName.value();
    }

}
