package server.spread.messagetypes;

import java.io.Serializable;

public class RegisterMessage implements Serializable {
    String playerName;
    String playerIP;

    public RegisterMessage(String playerName, String playerIP) {
        this.playerName = playerName;
        this.playerIP = playerIP;
    }
    public String getPlayerIP() {
        return playerIP;
    }

    public void setPlayerIP(String playerIP) {
        this.playerIP = playerIP;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
