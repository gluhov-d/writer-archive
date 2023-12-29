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

    public static void migrateDatabase() throws LiquibaseException, SQLException {
        try (Connection connection = getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            update(database);
        }
    }

    private static void update(Database database) throws CommandExecutionException {
        new CommandScope("update")
                .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database)
                .addArgumentValue("changeLogFile", CHANGELOG_PATH)
                .execute();
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
}