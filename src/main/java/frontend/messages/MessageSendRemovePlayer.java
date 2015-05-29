package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messageSystem.Address;

public final class MessageSendRemovePlayer extends MessageToWebSocketService {
    private String user;
    private String removedUser;

    public MessageSendRemovePlayer(Address from, Address to, String user, String removedUser) {
        super(from, to);
        this.user = user;
        this.removedUser = removedUser;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.sendRemovePlayer(user, removedUser);
    }
}
