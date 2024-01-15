package com.github.gluhov.util;

import liquibase.command.CommandScope;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CommandExecutionException;
import liquibase.exception.LiquibaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/writers";
    private static final String USERNAME = "writers";
    private static final String PASSWORD = "ScjymDL";
    private static final String CHANGELOG_PATH = "db/changelog/db.changelog-master.yaml";

    private static DatabaseUtil instance = null;
    private Connection autoCommitTrueConnection = null;
    private Connection autoCommitFalseConnection = null;

    private DatabaseUtil() {
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
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
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

    public void migrateDatabase() throws LiquibaseException, SQLException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(getConnection(true)));
        update(database);
    }

    private void update(Database database) throws CommandExecutionException {
        new CommandScope("update")
                .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database)
                .addArgumentValue("changeLogFile", CHANGELOG_PATH)
                .execute();
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