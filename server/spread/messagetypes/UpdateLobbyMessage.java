package server.spread.messagetypes;

import at.falb.games.alcatraz.api.Player;
import server.AlcatrazPlayer;
import server.Lobby;

import java.io.Serializable;

public class UpdateLobbyMessage implements Serializable {
    Lobby<AlcatrazPlayer> updateLobby;

    public UpdateLobbyMessage(Lobby<AlcatrazPlayer> updateLobby) {
        this.updateLobby = updateLobby;
    }

    public Lobby<AlcatrazPlayer> getUpdateLobby() {
        return updateLobby;
    }

    public void setUpdateLobby(Lobby<AlcatrazPlayer> updateLobby) {
        this.updateLobby = updateLobby;
    }
}
