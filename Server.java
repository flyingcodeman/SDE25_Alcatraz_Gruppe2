import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class Server extends UnicastRemoteObject implements ServerInterface {
    private List<ClientInterface> clients = new ArrayList<>();

    public Server() throws RemoteException {
    }

    @Override
    public synchronized void register(ClientInterface client) throws RemoteException {
        clients.add(client);
        if (clients.size() == 2) {
            clients.get(0).setOpponent(clients.get(1));
            clients.get(1).setOpponent(clients.get(0));
            clients.get(0).startGame();
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            java.rmi.registry.LocateRegistry.createRegistry(ServerConfig.SERVER_PORT);
            java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.getRegistry();
            registry.rebind("Server", server);
            System.out.println("Server gestartet.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
