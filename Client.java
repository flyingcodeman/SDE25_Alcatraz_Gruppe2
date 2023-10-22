import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class Client extends UnicastRemoteObject implements ClientInterface {
    private String name;
    private ClientInterface opponent;
    private int currentValue;

    public Client(String name) throws RemoteException {
        this.name = name;
        this.currentValue = 1;
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        System.out.println(name + " hat eine Nachricht erhalten: " + message);
    }

    @Override
    public void setOpponent(ClientInterface opponent) throws RemoteException {
        this.opponent = opponent;
    }

    @Override
    public void startGame() throws RemoteException {
        playGame();
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

    private void playGame() {
        try {
            sendMessage(currentValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String serverHost = "localhost";
            Registry registry = LocateRegistry.getRegistry(serverHost, 1099);
            ServerInterface server = (ServerInterface) registry.lookup("Server");

            String clientName = "Client1";
            Client client = new Client(clientName);
            server.register(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
