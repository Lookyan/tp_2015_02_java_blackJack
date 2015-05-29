package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messageSystem.Address;

public final class MessageSendDeckShuffle extends MessageToWebSocketService {
    private String user;

    public MessageSendDeckShuffle(Address from, Address to, String user) {
        super(from, to);
        this.user = user;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.sendDeckShuffle(user);
    }
}
