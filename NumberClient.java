import java.rmi.Naming;
import java.util.Scanner;

public class NumberClient {
    public static void main(String[] args) {
        try {
            NumberService numberService1 = (NumberService) Naming.lookup("rmi://localhost:1099/NumberService1");
            NumberService numberService2 = (NumberService) Naming.lookup("rmi://localhost:1099/NumberService2");
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
                    numberService1.updateNumber(number);
                    numberService2.updateNumber(number);

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
