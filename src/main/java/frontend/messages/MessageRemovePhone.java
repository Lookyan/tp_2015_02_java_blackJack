package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import frontend.PhoneWebSocket;
import messageSystem.Address;

/**
 * @author e.shubin
 */
public final class MessageRemovePhone extends MessageToWebSocketService {
    private PhoneWebSocket socket;

    public MessageRemovePhone(Address from, Address to, PhoneWebSocket socket) {
        super(from, to);
        this.socket = socket;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.removePhone(socket);
    }
}
