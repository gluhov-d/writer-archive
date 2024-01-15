package com.github.gluhov.view;

import com.github.gluhov.controller.LabelController;
import com.github.gluhov.controller.PostController;
import com.github.gluhov.controller.WriterController;
import com.github.gluhov.repository.LabelRepository;
import com.github.gluhov.repository.PostRepository;
import com.github.gluhov.repository.WriterRepository;
import com.github.gluhov.repository.jdbc.JdbcLabelRepositoryImpl;
import com.github.gluhov.repository.jdbc.JdbcPostRepositoryImpl;
import com.github.gluhov.repository.jdbc.JdbcWriterRepositoryImpl;
import com.github.gluhov.service.LabelService;
import com.github.gluhov.service.PostService;
import com.github.gluhov.service.WriterService;
import com.github.gluhov.util.ConsoleUtil;
import com.github.gluhov.util.DatabaseUtil;

import java.io.IOException;
import java.util.Scanner;

public class MainView {
    private final WriterController writerController;
    private final PostController postController;
    private final LabelController labelController;
    public MainView() {
        WriterRepository writerRepository = new JdbcWriterRepositoryImpl();
        PostRepository postRepository = new JdbcPostRepositoryImpl();
        LabelRepository labelRepository = new JdbcLabelRepositoryImpl();
        LabelService labelService = new LabelService(labelRepository);
        PostService postService = new PostService(postRepository, labelRepository);
        WriterService writerService = new WriterService(writerRepository, postRepository);
        this.writerController = new WriterController(writerService, postService);
        this.postController = new PostController(postService);
        this.labelController = new LabelController(labelService);
    }

    public void displayMenu() {
        try (Scanner sc = new Scanner(System.in)){
            WriterView writerView = new WriterView(sc, writerController);
            PostView postView = new PostView(sc, postController);
            LabelView labelView = new LabelView(sc, labelController);
            ConsoleUtil.writeEmptyLines();
            while (true) {
                System.out.println("--- Writer keeper menu ---");
                System.out.println("1. Writers menu");
                System.out.println("2. Posts menu");
                System.out.println("3. Labels menu");
                System.out.println("6. Exit");
                int choice = ConsoleUtil.readInt(sc, "Choose an option: ");
                ConsoleUtil.writeEmptyLines();
                switch (choice) {
                    case 1 -> writerView.displayMenu();
                    case 2 -> postView.displayMenu();
                    case 3 -> labelView.displayMenu();
                    case 6 -> {
                        System.out.println("Exiting application...");
                        DatabaseUtil.getInstance().closeConnections();
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (IOException ex) {
            System.out.println("IO Exception. Try again.");
        }
    }
}
