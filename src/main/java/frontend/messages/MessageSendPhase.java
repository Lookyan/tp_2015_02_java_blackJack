package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messageSystem.Address;

public final class MessageSendPhase extends MessageToWebSocketService {
    private String user;
    private String phase;

    public MessageSendPhase(Address from, Address to, String user, String phase) {
        super(from, to);
        this.user = user;
        this.phase = phase;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.sendPhase(user, phase);
    }
}
