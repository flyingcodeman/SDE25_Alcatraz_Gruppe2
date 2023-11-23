package server;

import spread.SpreadException;

import java.net.UnknownHostException;
import java.util.Scanner;


public class MainTest{
	
	
	public static void main(String[] args) throws UnknownHostException, SpreadException {
		SpreadService spreadService = new SpreadService("service1");
		SpreadService spreadService2 = new SpreadService("service2");
		
		Scanner scanner = new Scanner(System.in);
		
        // Main loop
        while (true) {
            System.out.print("Enter a string (type 'exit' to terminate): ");
            String userInput = scanner.nextLine();

            // Check for exit condition
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the program. Goodbye!");
                break;
            }

            // Process the user input (you can add your logic here)
            System.out.println("You entered: " + userInput);
            CustomMsg cMsg = new CustomMsg();
            cMsg.setData(userInput);
            
            spreadService.sendMsg(cMsg);
            spreadService2.sendMsg(cMsg);
        }

        // Close the scanner
        scanner.close();
		
		
	}

}
