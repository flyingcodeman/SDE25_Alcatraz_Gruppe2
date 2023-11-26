package interfaces;


import at.falb.games.alcatraz.api.Player;

import java.util.List;

public interface ServerRMIInterface extends java.rmi.Remote {
    int register(String name) throws java.rmi.RemoteException;

    boolean deRegister(Player player) throws java.rmi.RemoteException;

    List<Player> startGame() throws java.rmi.RemoteException;
}
