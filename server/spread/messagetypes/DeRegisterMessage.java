package server.spread.messagetypes;

import at.falb.games.alcatraz.api.Player;

import java.io.Serializable;

public class DeRegisterMessage implements Serializable {

    Player playerToRemove;

    public DeRegisterMessage(Player playerToRemove) {
        this.playerToRemove = playerToRemove;
    }

    public Player getPlayerToRemove() {
        return playerToRemove;
    }

    public void setPlayerToRemove(Player playerToRemove) {
        this.playerToRemove = playerToRemove;
    }
}
