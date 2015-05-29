package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messageSystem.Address;

import java.util.Map;


public final class MessageSendWins extends MessageToWebSocketService {
    private String user;
    private Map<String, Integer> wins;

    public MessageSendWins(Address from, Address to, String user, Map<String, Integer> wins) {
        super(from, to);
        this.user = user;
        this.wins = wins;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.sendWins(user, wins);
    }
}
