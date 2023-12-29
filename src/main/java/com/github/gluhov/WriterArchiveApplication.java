package com.github.gluhov;

import com.github.gluhov.util.DatabaseUtil;
import liquibase.exception.LiquibaseException;

import java.sql.SQLException;

public class WriterArchiveApplication {
    public static void main(String[] args) {
        try {
            DatabaseUtil.migrateDatabase();
        } catch (LiquibaseException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}