import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;

public class Client extends UnicastRemoteObject implements ClientInterface {
    private String name;
    private int id;
    private int currentTurnId;
    private ClientInterface opponent;
    private int currentValue;
    private int moveCounter = 0;
    private Alcatraz alcatraz;
    private List<Player> players = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();

    public Client(String name) throws RemoteException {
        this.name = name;
        this.currentValue = 1;
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        System.out.println(name + " hat eine Nachricht erhalten: " + message);
    }

    @Override
    public void sendMessage(int value) throws RemoteException {
        if (currentValue == 10) {
            System.out.println(name + " sagt: Fertig!");
            opponent.receiveMessage(name + " sagt: Fertig!");
        } else {
            currentValue++;
            System.out.println(name + " sendet " + currentValue);
            opponent.receiveMessage(name + " sendet " + currentValue);
            opponent.sendMessage(currentValue);
        }
    }

    public static void main(String[] args) {
        try {
            String serverHost = "localhost";
            Registry registry = LocateRegistry.getRegistry(serverHost, 4567);
            ServerInterface server = (ServerInterface) registry.lookup("Server");

            String clientName = "Client1";
            Client client = new Client(clientName);
            server.register(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
