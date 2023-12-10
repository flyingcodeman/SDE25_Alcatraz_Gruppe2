package client;

import java.lang.reflect.Field;
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

    protected static AlcatrazPlayer clientPlayer;
    private static Alcatraz alcatraz;

    protected Scanner scannerOption;
    protected Scanner scannerName;
    protected Scanner scannerLobby;

    protected static boolean stayInLobby;
    protected static boolean started;
    protected static boolean stayInRegister;
    protected int lobbyChoice;

    // Constructor
    public PlayerServerImpl() throws RemoteException {
        super();
        stayInLobby = true;
        started = true;
        stayInRegister = true;
        this.scannerOption = new Scanner(System.in);
        this.scannerName = new Scanner(System.in);
        this.scannerLobby = new Scanner(System.in);
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, SpreadException {
        // Todo: Remove after Server connection works

        // Start up the playerm
        PlayerServerImpl game = new PlayerServerImpl();
        game.init();
    }

    public void init() throws SpreadException, RemoteException {

        while (started) {
            System.out.println("Choose an option:");
            System.out.println("Register to Game: 1");
            System.out.println("Exit the program: 2\n");

            // Read user input
            int choice = this.scannerOption.nextInt();

            // Process user input
            switch (choice) {
                case 1:
                    try {
                        // Lookup the remote object from the ServerRMI registry
                        ServerRMIInterface serverObject = findAvailableServer();
                        if(serverObject == null){
                            System.exit(0);
                        }

                        while(stayInRegister){
                            System.out.println("Type in your name to register, or exit to leave:");
                            String playerName = this.scannerName.nextLine();

                            if(playerName.equalsIgnoreCase("exit")) {
                                System.out.println("Exiting the program. Goodbye!");
                                break;
                            }
                            // Call the remote method
                            clientPlayer = serverObject.register(playerName, MY_NETWORK);

                            // Display the result
                            if(clientPlayer == null){
                                System.out.println("Name already exists!");
                            }else{
                                System.out.println("Message from server: "+ clientPlayer.getName() +" successfully registered!");

                                // Startup own RMI P2P connection
                                try {
                                    initClientRMI(clientPlayer.getName());
                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }

                                while(stayInLobby){
                                    System.out.println("You are in the lobby.");
                                    System.out.println("Press 1 to leave,");
                                    System.out.println("Press 2 to start the game: ");
                                    int lobbyChoice = this.scannerLobby.nextInt();

                                    if(stayInLobby) {
                                        switch (lobbyChoice) {
                                            case 1:
                                                serverObject.deRegister(clientPlayer);
                                                clientPlayer = null;
                                                stayInLobby = false;
                                                break;
                                            case 2:
                                                serverObject = findAvailableServer();
                                                if (serverObject == null) {
                                                    System.exit(0);
                                                }


                                                allClients = serverObject.startGame();
                                                if (allClients == null) {
                                                    System.out.println("Not enough players in lobby, wait for others!");
                                                    break;
                                                }

                                                alcatraz = new Alcatraz();
                                                System.out.println(allClients.size());
                                                alcatraz.init(allClients.size(), allClients.indexOf(clientPlayer));
                                                allClients.forEach(aPlayer -> alcatraz.getPlayer(allClients.indexOf(aPlayer)).setName(aPlayer.getName()));
                                                alcatraz.addMoveListener(this);
                                                alcatraz.showWindow();
                                                alcatraz.start();

                                                for (AlcatrazPlayer client : allClients) {
                                                    if (client.getId() == clientPlayer.getId()) {
                                                        continue;
                                                    }
                                                    try {
                                                        PlayerServer currentPlayer = getRMIPlayer(client.getName(), client.getPlayerIP());
                                                        currentPlayer.startGame(allClients);
                                                    } catch (RemoteException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                                stayInLobby = false;
                                                started = false;
                                                stayInRegister = false;
                                                this.scannerName.close();
                                                this.scannerOption.close();
                                                this.scannerLobby.close();

                                                break;
                                            default:
                                                System.out.println("Invalid option. Please choose a valid option.");
                                                break;
                                        }
                                    }else{
                                        started = false;
                                        stayInRegister = false;
                                        this.scannerName.close();
                                        this.scannerOption.close();
                                        this.scannerLobby.close();

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
                    this.scannerName.close();
                    this.scannerOption.close();
                    this.scannerLobby.close();

                    System.exit(0); // Terminate the program
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid option.");
            }
        }
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
        String serverPortName;
        try {
            Field serverPortField = Constants.class.getDeclaredField("SERVER_PORT" + String.valueOf(serverIndex));
            serverPortName = String.valueOf(serverPortField.get(null));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Log registering via this server: rmi://"+ MY_NETWORK + ":" + serverPortName + "/Server" + serverIndex);
        return (ServerRMIInterface) Naming.lookup("rmi://"+ MY_NETWORK + ":" + serverPortName + "/Server" + serverIndex);
    }

    private static void initClientRMI(String playerName) throws RemoteException  {
        PlayerServer tmpPlayerServer = new PlayerServerImpl();
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(Integer.parseInt(CLIENT_PORT));
        } catch (RemoteException ex) {
            try {
                registry = LocateRegistry.getRegistry(Integer.parseInt(CLIENT_PORT));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        registry.rebind("player" + playerName, tmpPlayerServer);
        System.out.println("player " + playerName + " RMI successfully started.\n");
    }

    private static PlayerServer getRMIPlayer(String playerName, String clientIP) throws RemoteException {
        // Check if clients from server list are reachable
        PlayerServer playerOp = null;
            try {
                playerOp = (PlayerServer) Naming.lookup("rmi://"+ clientIP + ":"+ CLIENT_PORT+"/player" + playerName);
                System.out.println("Player_" + playerName + " up and running");

            } catch (NotBoundException e) {
                System.out.println("Player " + playerName + " not reachable");
            } catch (MalformedURLException | RemoteException e) {
                //e.printStackTrace();
            }
        return playerOp;
    }
    @Override
    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));

        for (AlcatrazPlayer aPlayer : allClients) {
            if (aPlayer.getId() == clientPlayer.getId()){
                continue;
            }

            int retryCounter = 0;
            while(true){
                if(retryCounter == 5){
                    System.exit(0);
                }
                try {
                    PlayerServer playertmp = (PlayerServer) Naming.lookup("rmi://"+ aPlayer.getPlayerIP() + ":"+ CLIENT_PORT +"/player" + aPlayer.getName());
                    System.out.println("Send move to Opponent " + "rmi://"+ aPlayer.getPlayerIP() + ":"+ CLIENT_PORT +"/player" + aPlayer.getName());
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
        stayInLobby = false;

        allClients = allClientsFromServer;
        alcatraz = new Alcatraz();
        System.out.println(allClients.size());
        alcatraz.init(allClients.size(),allClients.indexOf(clientPlayer));
        allClients.forEach(aPlayer -> alcatraz.getPlayer(allClients.indexOf(aPlayer)).setName(aPlayer.getName()));
        alcatraz.addMoveListener(this);
        alcatraz.showWindow();
        alcatraz.start();
    }

    @Override
    public void sendMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws IllegalMoveException {
        System.out.println("Doing move from opponent");

        alcatraz.doMove(player, prisoner, rowOrCol, row, col);
        //System.out.println("Move received: " + player.getName() + " Prisoner: " + prisoner.getId() + " Row/Col:" + rowOrCol + " Row: " + row + " Col:" +col);
    }

    @Override
    public void gameWon(Player player) {
        System.out.println("Player " + player.getId() + " wins.");
    }

}
