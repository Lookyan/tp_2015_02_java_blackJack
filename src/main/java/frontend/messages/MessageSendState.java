package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import game.Player;
import messageSystem.Address;

import java.util.Map;

public final class MessageSendState extends MessageToWebSocketService {
    private String user;
    private Map<String, Player> players;

    public MessageSendState(Address from, Address to, String user, Map<String, Player> players) {
        super(from, to);
        this.user = user;
        this.players = players;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.sendState(user, players);
    }
}
