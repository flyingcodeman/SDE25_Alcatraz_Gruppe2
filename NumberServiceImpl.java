import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class NumberServiceImpl extends UnicastRemoteObject implements NumberService {
    private int sharedNumber = 0;
    private List<NumberService> clients = new ArrayList<>();

    public NumberServiceImpl() throws RemoteException {
        // Constructor
    }

    @Override
    public synchronized void updateNumber(int number) throws RemoteException {
        sharedNumber += number;
        System.out.println("Number updated: " + sharedNumber);

        // Notify all connected clients about the update, excluding the calling server
        for (NumberService client : clients) {
            if (!client.equals(this)) {
                client.updateNumber(number);
            }
        }
    }

    @Override
    public synchronized int getNumber() throws RemoteException {
        return sharedNumber;
    }

    public synchronized void registerClient(NumberService client) throws RemoteException {
        clients.add(client);
    }

    public synchronized void unregisterClient(NumberService client) throws RemoteException {
        clients.remove(client);
    }
}
