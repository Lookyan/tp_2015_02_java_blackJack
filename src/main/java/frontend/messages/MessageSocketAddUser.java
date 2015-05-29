package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messageSystem.Address;

public final class MessageSocketAddUser extends MessageToWebSocketService {
    private GameWebSocket socket;

    public MessageSocketAddUser(Address from, Address to, GameWebSocket socket) {
        super(from, to);
        this.socket = socket;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.addUser(socket);
    }
}
