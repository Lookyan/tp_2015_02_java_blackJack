package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messageSystem.Address;

public final class MessageSendNewPlayer extends MessageToWebSocketService {
    private String user;
    private String newPlayer;

    public MessageSendNewPlayer(Address from, Address to, String user, String newPlayer) {
        super(from, to);
        this.user = user;
        this.newPlayer = newPlayer;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.sendNewPlayer(user, newPlayer);
    }
}
