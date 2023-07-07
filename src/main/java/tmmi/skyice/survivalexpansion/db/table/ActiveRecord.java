package tmmi.skyice.survivalexpansion.db.table;

public interface ActiveRecord<T> {
    T init();
    T copy();

    T selectById();

    T selectById(long id);

    T selectByIdOrDefault();

    T selectByIdOrDefault(long id);

    int deleteById();

    int deleteById(long id);

    int insert();

    int updateById();

    int insertOrUpdate();
}
