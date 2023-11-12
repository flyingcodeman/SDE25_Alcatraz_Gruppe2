import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NumberService extends Remote {
    void updateNumber(int number) throws RemoteException;
    int getNumber() throws RemoteException;
}
