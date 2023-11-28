package server.spread.messagetypes;

import at.falb.games.alcatraz.api.Player;
import server.Lobby;

import java.io.Serializable;

public class StartGameMessage implements Serializable {
    Lobby<Player> finalLobby;

    public StartGameMessage(Lobby<Player> finalLobby) {
        this.finalLobby = finalLobby;
    }

    public Lobby<Player> getFinalLobby() {
        return finalLobby;
    }

    public void setFinalLobby(Lobby<Player> finalLobby) {
        this.finalLobby = finalLobby;
    }

}
