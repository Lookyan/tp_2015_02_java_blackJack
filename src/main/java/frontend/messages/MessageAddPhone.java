package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import frontend.PhoneWebSocket;
import messageSystem.Address;

public final class MessageAddPhone extends MessageToWebSocketService {
    private PhoneWebSocket socket;

    public MessageAddPhone(Address from, Address to, PhoneWebSocket socket) {
        super(from, to);
        this.socket = socket;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.addPhone(socket);
    }
}
