package server.spread.messagetypes;

import java.io.Serializable;

public class RegisterMessage implements Serializable {
    String playerName;

    public RegisterMessage(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
