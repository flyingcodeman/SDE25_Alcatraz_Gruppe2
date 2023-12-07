package interfaces;


import at.falb.games.alcatraz.api.Player;
import server.AlcatrazPlayer;
import spread.SpreadException;

import java.util.List;

public interface ServerRMIInterface extends java.rmi.Remote {
    int register(String name, String clientIP) throws java.rmi.RemoteException, SpreadException;

    boolean deRegister(AlcatrazPlayer player) throws java.rmi.RemoteException, SpreadException;

    List<AlcatrazPlayer> startGame() throws java.rmi.RemoteException, SpreadException;
}
