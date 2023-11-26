package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;
import interfaces.ServerRMIInterface;


public class PlayerServer {
    private List<Player> allClients = new ArrayList<>();

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        // Create an instance of NumberServer
        PlayerServer playerServer = new PlayerServer();
        playerServer.init(args);
    }

    private void init(String[] args) throws RemoteException {
       // PlayerService playerService = new PlayerServiceImpl();
        Player player;

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

        Scanner scannerOption = new Scanner(System.in);
        Scanner scannerName = new Scanner(System.in);
        Scanner scannerLobby = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("Start Game: 1");
            System.out.println("Refresh Player List: 2");
            System.out.println("Register to Game: 3");
            System.out.println("Exit the game: 4\n");

            // Read user input
            int choice = scannerOption.nextInt();

            // Process user input
            switch (choice) {
                case 1:
                    // ----------------------------------------------
                    // Create Alcatraz instances and add move listeners
                   /* if(allClients.size() > 1) {
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
                    }*/
                    break;
                case 2:
                    allClients = refreshRMI(args[0]);
                    System.out.println("Players in lobby: " + allClients.size());
                    break;
                case 3:

                    try {
                        // Lookup the remote object from the ServerRMI registry
                        ServerRMIInterface serverObject = (ServerRMIInterface) Naming.lookup("rmi://localhost:1099/Server1");

                        while(true){
                            System.out.println("Type in your name to register, or exit to leave:");
                            String playerName = scannerName.nextLine();

                            if(playerName.equalsIgnoreCase("exit")) {
                                System.out.println("Exiting the program. Goodbye!");
                                break;
                            }
                            // Call the remote method
                            int playerId = serverObject.register(playerName);

                            // Display the result
                            if(playerId == -1){
                                System.out.println("Name already exists!");
                            }else{
                                player = new Player(playerId);
                                player.setName(playerName);
                                System.out.println("Message from server: Your ID is " + playerId);

                                    System.out.println("You are in the lobby.");
                                    System.out.println("Press 1 to leave,");
                                    System.out.println("Press 2 to start the game: ");
                                    int lobbyChoice = scannerLobby.nextInt();

                                    switch (lobbyChoice){
                                        case 1:
                                            serverObject.deRegister(player);
                                            break;
                                        case 2:
                                            List<Player> playerList = serverObject.startGame();
                                            for(Player tempPlayer : playerList){
                                                System.out.println(tempPlayer.getName());
                                            }
                                            break;
                                        default:
                                            System.out.println("Invalid option. Please choose a valid option.");
                                            break;
                                    }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("Exiting the program. Goodbye!");
                    scannerName.close();
                    scannerOption.close();
                    scannerLobby.close();

                    System.exit(0); // Terminate the program
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

    private static List<Player> refreshRMI(String myId) throws RemoteException {
        int id = Integer.parseInt(myId);
        Player playerServiceClient;
        //playerServiceClient.initPlayer("Player_"+myId, id);

        List<Player> tmpPlayerServiceClient = new ArrayList<>();
        
        for(int x=1; x<5; x++) {
            /*if (id == x){
                continue;
            }*/
            try {
                playerServiceClient = (Player) Naming.lookup("rmi://localhost:1099/NumberService" + x);
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
