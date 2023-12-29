package com.github.gluhov.util;

import java.util.Scanner;

public class ConsoleUtil {
    public static void writeEmptyLines() {
        for (int i = 0; i < 15; i++) {
            System.out.println();
        }
    }

    public static void printOperationResult(String msg) {
        System.out.println("--- Operation result ---");
        System.out.println(msg);
        System.out.println();

    }

    public static long readLong(Scanner sc, String msg) {
        boolean validInput = false;
        long inputLong = 0;

        while (!validInput) {
            try {
                System.out.print(msg);
                inputLong = sc.nextLong();
                validInput = true;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid long value.");
                sc.nextLine();
            }
        }
        return inputLong;
    }

    public static int readInt(Scanner sc, String msg) {
        boolean validInput = false;
        int inputInt = 0;

        while (!validInput) {
            try {
                System.out.print(msg);
                inputInt = sc.nextInt();
                validInput = true;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid int value.");
                sc.nextLine();
            }
        }
        return inputInt;
    }
}
