package com.github.gluhov.view;

import com.github.gluhov.controller.WriterController;
import com.github.gluhov.model.Writer;
import com.github.gluhov.util.ConsoleUtil;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@RequiredArgsConstructor
public class WriterView {
    private final Scanner sc;
    private final WriterController writerController;

    public void displayMenu() throws IOException {
            while(true) {
                System.out.println("--- Writer menu ---");
                System.out.println("1. View writer with posts");
                System.out.println("2. Create writer");
                System.out.println("3. Update writer");
                System.out.println("4. Delete writer");
                System.out.println("5. Find all writers");
                System.out.println("6. Main menu");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                ConsoleUtil.writeEmptyLines();
                switch (choice) {
                    case 1 -> view();
                    case 2 -> create();
                    case 3 -> update();
                    case 4 -> delete();
                    case 5 -> findAll();
                    case 6 -> {
                        System.out.println("Returning to main menu");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }

    }

    private void findAll() {
        Optional<List<Writer>> writers = writerController.findAll();
        ConsoleUtil.printOperationResult("Available active writers: ");
        if (writers.isEmpty()) {
            System.out.println("No posts available");
        } else {
            for (Writer w : writers.get()) {
                System.out.println(w);
            }
        }
        System.out.println();
    }

    private void delete() {
        Long deleteId = ConsoleUtil.readLong(sc, "Writer id: ");
        if (writerController.checkIfWriterExists(deleteId)) {
            writerController.delete(deleteId);
            ConsoleUtil.printOperationResult("Writer deleted");
        } else {
            ConsoleUtil.printOperationResult("No such writer, try again");
        }
    }

    private void update() {
        Long updatedWriterId = ConsoleUtil.readLong(sc, "Id: ");
        Optional<Writer> updatedWriter = writerController.get(updatedWriterId);
        if (updatedWriter.isPresent()) {
            System.out.print("First name: ");
            String updatedFirstName = sc.next();
            System.out.print("Last name: ");
            String updatedLastName = sc.next();
            List<Long> updatedPosts = readAllPostsIds();
            updatedWriter.get().setFirstName(updatedFirstName);
            updatedWriter.get().setLastName(updatedLastName);
            Optional<Writer> writer = writerController.updateWithPosts(updatedWriter.get(), updatedPosts);
            ConsoleUtil.printOperationResult(writer.isPresent()?writer.get().toString():"No writer with such id");
        } else {
            ConsoleUtil.printOperationResult("No writer with such id");
        }
        System.out.println();
    }

    private void create() {
        System.out.print("First name:");
        String firstName = sc.next();
        System.out.print("Last name:");
        String lastName = sc.next();
        List<Long> posts = readAllPostsIds();
        ConsoleUtil.writeEmptyLines();
        Writer createdWriter = Writer.builder().firstName(firstName).lastName(lastName).build();
        Optional<Writer> writer = writerController.saveWithPosts(createdWriter, posts);
        ConsoleUtil.printOperationResult(writer.isPresent()?writer.get().toString():"Can not create writer.");
    }

    private void view() {
        Long id = ConsoleUtil.readLong(sc,"Id: ");
        Optional<Writer> writer = writerController.get(id);
        ConsoleUtil.writeEmptyLines();
        ConsoleUtil.printOperationResult(writer.isPresent()?writer.get().toString():"No writer with such id");
    }

    private List<Long> readAllPostsIds() {
        List<Long> posts = new ArrayList<>();
        System.out.println("Posts id (one on the line) or type '-1' to Exit:");
        while(true) {
            Long postId = ConsoleUtil.readLong(sc, "Post id: ");
            if (postId == -1) {
                break;
            }
            if (writerController.checkPostStatus(postId)) {
                posts.add(postId);
            } else {
                ConsoleUtil.printOperationResult("No post with such id");
            }
        }
        return posts;
    }
}
