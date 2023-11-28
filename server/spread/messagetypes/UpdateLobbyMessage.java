package server.spread.messagetypes;

import at.falb.games.alcatraz.api.Player;
import server.Lobby;

import java.io.Serializable;

public class UpdateLobbyMessage implements Serializable {
    Lobby<Player> updateLobby;

    public UpdateLobbyMessage(Lobby<Player> updateLobby) {
        this.updateLobby = updateLobby;
    }

    public Lobby<Player> getUpdateLobby() {
        return updateLobby;
    }

    public void setUpdateLobby(Lobby<Player> updateLobby) {
        this.updateLobby = updateLobby;
    }
}
