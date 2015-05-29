package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messageSystem.Address;

public final class MessageSendBet extends MessageToWebSocketService {

    private String user;
    private String owner;
    private int bet;

    public MessageSendBet(Address from, Address to,String user, String owner, int bet) {
        super(from, to);
        this.user = user;
        this.owner = owner;
        this.bet = bet;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.sendBet(user, owner, bet);
    }
}
