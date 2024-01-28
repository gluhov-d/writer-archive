package com.github.gluhov.util;

import com.github.gluhov.config.FlywayConfig;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static final String FLYWAY_PATH = "db/flyway.properties";
    private static DatabaseUtil instance = null;
    private static Properties properties = null;
    private Connection autoCommitTrueConnection = null;
    private Connection autoCommitFalseConnection = null;

    private DatabaseUtil() {
        FlywayConfig flywayConfig = new FlywayConfig(FLYWAY_PATH);
        properties = flywayConfig.getProperties();
        initConnections();
    }

    private void initConnections() {
        try {
            if (autoCommitTrueConnection == null || autoCommitTrueConnection.isClosed()) {
                autoCommitTrueConnection = createConnection(true);
                autoCommitTrueConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            }
            if (autoCommitFalseConnection == null || autoCommitFalseConnection.isClosed()) {
                autoCommitFalseConnection = createConnection(false);
                autoCommitFalseConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database connections", e);
        }
    }

    private Connection createConnection(boolean autoCommit) throws SQLException {
        Connection connection = DriverManager.getConnection(properties.getProperty("flyway.url"),
                properties.getProperty("flyway.user"),
                properties.getProperty("flyway.password"));
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    public static synchronized DatabaseUtil getInstance() {
        if (instance == null) {
            instance = new DatabaseUtil();
        }
        return instance;
    }

    public Connection getConnection(boolean autoCommit) {
        initConnections();
        return autoCommit ? autoCommitTrueConnection : autoCommitFalseConnection;
    }

    public void migrateDatabase() throws SQLException {
        Flyway flyway = Flyway.configure()
                .dataSource(properties.getProperty("flyway.url"),
                        properties.getProperty("flyway.user"),
                        properties.getProperty("flyway.password"))
                .locations(properties.getProperty("flyway.locations"))
                .load();

        flyway.migrate();
    }

    public void closeConnections() {
        try {
            if (autoCommitTrueConnection != null && !autoCommitTrueConnection.isClosed()) {
                autoCommitTrueConnection.close();
            }
            if (autoCommitFalseConnection != null && !autoCommitFalseConnection.isClosed()) {
                autoCommitFalseConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}