package server;

import at.falb.fh.vtsys.PlayerService;
import at.falb.games.alcatraz.api.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PlayerServiceImpl extends UnicastRemoteObject implements PlayerService {
    Player player;

    public PlayerServiceImpl() throws RemoteException {
        // No need to initialize sharedNumber here
    }

    @Override
    public String getPlayerName() throws RemoteException {
        return player.getName();
    }

    @Override
    public Integer getPlayerId() throws RemoteException {
        return player.getId();
    }

    @Override
    public void initPlayer(String name, int id) throws RemoteException {
        player = new Player(id);
        player.setName(name);
    }

    @Override
    public void notifyMove(int playerId, int prisonerId, int rowOrCol, int row, int col) throws RemoteException {
        // Implement this method to handle notifications of moves
        // You can perform actions on the server based on the moves received
        // For example, you might update the game state, check for a winner, etc.
    }
}
