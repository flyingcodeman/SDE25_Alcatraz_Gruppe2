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
    void sendMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws IllegalMoveException;
}

public class PlayerServerImpl implements MoveListener, PlayerServer, Serializable {
    // To be filled from the server
    private static final List<Player> allClients = new ArrayList<>();
    private static Alcatraz alcatraz = new Alcatraz();

    private static Integer myID;

    // Constructor
    public PlayerServerImpl() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        // Todo: Remove after Server connection works
        myID = Integer.parseInt(args[0]);
        // Start up the player
        init(args);
    }

    public static void init(String[] args) throws RemoteException {
        Player player;
        Player playerOp;

        // Startup own RMI P2P connection
        // Todo: To be switched after registration at server!
        initClientRMI(String.valueOf(args[0]));

        Scanner scannerOption = new Scanner(System.in);
        Scanner scannerName = new Scanner(System.in);
        Scanner scannerLobby = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("Register to Game: 1");
            System.out.println("Start Game: 2");
            System.out.println("Exit the program: 3\n");

            // Read user input
            int choice = scannerOption.nextInt();

            // Process user input
            switch (choice) {
                case 1:
                    try {
                        // Lookup the remote object from the ServerRMI registry
                        ServerRMIInterface serverObject = (ServerRMIInterface) Naming.lookup("rmi://localhost:1098/Server1");

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
                                myID = playerId;

                                // Startup own RMI P2P connection
                                initClientRMI(String.valueOf(player.getId()));

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
                        //e.printStackTrace();
                    }
                    break;

                case 2:
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
                    checkOtherPlayersRMI(String.valueOf(player.getId()));
                    // ----------------------------------------------
                    // Create Alcatraz instances and add move listeners
                    if(allClients.size() > 1) {
                        alcatraz = new Alcatraz();
                        System.out.println(allClients.size());
                        alcatraz.init(allClients.size(),player.getId());
                        alcatraz.addMoveListener(game);
                        alcatraz.showWindow();
                        alcatraz.start();
                        System.out.println("Game started");
                    }else{
                        System.out.println("Too few player to start - try later");
                    }
                    break;

                case 3:
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

    private static void initClientRMI(String myId) throws RemoteException {
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
    }

    private static void checkOtherPlayersRMI(String myId) throws RemoteException {
        // Check if clients from server list are reachable
        for (Player player : PlayerServerImpl.allClients) {
            // SKip own ID
            if (player.getId() == Integer.parseInt(myId)) { continue; }

            try {
                PlayerServerImpl playerOp = (PlayerServerImpl) Naming.lookup("rmi://localhost:1099/player" + player.getId());
                System.out.println("Player_" + player.getId() + " up and running");
            } catch (NotBoundException e) {
                System.out.println("Player " + player.getId() + " not reachable");
            } catch (MalformedURLException | RemoteException e) {
                //e.printStackTrace();
            }
        }
    }
    @Override
    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));

        for (Player p : allClients) {
            if (p.getId() == myID){
                continue;
            }
            try {
                // Show own move in gui
                alcatraz.doMove(p, prisoner, rowOrCol, row, col);
                // Send move to other players
                PlayerServer playertmp = (PlayerServer) Naming.lookup("rmi://localhost:1099/player" + p.getId());
                System.out.println("Send move to Opponent " + "rmi://localhost:1099/player" + p.getId());
                playertmp.sendMove(player, prisoner, rowOrCol, row, col);

            } catch (NotBoundException e) {
                System.out.println("Player " + p.getId() + " not reachable");
            } catch (MalformedURLException | RemoteException e) {
                //e.printStackTrace();
            } catch (IllegalMoveException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws IllegalMoveException {
        System.out.println("This method should be called at opponent");
        alcatraz.doMove(player, prisoner, rowOrCol, row, col);
        //System.out.println("Move received: " + player.getName() + " Prisoner: " + prisoner.getId() + " Row/Col:" + rowOrCol + " Row: " + row + " Col:" +col);
    }

    @Override
    public void gameWon(Player player) {
        System.out.println("Player " + /*player.getId() +*/ " wins.");
    }




}
