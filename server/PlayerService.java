package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PlayerService extends Remote {
    String getPlayerName() throws RemoteException;
    Integer getPlayerId() throws RemoteException;
    void initPlayer(String name, int id) throws RemoteException;
    void notifyMove(int playerId, int prisonerId, int rowOrCol, int row, int col) throws RemoteException;
}
