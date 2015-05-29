package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messageSystem.Address;

public final class MessageSendEnd extends MessageToWebSocketService {
    private String user;

    public MessageSendEnd(Address from, Address to, String user) {
        super(from, to);
        this.user = user;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.sendEnd(user);
    }
}
