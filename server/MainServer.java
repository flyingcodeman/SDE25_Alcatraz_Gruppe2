package server;

import at.falb.games.alcatraz.api.Player;
import client.PlayerServerImpl;
import interfaces.Constants;
import interfaces.ServerRMIInterface;
import spread.SpreadException;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static interfaces.Constants.MY_NETWORK;
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

        if(players.size() >= 4){
            System.out.println("Log Lobby full! Cant register " + name);
            return new AlcatrazPlayer(-1, "lobbyFull");
        }

        for(AlcatrazPlayer player : players){
            if (Objects.equals(player.getName(), name)) {
                System.out.println("Log player name " + player.getName() + " already exists!");
                return new AlcatrazPlayer(-2, "nameTaken");
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

        String serverPortName;
        try {
            Field serverPortField = Constants.class.getDeclaredField("SERVER_PORT" + String.valueOf(args[0]));
            serverPortName = String.valueOf(serverPortField.get(null));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try {

            Registry registry = null;
            try {
                registry = LocateRegistry.createRegistry(Integer.parseInt(serverPortName));

            } catch (RemoteException ex) {
                System.out.println(ex);
            }


            ServerRMIInterface server = new ServerRMI();
            // Bind the remote object to the RMI registry
            registry.rebind("Server"+args[0], server);   //server
            System.out.println("Server" + args[0] + " is ready...");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
