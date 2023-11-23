/*

package at.falb.fh.vtsys.Backup_NumberService;


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
        List<PlayerService> allClients = new ArrayList<>();

        PlayerService playerService = new at.falb.fh.vtsys.PlayerServiceImpl();
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
        registry.rebind("NumberService" + args[0], playerService);
        System.out.println("Player " + args[0] + " successfully started.\n");

        //----
        // Shortened Client logic - see method below
        allClients = refreshRMI(args[0]);
        //----
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("Add a number: 1");
            System.out.println("Refresh Player List: 2");
            System.out.println("Exit the game: 3\n");

            // Read user input
            int choice = scanner.nextInt();

            // Process user input
            switch (choice) {
                case 1:
                    System.out.println("Add number");
                    try {
                        // Get the updated number from the server
                        int updatedNumber = playerService.getNumber();
                        System.out.println("Current number: " + updatedNumber);

                        // Update the shared number
                        for (PlayerService ns : allClients){
                            ns.updateNumber(1);
                        }
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

    private static List<PlayerService> refreshRMI(String myId) {
        int id = Integer.parseInt(myId);
        List<PlayerService> tmpPlayerServiceClient = new ArrayList<>();
        
        for(int x=1; x<5; x++) {
            if (id == x){
                continue;
            }
            try {
                PlayerService playerServiceClient = (PlayerService) Naming.lookup("rmi://localhost:1099/NumberService" + x);
                tmpPlayerServiceClient.add(playerServiceClient);
                System.out.println("Player " + x + " up and running");
            } catch (NotBoundException e) {
                //System.out.println("Number Service " + x + " not running");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return tmpPlayerServiceClient;
    }
}


 */