package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messageSystem.Address;

public final class MessageSendTurn extends MessageToWebSocketService {
    private String user;
    private String player;

    public MessageSendTurn(Address from, Address to, String user, String player) {
        super(from, to);
        this.user = user;
        this.player = player;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.sendTurn(user, player);
    }
}
