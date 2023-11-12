import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class NumberServer {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        List<NumberService> allClients = new ArrayList<>();
        
        
        System.out.println(args[0]);
        NumberService numberService = new NumberServiceImpl();
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException ex) {
            try {
                registry = LocateRegistry.getRegistry(1099);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        registry.rebind("NumberService" + args[0], numberService);
        System.out.println("Server server started.");

        //----
        // Client logic
        
        allClients = refreshRMI(args[0]);
        //----


        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Exit");

            // Read user input
            int choice = scanner.nextInt();

            // Process user input
            switch (choice) {
                case 1:
                    System.out.println("Add number");

                    try {
                        // Update the shared number
                        for (NumberService ns : allClients){
                            ns.updateNumber(1);
                        }

                        // Get the updated number from the server
                        int updatedNumber = numberService.getNumber();
                        System.out.println("Current number: " + updatedNumber);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                    break;
                case 2:
                    allClients = refreshRMI(args[0]);
                    break;
                case 3:
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0); // Terminate the program
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid option.");
            }
        }

    }

    private static List<NumberService> refreshRMI(String myId) {
        int id = Integer.parseInt(myId);
        List<NumberService> tmpNumberServiceClient = new ArrayList<>();
        
        for(int x=1; x<5; x++) {
            if (id == x){
                continue;
            }
            try {
                NumberService numberServiceClient = (NumberService) Naming.lookup("rmi://localhost:1099/NumberService" + x);
                tmpNumberServiceClient.add(numberServiceClient);
            } catch (NotBoundException e) {
                System.out.println("Number Service " + x + " not running");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return tmpNumberServiceClient;
    }
}
