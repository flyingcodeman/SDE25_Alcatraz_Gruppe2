package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.io.Serializable;

import at.falb.games.alcatraz.api.*;
import interfaces.Constants;
import interfaces.ServerRMIInterface;
import server.AlcatrazPlayer;
import spread.SpreadException;

public class PlayerServerImpl extends UnicastRemoteObject implements Constants, MoveListener, PlayerServer, Serializable {
    // To be filled from the server
    private static List<AlcatrazPlayer> allClients = new ArrayList<>();
    private static Alcatraz alcatraz;

    private static Integer myID;

    // Constructor
    public PlayerServerImpl() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, SpreadException {
        // Todo: Remove after Server connection works

        // Start up the playerm
        PlayerServerImpl game = new PlayerServerImpl();
        game.init();
    }

    public void init() throws SpreadException, RemoteException {
        Player player;

        Scanner scannerOption = new Scanner(System.in);
        Scanner scannerName = new Scanner(System.in);
        Scanner scannerLobby = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("Register to Game: 1");
            System.out.println("Exit the program: 2\n");

            // Read user input
            int choice = scannerOption.nextInt();

            // Process user input
            switch (choice) {
                case 1:
                    try {
                        // Lookup the remote object from the ServerRMI registry
                        ServerRMIInterface serverObject = findAvailableServer();
                        if(serverObject == null){
                            System.exit(0);
                        }

                        while(true){
                            System.out.println("Type in your name to register, or exit to leave:");
                            String playerName = scannerName.nextLine();

                            if(playerName.equalsIgnoreCase("exit")) {
                                System.out.println("Exiting the program. Goodbye!");
                                break;
                            }
                            // Call the remote method
                            int playerId = serverObject.register(playerName, MY_NETWORK);
                            setClientID(playerId);

                            // Display the result
                            if(playerId == -1){
                                System.out.println("Name already exists!");
                            }else{
                                player = new Player(playerId);
                                player.setName(playerName);
                                System.out.println("Message from server: Your ID is " + playerId);

                                // Startup own RMI P2P connection
                                try {
                                    initClientRMI(String.valueOf(myID));
                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }
                                boolean stayInLobby = true;
                                while(stayInLobby){
                                    System.out.println("You are in the lobby.");
                                    System.out.println("Press 1 to leave,");
                                    System.out.println("Press 2 to start the game: ");
                                    int lobbyChoice = scannerLobby.nextInt();

                                    switch (lobbyChoice){
                                        case 1:
                                            serverObject.deRegister(player);
                                            stayInLobby = false;
                                            break;
                                        case 2:
                                            serverObject = findAvailableServer();
                                            if(serverObject == null){
                                                System.exit(0);
                                            }


                                            allClients = serverObject.startGame();
                                            if(allClients == null){
                                                System.out.println("Not enough players in lobby, wait for others!");
                                                //TODO: deregister player or escape untill startGame
                                                break;
                                            }

                                            alcatraz = new Alcatraz();
                                            System.out.println(allClients.size());
                                            alcatraz.init(allClients.size(),myID);
                                            alcatraz.addMoveListener(this);
                                            alcatraz.showWindow();
                                            alcatraz.start();

                                            for(AlcatrazPlayer client : allClients){
                                                if(client.getId() == myID){
                                                    continue;
                                                }
                                                try {
                                                    PlayerServer currentPlayer = getRMIPlayer(client.getId(), client.getPlayerIP());
                                                    currentPlayer.startGame(allClients);
                                                } catch (RemoteException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                            break;
                                        default:
                                            System.out.println("Invalid option. Please choose a valid option.");
                                            break;
                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    break;
                case 2:
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

    public void setClientID(int cID){
        myID = cID;
    }

    private static ServerRMIInterface findAvailableServer() {
        ServerRMIInterface serverObject = null;
        for(int i = 1; i <= 3; i++){
            try{
                serverObject = getServerObject(i);

            }catch (Exception e){
                System.out.println(e);
            }
            if(serverObject != null){
                break;
            }
        }
        return serverObject;
    }

    private static ServerRMIInterface getServerObject(int serverIndex) throws MalformedURLException, NotBoundException, RemoteException {
        return (ServerRMIInterface) Naming.lookup("rmi://"+ MY_NETWORK + ":1098/Server" + serverIndex);
    }

    private static void initClientRMI(String myId) throws RemoteException  {
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

    private static PlayerServer getRMIPlayer(int playerId, String clientIP) throws RemoteException {
        // Check if clients from server list are reachable
        PlayerServer playerOp = null;
            try {
                playerOp = (PlayerServer) Naming.lookup("rmi://"+ clientIP + ":1099/player" + playerId);
                System.out.println("Player_" + playerId + " up and running");

            } catch (NotBoundException e) {
                System.out.println("Player " + playerId + " not reachable");
            } catch (MalformedURLException | RemoteException e) {
                //e.printStackTrace();
            }
        return playerOp;
    }
    @Override
    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));

        for (AlcatrazPlayer aPlayer : allClients) {
            if (aPlayer.getId() == myID){
                continue;
            }

            int retryCounter = 0;
            while(true){
                if(retryCounter == 5){
                    System.exit(0);
                }
                try {
                    PlayerServer playertmp = (PlayerServer) Naming.lookup("rmi://"+ aPlayer.getPlayerIP() + ":1099/player" + aPlayer.getId());
                    System.out.println("Send move to Opponent " + "rmi://"+ aPlayer.getPlayerIP() +":1099/player" + aPlayer.getId());
                    playertmp.sendMove(player, prisoner, rowOrCol, row, col);
                    break;
                } catch (NotBoundException | MalformedURLException | RemoteException e) {
                    System.out.println("Player " + aPlayer.getId() + " not reachable");
                    try {
                        retryCounter += 1;
                        System.out.println("Retrying every 4sec, " + retryCounter + " of 5!");
                        Thread.sleep(4000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (IllegalMoveException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    @Override
    public void startGame(List <AlcatrazPlayer> allClientsFromServer) throws RemoteException {

        allClients = allClientsFromServer;
        alcatraz = new Alcatraz();
        System.out.println(allClients.size());
        alcatraz.init(allClients.size(),myID);
        alcatraz.addMoveListener(this);
        alcatraz.showWindow();
        alcatraz.start();
    }

    @Override
    public void sendMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws IllegalMoveException {
        System.out.println("This method should be called at opponent");

        alcatraz.doMove(player, prisoner, rowOrCol, row, col);
        //System.out.println("Move received: " + player.getName() + " Prisoner: " + prisoner.getId() + " Row/Col:" + rowOrCol + " Row: " + row + " Col:" +col);
    }

    @Override
    public void gameWon(Player player) {
        System.out.println("Player " + player.getId() + " wins.");
    }




}
