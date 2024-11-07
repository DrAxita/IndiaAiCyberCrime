package com.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    private static Scanner scanner = new Scanner(System.in);

    public static void main( String[] args ){
    while (true) {
        displayMenu();
        int choice = getUserChoice();
        
        switch (choice) {
            case 1:
                uploadFile();
                break;
            case 2:
                System.out.println("Pre Process - Not implemented yet");
                break;
            case 3:
                System.out.println("Model Selection - Not implemented yet");
                break;
            case 4:
                System.out.println("Train the Model - Not implemented yet");
                break;
            case 5:
                System.out.println("Test the Model - Not implemented yet");
                break;
            case 6:
                System.out.println("Evaluate the Model - Not implemented yet");
                break;
            case 0:
                System.out.println("Exiting the program. Goodbye!");
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
}
private static void displayMenu() {
    System.out.println("\n--- Cyber Crime Analysis Menu ---");
    System.out.println("1. Upload File");
    System.out.println("2. Pre Process");
    System.out.println("3. Model Selection");
    System.out.println("4. Train the Model");
    System.out.println("5. Test the Model");
    System.out.println("6. Evaluate the Model");
    System.out.println("0. Exit");
    System.out.print("Enter your choice: ");
}

private static int getUserChoice() {
    while (!scanner.hasNextInt()) {
        System.out.println("That's not a valid number. Please try again.");
        scanner.next();
    }
    return scanner.nextInt();
}

private static void uploadFile() {
    scanner.nextLine(); // Consume newline
    System.out.print("Enter the path to the CSV file: ");
    String filePath = scanner.nextLine();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line;
        String[] headers = null;
        boolean firstLine = true;

        System.out.println("\nFile contents:");

        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            
            if (firstLine) {
                headers = values;
                firstLine = false;
                continue;
            }

            for (int i = 0; i < values.length; i++) {
                if (i < headers.length) {
                    System.out.println(headers[i] + ": " + values[i]);
                } else {
                    System.out.println("Column " + (i + 1) + ": " + values[i]);
                }
            }
            System.out.println(); // New line between rows
        }
    } catch (IOException e) {
        System.out.println("An error occurred while reading the file: " + e.getMessage());
    }
}
}