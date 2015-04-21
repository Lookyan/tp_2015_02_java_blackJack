package resourceSystem;

import base.Resource;

public class ServerConfig implements Resource {

    private int port;

    public ServerConfig() {
        this.port = 80;
    }

    public int getPort() {
        return port;
    }

}
