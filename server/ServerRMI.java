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
    public AlcatrazPlayer register(String name, String networkIP) throws RemoteException, SpreadException {
        MainServer.spreadService.registerPlayer(name, networkIP);

        return registerPlayer(name, networkIP);
    }

    @Override
    public boolean deRegister(AlcatrazPlayer player) throws RemoteException, SpreadException {
        MainServer.spreadService.deRegisterPlayer(player);

        return deRegisterPlayer(player);
    }

    @Override
    public List<AlcatrazPlayer> startGame() throws RemoteException, SpreadException {

        MainServer.spreadService.startGame();
        if(setGameStart()){
            return MainServer.players;
        }else{
            return null;
        }
    }
}
