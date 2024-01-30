package com.github.gluhov;

import com.github.gluhov.util.DatabaseUtil;
import com.github.gluhov.view.MainView;

public class WriterArchiveApplication {
    public static void main(String[] args) {
        DatabaseUtil.migrateDatabase();
        MainView mainView = new MainView();
        mainView.displayMenu();
    }
}