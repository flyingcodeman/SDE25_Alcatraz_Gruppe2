package server.spread.messagetypes;

import at.falb.games.alcatraz.api.Player;
import server.AlcatrazPlayer;
import server.Lobby;

import java.io.Serializable;

public class StartGameMessage implements Serializable {
    Lobby<AlcatrazPlayer> finalLobby;

    public StartGameMessage(Lobby<AlcatrazPlayer> finalLobby) {
        this.finalLobby = finalLobby;
    }

    public Lobby<AlcatrazPlayer> getFinalLobby() {
        return finalLobby;
    }

    public void setFinalLobby(Lobby<AlcatrazPlayer> finalLobby) {
        this.finalLobby = finalLobby;
    }

}
