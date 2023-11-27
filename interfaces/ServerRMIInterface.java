package interfaces;


import at.falb.games.alcatraz.api.Player;
import spread.SpreadException;

import java.util.List;

public interface ServerRMIInterface extends java.rmi.Remote {
    int register(String name) throws java.rmi.RemoteException, SpreadException;

    boolean deRegister(Player player) throws java.rmi.RemoteException, SpreadException;

    List<Player> startGame() throws java.rmi.RemoteException;
}
