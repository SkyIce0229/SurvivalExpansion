package tmmi.skyice.survivalexpansion.db.util;

import tmmi.skyice.survivalexpansion.db.annotation.TableField;
import tmmi.skyice.survivalexpansion.db.table.ActiveRecord;
import tmmi.skyice.survivalexpansion.db.table.typehandler.TypeHandle;
import tmmi.skyice.survivalexpansion.db.util.convert.SqlDataTypeConverters;
import tmmi.skyice.survivalexpansion.util.LogUtil;
import tmmi.skyice.survivalexpansion.util.StringUtil;


import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class DB {
    static {
        String mysqlDriver = "com.mysql.cj.jdbc.Driver";
        try {
            //加载MySql驱动
            Class.forName(mysqlDriver);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final DBConnectionPool connectionPool = new DBConnectionPool();
    //连接链接池用这个方法
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> isTransaction = new ThreadLocal<>();

    public static Connection getConnection() {
        Connection conn = connectionHolder.get();
        LogUtil.debug("get connectionHolder：{}", System.identityHashCode(conn));
        if (conn == null) {
            try {
                conn = connectionPool.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException("获取连接失败", e);
            }
            connectionHolder.set(conn);
        }
        return conn;
    }

    public static void closeConnection() {
        Connection conn = connectionHolder.get();
        LogUtil.debug("close connectionHolder：{}", System.identityHashCode(conn));
        if (conn != null) {
            connectionHolder.remove();
            connectionPool.releaseConnection(conn);
        }
    }

    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (conn != null) {
            connectionPool.releaseConnection(conn);
        }
    }

    public static boolean isTransaction() {
        Boolean flag = isTransaction.get();
        return flag != null && flag;
    }

    public static void setIsTransaction(boolean flag) {
        if (!flag) {
            isTransaction.remove();
            return;
        }
        isTransaction.set(true);
    }

    public static void executeTransaction(Runnable runnable) {
        beginTransaction();
        try {
            runnable.run();
            commitTransaction();
        } catch (Throwable e) {
            rollbackTransaction();
            throw e;
        } finally {
            closeConnection();
        }
    }

    public static void beginTransaction() {
        try {
            Connection connection = getConnection();
            LogUtil.debug("beginTransaction connectionId:{} AutoCommit:{}", System.identityHashCode(connection), connection.getAutoCommit());
            connection.setAutoCommit(false);
            setIsTransaction(true);
        } catch (SQLException e) {
            throw new RuntimeException("事务开启失败", e);
        }
    }

    public static void commitTransaction() {
        try {
            Connection connection = getConnection();
            LogUtil.debug("commitTransaction connectionId:{} AutoCommit:{}", System.identityHashCode(connection), connection.getAutoCommit());
            connection.commit();
            connection.setAutoCommit(true);
            setIsTransaction(false);
        } catch (SQLException e) {
            throw new RuntimeException("事务提交失败", e);
        }
    }

    public static void rollbackTransaction() {
        try {
            Connection connection = getConnection();
            LogUtil.debug("rollbackTransaction connectionId:{} AutoCommit:{}", System.identityHashCode(connection), connection.getAutoCommit());
            connection.rollback();
            connection.setAutoCommit(true);
            setIsTransaction(false);
        } catch (SQLException e) {
            throw new RuntimeException("事务回滚失败", e);
        }
    }

    public static <T> T executeQuery(Class<T> clazz, String sql, Object... args) {
        List<T> result = executeQueryList(clazz, sql, args);
        return result == null || result.isEmpty() ? null : result.get(0);
    }

    public static <T> T executeQueryOrDefault(Class<T> clazz, String sql, Object... args) {
        List<T> ts = executeQueryList(clazz, sql, args);
        if (ts != null && !ts.isEmpty()) {
            return ts.get(0);
        }
        if (!ActiveRecord.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz.getName() + " is not implemented DefaultInstance interface");
        }
        try {
            //noinspection rawtypes
            ActiveRecord t = (ActiveRecord) clazz.getConstructor().newInstance();

            //noinspection unchecked
            return (T) t.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> executeQueryList(Class<T> clazz, String sql, Object... args) {
        LogUtil.debug("execute sql : {}", sql);
        LogUtil.debug("sql parameters : {}", args);
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            //不用外场变量，这个效率比for循环高
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            ResultSet rs = stmt.executeQuery();
            return resultSetToBean(clazz, rs);
        } catch (SQLException e) {
            throw new RuntimeException("SQL执行失败", e);
        } finally {
            if (!isTransaction()) {
                closeConnection();
            }
        }
    }

    private static <T> List<T> resultSetToBean(Class<T> clazz, ResultSet rs) {
        List<T> result = new ArrayList<>();
        try (rs) {

            while (rs.next()) {
                T obj = clazz.getDeclaredConstructor().newInstance();
                ResultSetMetaData metadata = rs.getMetaData();
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    String columnName = metadata.getColumnName(i);
                    Object value = rs.getObject(i);
                    Field field;
                    try {
                        field = clazz.getDeclaredField(StringUtil.underlineToCamel(columnName));
                    } catch (NoSuchFieldException e) {
                        // 忽略不存在的属性映射
                        continue;
                    }
                    TableField tableField = field.getAnnotation(TableField.class);
                    if (tableField != null && tableField.typeHandle() != null) {
                        TypeHandle<?> typeHandle = tableField.typeHandle().getConstructor().newInstance();
                        value = typeHandle.parse(rs, columnName, i);
                    }
                    //转换器
                    if (!field.getType().isAssignableFrom(value.getClass())) {
                        value = SqlDataTypeConverters.convert(value, field.getType());
                    }
                    field.setAccessible(true);
                    field.set(obj, value);
                }
                result.add(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException("ResultSetToBean映射失败", e);
        }
        return result.isEmpty() ? null : result;
    }

    public static int executeUpdate(String sql, Object... args) {
        LogUtil.debug("execute sql : {}", sql);
        LogUtil.debug("sql parameters : {}", args);
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            //不用共享变量，这个效率比for循环高
            IntStream.range(0, args.length).forEach(i -> {
                try {
                    stmt.setObject(i + 1, args[i]);
                } catch (SQLException e) {
                    throw new RuntimeException("sql预编译失败", e);
                }
            });
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("sql执行失败", e);
        } finally {
            if (!isTransaction()) {
                closeConnection();
            }
        }
    }
}
