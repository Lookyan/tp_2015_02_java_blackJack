package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messageSystem.Address;

public final class MessageSocketRemoveUser extends MessageToWebSocketService {
    private GameWebSocket socket;

    public MessageSocketRemoveUser(Address from, Address to, GameWebSocket socket) {
        super(from, to);
        this.socket = socket;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.removeUser(socket);
    }
}
