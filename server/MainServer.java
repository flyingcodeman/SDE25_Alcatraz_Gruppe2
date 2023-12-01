package server;

import at.falb.games.alcatraz.api.Player;
import client.PlayerServerImpl;
import interfaces.ServerRMIInterface;
import spread.SpreadException;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import server.ServerRMI;

import static java.lang.Integer.parseInt;


public class MainServer extends UnicastRemoteObject implements ServerRMIInterface {
    ServerRMI serverRMI = new ServerRMI();
    static SpreadService spreadService;

    static boolean gameStarted;

    //public static List<Player> players = new ArrayList<>();
    public static Lobby<Player> players = new Lobby<Player>();

    // Constructor
    public MainServer() throws RemoteException {
        super();
    }
    @Override
    public int register(String name) throws RemoteException, SpreadException {
        spreadService.registerPlayer(name);

        Player newPlayer = registerPlayer(name);
        if (newPlayer == null) return -1;

        return newPlayer.getId();
    }

    public static Player registerPlayer(String name) {
        /*int highestId = 0;
        for(Player player : players){
            if(player.getId() > highestId){
                highestId = player.getId();
            }
            if (name.equals(player.getName())) {
                System.out.println("Value exists in the object list!");
                return null;
            }
        }*/

        Player newPlayer = new Player(players.size());
        newPlayer.setName(name);
        players.add(newPlayer);
        System.out.println("Log players in register method: " + players);
        return newPlayer;
    }


    @Override
    public boolean deRegister(Player player) throws RemoteException, SpreadException {
        spreadService.deRegisterPlayer(player);

        return deRegisterPlayer(player);
    }

    public static boolean deRegisterPlayer(Player removePlayer) {
        for(Player player : players){
            if (player.equals(removePlayer)) {
                players.remove(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Player> startGame() throws RemoteException, SpreadException {
        if(MainServer.players.size() >= 2 && !gameStarted){
            spreadService.startGame();
            gameStarted = true;
            return players;
        }else{
            return null;
        }
    }

    public static void main(String[] args) throws UnknownHostException, SpreadException {
		spreadService = new SpreadService("service"+args[0]);
        int serverRMIPort = 1098;

        try {
            // Create and export the remote object
            MainServer server = new MainServer();

            // Create the RMI registry on port 1099
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(serverRMIPort);
            } catch (RemoteException ex) {
                try {
                    registry = LocateRegistry.getRegistry(serverRMIPort);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            // Bind the remote object to the RMI registry
            registry.rebind("Server"+args[0], server);

            System.out.println("Server" + args[0] + " is ready...");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
