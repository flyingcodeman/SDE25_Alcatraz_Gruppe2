import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    void receiveMessage(String message) throws RemoteException;
    //void setOpponent(ClientInterface opponent) throws RemoteException;
    //void startGame() throws RemoteException;
    void sendMessage(int value) throws RemoteException;

    //void sendMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col, int counter);
}
