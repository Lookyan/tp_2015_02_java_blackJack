package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import game.Card;
import messageSystem.Address;

public final class MessageSendCard extends MessageToWebSocketService {
    private String user;
    private String owner;
    private Card card;
    private int score;

    public MessageSendCard(Address from, Address to, String user, String owner, Card card, int score) {
        super(from, to);
        this.user = user;
        this.owner = owner;
        this.card = card;
        this.score = score;

    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.sendCard(user, owner, card, score);
    }
}
