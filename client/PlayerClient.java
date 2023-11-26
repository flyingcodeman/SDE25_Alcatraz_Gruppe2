package client;

import java.rmi.Naming;
import java.util.Scanner;

public class PlayerClient {
    public static void main(String[] args) {
        try {
            PlayerService playerService1 = (PlayerService) Naming.lookup("rmi://localhost:1099/NumberService1");
            PlayerService playerService2 = (PlayerService) Naming.lookup("rmi://localhost:1099/NumberService2");
            Scanner scanner = new Scanner(System.in);

            while (true) {
                // Get input from the user
                System.out.print("Enter a number (or 'exit' to quit): ");
                String userInput = scanner.nextLine();

                // Check if the user wants to exit
                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }

                try {
                    int number = Integer.parseInt(userInput);

                    // Update the shared number
                    //playerService1.updateNumber(number);
                    //playerService2.updateNumber(number);

                    // Get the updated number from the server
                    //int updatedNumber = numberService.getNumber();
                    //System.out.println("Current number: " + updatedNumber);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

            // Close the scanner
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
