package server;

import at.falb.games.alcatraz.api.Player;
import client.PlayerService;
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


public class MainServer extends UnicastRemoteObject implements ServerRMIInterface {
    ServerRMI serverRMI = new ServerRMI();

    private List<Player> players = new ArrayList<>();

    // Constructor
    public MainServer() throws RemoteException {
        super();
    }
    @Override
    public int register(String name) throws RemoteException {
        for(Player player : players){
            if (name.equals(player.getName())) {
                System.out.println("Value exists in the object list!");
                return -1;
            }
        }
        Player newPlayer = new Player(players.size()+1);
        newPlayer.setName(name);
        players.add(newPlayer);
        System.out.println(players);
        return newPlayer.getId();
    }


    @Override
    public boolean deRegister(Player player) throws RemoteException {

        return players.remove(player);
    }

    @Override
    public List<Player> startGame() throws RemoteException {
        return players;
    }

    public static void main(String[] args) throws UnknownHostException, SpreadException {
		SpreadService spreadService = new SpreadService("service"+args[0]);

        try {
            // Create and export the remote object
            MainServer server = new MainServer();

            // Create the RMI registry on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // Bind the remote object to the RMI registry
            registry.rebind("Server"+args[0], server);

            System.out.println("Server" + args[0] + " is ready...");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
