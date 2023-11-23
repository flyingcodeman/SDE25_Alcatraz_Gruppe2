package server;

import at.falb.fh.vtsys.HelloInterface;
import spread.SpreadException;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;




public class MainTest extends UnicastRemoteObject implements HelloInterface {
    // Constructor
    public MainTest() throws RemoteException {
        super();
    }

    // Remote method
    public String sayHello() throws RemoteException {
        return "Hello from the server!";
    }
	
	public static void main(String[] args) throws UnknownHostException, SpreadException {
		SpreadService spreadService = new SpreadService("service1");
		SpreadService spreadService2 = new SpreadService("service2");
		
		Scanner scanner = new Scanner(System.in);

        try {
            // Create and export the remote object
            MainTest server = new MainTest();

            // Create the RMI registry on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // Bind the remote object to the RMI registry
            registry.rebind("HelloServer", server);

            System.out.println("Server is ready...");
        } catch (Exception e) {
            e.printStackTrace();
        }
		
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
