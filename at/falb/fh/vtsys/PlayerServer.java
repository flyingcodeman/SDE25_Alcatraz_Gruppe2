package at.falb.fh.vtsys;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.IllegalMoveException;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;


public class PlayerServer {
    private List<PlayerService> allClients = new ArrayList<>();

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        // Create an instance of NumberServer
        PlayerServer playerServer = new PlayerServer();
        playerServer.lobby(args);
    }

    private void lobby(String[] args) throws RemoteException {
        PlayerService playerService = new at.falb.fh.vtsys.PlayerServiceImpl();

        /*
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
        playerService.initPlayer("Player_"+args[0], Integer.valueOf(args[0]));
        registry.rebind("NumberService" + args[0], playerService);

        System.out.println(playerService.getPlayerName() + " successfully started.\n");

        //----
        // Shortened Client logic - see method below
        //Todo
        allClients = refreshRMI(args[0]);
        //----
        */

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("Start Game: 1");
            System.out.println("Refresh Player List: 2");
            System.out.println("Exit the game: 3\n");

            // Read user input
            int choice = scanner.nextInt();

            // Process user input
            switch (choice) {
                case 1:
                    // ----------------------------------------------
                    // Create Alcatraz instances and add move listeners
                    if(allClients.size() > 1) {
                        Alcatraz[] alcatrazInstances = new Alcatraz[allClients.size()];
                        Alcatraz alcatraz = new Alcatraz();

                        for (int i = 1; i < allClients.size()+1; i++) {
                            System.out.println("Step 1: "+ playerService.getPlayerId());
                            alcatraz.init(allClients.size(), playerService.getPlayerId());
                            System.out.println("Step 2");
                            alcatraz.addMoveListener(new AlcatrazMoveListener(allClients, alcatraz));
                            alcatrazInstances[i] = alcatraz;
                        }

                        // Link Alcatraz instances together
                        for (int i = 1; i < allClients.size()+1; i++) {
                            for (int j = 0; j < args.length; j++) {
                                if (i != j) {
                                    alcatrazInstances[i].addMoveListener(new AlcatrazMoveListener(allClients, alcatrazInstances[j]));
                                }
                            }
                        }
                        alcatraz.showWindow();
                        alcatraz.start();
                        System.out.println("Game started");
                    }else{
                        System.out.println("Too few player to start - try later");
                    }
                    break;
                case 2:
                    allClients = refreshRMI(args[0]);
                    System.out.println("Players in lobby: " + allClients.size());
                    break;
                case 3:
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0); // Terminate the program
                    break;
                case 4:
                    try {
                        // Lookup the remote object from the RMI registry
                        HelloInterface hello = (HelloInterface) Naming.lookup("rmi://localhost/HelloServer");

                        // Call the remote method
                        String message = hello.sayHello();

                        // Display the result
                        System.out.println("Message from server: " + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid option.");
            }
        }

    }

    class AlcatrazMoveListener implements MoveListener {
        private final List<PlayerService> allClients;
        private final Alcatraz alcatraz;

        public AlcatrazMoveListener(List<PlayerService> allClients, Alcatraz alcatraz) {
            this.allClients = allClients;
            this.alcatraz = alcatraz;
        }

        @Override
        public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
            System.out.println("Moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
            for (PlayerService ns : allClients) {
                try {
                    ns.notifyMove(player.getId(), prisoner.getId(), rowOrCol, row, col);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void gameWon(Player player) {
            System.out.println("Player " + player.getId() + " wins.");
        }
    }

    private static List<PlayerService> refreshRMI(String myId) throws RemoteException {
        int id = Integer.parseInt(myId);
        PlayerService playerServiceClient;
        //playerServiceClient.initPlayer("Player_"+myId, id);

        List<PlayerService> tmpPlayerServiceClient = new ArrayList<>();
        
        for(int x=1; x<5; x++) {
            /*if (id == x){
                continue;
            }*/
            try {
                playerServiceClient = (PlayerService) Naming.lookup("rmi://localhost:1099/NumberService" + x);
                tmpPlayerServiceClient.add(playerServiceClient);

                System.out.println("Player_"+x + " up and running");
            } catch (NotBoundException e) {
                //System.out.println("Player " + x + " not running");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return tmpPlayerServiceClient;
    }
}
