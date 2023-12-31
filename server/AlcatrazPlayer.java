package server;

import at.falb.games.alcatraz.api.Player;

public class AlcatrazPlayer extends Player {
    private String playerIP;
    public AlcatrazPlayer(int id, String networkIP) {
        super(id);
        this.playerIP = networkIP;
    }

    public String getPlayerIP() {
        return playerIP;
    }

    public void setPlayerIP(String playerIP) {
        this.playerIP = playerIP;
    }
}
