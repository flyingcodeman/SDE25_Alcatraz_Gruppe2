package client;

import at.falb.games.alcatraz.api.IllegalMoveException;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

interface PlayerServer extends Remote {
    void startGame(List<Player> players) throws RemoteException;
    void sendMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws IllegalMoveException, RemoteException;
}
