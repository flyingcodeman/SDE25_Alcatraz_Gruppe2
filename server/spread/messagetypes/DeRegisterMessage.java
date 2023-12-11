package server.spread.messagetypes;

import at.falb.games.alcatraz.api.Player;
import server.AlcatrazPlayer;

import java.io.Serializable;

public class DeRegisterMessage implements Serializable {

    AlcatrazPlayer playerToRemove;

    public DeRegisterMessage(AlcatrazPlayer playerToRemove) {
        this.playerToRemove = playerToRemove;
    }

    public AlcatrazPlayer getPlayerToRemove() {
        return playerToRemove;
    }

    public void setPlayerToRemove(AlcatrazPlayer playerToRemove) {
        this.playerToRemove = playerToRemove;
    }
}
