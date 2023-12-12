package server;

import at.falb.games.alcatraz.api.Player;
import interfaces.ServerRMIInterface;
import spread.SpreadException;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import static server.MainServer.*;

public class ServerRMI extends UnicastRemoteObject implements ServerRMIInterface, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected ServerRMI() throws RemoteException {
        super();
    }

    @Override
    public AlcatrazPlayer register(String name, String networkIP) throws RemoteException {
        try {
            MainServer.spreadService.registerPlayer(name, networkIP);
            return registerPlayer(name, networkIP);
        } catch (SpreadException e) {
            return null;
        }
    }

    @Override
    public boolean deRegister(AlcatrazPlayer player) throws RemoteException {
        try {
            MainServer.spreadService.deRegisterPlayer(player);
            return deRegisterPlayer(player);
        } catch (SpreadException e) {
            return false;
        }
    }

    @Override
    public List<AlcatrazPlayer> startGame() throws RemoteException {
        try {
            MainServer.spreadService.startGame();
            return setGameStart();
        } catch (SpreadException e) {
            return null;
        }
    }
}
