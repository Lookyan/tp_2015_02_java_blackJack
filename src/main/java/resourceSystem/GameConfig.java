package resourceSystem;

import base.Resource;

public class GameConfig implements Resource {

    private int maxPlayers;
    private String dealerName;

    public GameConfig() {
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getDealerName() {
        return dealerName;
    }

}
