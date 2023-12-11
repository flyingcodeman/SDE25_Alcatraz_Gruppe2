package client;

import at.falb.games.alcatraz.api.IllegalMoveException;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;
import server.AlcatrazPlayer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

interface PlayerServer extends Remote {
    void startGame(List<AlcatrazPlayer> players) throws RemoteException;
    void sendMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws IllegalMoveException, RemoteException;
    void isAlive() throws  RemoteException;
    void initiateGameClose() throws RemoteException;
}
