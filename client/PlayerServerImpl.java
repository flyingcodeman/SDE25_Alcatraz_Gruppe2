package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.io.Serializable;

import at.falb.games.alcatraz.api.*;
import interfaces.ServerRMIInterface;

interface PlayerServer extends java.rmi.Remote {
    void doMove() throws RemoteException;
}

public class PlayerServerImpl implements MoveListener, PlayerServer, Serializable {
    private static List<Player> allClients = new ArrayList<>();


    // Constructor
    public PlayerServerImpl() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        // Create an instance of NumberServer
        PlayerServer playerServer = new PlayerServerImpl();
        init(args);
    }

    public static void init(String[] args) throws RemoteException {
        Player player;
        Player playerOp;

        Scanner scannerOption = new Scanner(System.in);
        Scanner scannerName = new Scanner(System.in);
        Scanner scannerLobby = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("Start Game: 1");
            //System.out.println("Refresh Player List: 2");
            System.out.println("Register to Game: 3");
            System.out.println("Exit the game: 4\n");

            // Read user input
            int choice = scannerOption.nextInt();

            // Process user input
            switch (choice) {
                case 1:

                    // Test like playerlist came from server
                    player = new Player(Integer.parseInt(args[0]));
                    player.setName("Player_"+args[0]);
                    if(Objects.equals(args[0], "1")){
                        playerOp = new Player(2);
                        playerOp.setName("Player_2");
                    }else{
                        playerOp = new Player(1);
                        playerOp.setName("Player_1");
                    }
                    allClients.add(player);
                    allClients.add(playerOp);
                    // ----------------------------------------------
                    // Take List and create remote object list
                    PlayerServerImpl game = new PlayerServerImpl();
                    initRMI(String.valueOf(player.getId()), allClients);
                    // ----------------------------------------------
                    // Create Alcatraz instances and add move listeners
                    /*if(allClients.size() > 1) {
                        Alcatraz alcatraz = new Alcatraz();
                        alcatraz.init(allClients.size(),player.getId());
                        alcatraz.addMoveListener(game);
                        alcatraz.showWindow();
                        alcatraz.start();
                        System.out.println("Game started");
                    }else{
                        System.out.println("Too few player to start - try later");
                    }*/
                    break;
                case 2:
                    //allClients = refreshRMI(args[0]);
                    //System.out.println("Players in lobby: " + allClients.size());
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


    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
        /*for (Alcatraz a : otherAlcatraz) {
            try {
                a.doMove(a.getPlayer(player.getId()), a.getPrisoner(prisoner.getId()), rowOrCol, row, col);
            }
            catch (IllegalMoveException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void gameWon(Player player) {
        System.out.println("Player " + /*player.getId() +*/ " wins.");
    }

    private static void initRMI(String myId, List<Player> players) throws RemoteException {
        PlayerServer tmpPlayerServer = new PlayerServerImpl();
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
        System.out.println("myID: "+myId);
        registry.rebind("player" + myId, tmpPlayerServer);
        System.out.println("player " + myId + " successfully started.\n");

        // --
        // Look all other clients up
        for(int x=0; x< players.size(); x++) {
            System.out.println("Current Player ID: " + players.get(x).getId() + ", "+x);

            if (players.get(x).getId() == Integer.parseInt(myId)){
                System.out.println("Eigene ID - gescipped");
                continue;
            }

            try {
                PlayerServerImpl playerOp = (PlayerServerImpl) Naming.lookup("rmi://localhost:1099/player" + players.get(x).getId());
                System.out.println("Player_"+players.get(x).getId() + " up and running");
            } catch (NotBoundException e) {
                System.out.println("Player " + players.get(x).getId() + " not running");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void doMove() throws RemoteException {
        System.out.println("Placeholder");
    }
    /*private static List<Player> refreshRMI(String myId, List<Player> tmpPlayerServiceClient) throws RemoteException {
        //int id = Integer.parseInt(myId);
        Player playerServerClient;
        //playerServiceClient.initPlayer("Player_"+myId, id);
        
        for(int x=1; x<5; x++) {
            try {
                playerServerClient = (Player) Naming.lookup("rmi://localhost:1099/NumberService" + x);
                tmpPlayerServiceClient.add(playerServerClient);
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
    }*/
}
