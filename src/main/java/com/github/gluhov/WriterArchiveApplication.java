package com.github.gluhov;

import com.github.gluhov.util.DatabaseUtil;
import com.github.gluhov.view.MainView;

import java.sql.SQLException;

public class WriterArchiveApplication {
    public static void main(String[] args) {
        try {
            DatabaseUtil.getInstance().migrateDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        MainView mainView = new MainView();
        mainView.displayMenu();
    }
}