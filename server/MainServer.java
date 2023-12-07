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
import java.util.*;

import static interfaces.Constants.MY_NETWORK;
import static interfaces.Constants.SERVER_PORT;
import static java.lang.Integer.parseInt;


public class MainServer  {
    static SpreadService spreadService;

    static boolean gameStarted;

    public static Lobby<AlcatrazPlayer> players = new Lobby<AlcatrazPlayer>();

    public MainServer() throws RemoteException {
        super();
    }

    public static AlcatrazPlayer registerPlayer(String name, String networkIP) {
        List<Integer> usedIDS = new ArrayList<>();

        for(AlcatrazPlayer player : players){
            if (Objects.equals(player.getName(), name)) {
                System.out.println("Log player name " + player.getName() + " already exists!");
                return null;
            }
            usedIDS.add(player.getId());
        }

        int smallestFreeID = 0;
        while(true){
            if(!usedIDS.contains(smallestFreeID)){
                break;
            }
            smallestFreeID++;
        }

        AlcatrazPlayer newPlayer = new AlcatrazPlayer(smallestFreeID, networkIP);
        newPlayer.setName(name);
        players.add(newPlayer);
        System.out.println("Log players in register method: " + players);
        return newPlayer;
    }

    public static boolean deRegisterPlayer(AlcatrazPlayer removePlayer) {
        for(AlcatrazPlayer player : players){
            if (player.equals(removePlayer)) {
                players.remove(player);
                System.out.println("Log players removed: " + player.getName());
                System.out.println("Log players in register method: " + players);
                return true;
            }
        }
        System.out.println("Log player NOT removed!");
        return false;
    }

    public static boolean setGameStart() {
        if(MainServer.players.size() >= 2 && !MainServer.gameStarted){
            MainServer.gameStarted = true;
            return true;
        }else{
            return false;
        }
    }

    public static void main(String[] args) throws UnknownHostException, SpreadException {
		spreadService = new SpreadService("service"+args[0]);

        try {
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(Integer.parseInt(SERVER_PORT));
            } catch (RemoteException ex) {
                try {
                    registry = LocateRegistry.getRegistry(Integer.parseInt(SERVER_PORT));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            ServerRMIInterface server = new ServerRMI();

            // Bind the remote object to the RMI registry
            registry.rebind("Server"+args[0], server);    //server
            System.out.println("Server" + args[0] + " is ready...");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
