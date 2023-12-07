package server;

import at.falb.games.alcatraz.api.Player;
import interfaces.ServerRMIInterface;
import spread.SpreadException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import static server.MainServer.deRegisterPlayer;
import static server.MainServer.registerPlayer;

public class ServerRMI extends UnicastRemoteObject implements ServerRMIInterface {
    protected ServerRMI() throws RemoteException {
        super();
    }

    @Override
    public int register(String name, String networkIP) throws RemoteException, SpreadException {
        MainServer.spreadService.registerPlayer(name, networkIP);

        Player newPlayer = registerPlayer(name, networkIP);
        if (newPlayer == null) return -1;

        return newPlayer.getId();
    }

    @Override
    public boolean deRegister(AlcatrazPlayer player) throws RemoteException, SpreadException {
        MainServer.spreadService.deRegisterPlayer(player);

        return deRegisterPlayer(player);
    }

    @Override
    public List<AlcatrazPlayer> startGame() throws RemoteException, SpreadException {
        if(MainServer.players.size() >= 2 && !MainServer.gameStarted){
            MainServer.spreadService.startGame();
            MainServer.gameStarted = true;
            return MainServer.players;
        }else{
            return null;
        }
    }
}
