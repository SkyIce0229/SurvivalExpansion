package tmmi.skyice.survivalexpansion.db.util;

import tmmi.skyice.survivalexpansion.util.LogUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

import static tmmi.skyice.survivalexpansion.SurvivalExpansionMod.survivalExpansionToml;

public class DBConnectionPool {
    //数据库连接池
    private Deque<Connection> connections;
    int maxSize;

    public DBConnectionPool() {
        this(10);
    }
    public DBConnectionPool(int maxSize) {
        this.maxSize = maxSize;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connections == null) {
            connections = new ArrayDeque<>();
            for (int i = 0; i < maxSize; i++) {
                Connection connection = createConnection();
                if (isValid(connection)){//检查连接状态
                    //判断拿到的链接对象是否有效，有效就添加到池中
                    connections.addLast(connection);
                }
            }
        }
        while (connections.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        StringBuilder sb = new StringBuilder();
        connections.forEach(connection -> sb.append(System.identityHashCode(connection)).append(", "));
        LogUtil.debug("connectionPoll：[{}]",sb.toString());
        Connection connection = connections.pollFirst();
        if (!isValid(connection)) {
            connection = createConnection();
        }
        return connection;
    }
    public synchronized void releaseConnection(Connection connection) {
        connections.addLast(connection);
        StringBuilder sb = new StringBuilder();
        connections.forEach(conn -> sb.append(System.identityHashCode(conn)).append(", "));
        LogUtil.debug("connectionPoll：[{}]",sb.toString());
        //唤醒等待的线程
        notifyAll();
    }
    public synchronized void closeAllConnections() throws SQLException {
        for (Connection connection : connections) {
            connection.close();
        }
    }
    private Connection createConnection() throws SQLException {
        String databaseName = survivalExpansionToml.getDatabase().getDatabaseName();
        String url = "jdbc:mysql://"+survivalExpansionToml.getDatabase().getIp()+":"+survivalExpansionToml.getDatabase().getPort()+"/";
        return DriverManager.getConnection(url+databaseName,survivalExpansionToml.getDatabase().getUsername(),survivalExpansionToml.getDatabase().getPassword());

    }
    private boolean isValid(Connection connection) throws SQLException {
            return connection != null && !connection.isClosed() && connection.isValid(3000);

    }

}
