package game.messages;

import base.GameMechanics;
import messageSystem.Address;

public final class MessageMakeBet extends MessageToGameMechanics {
    private String userName;
    private int bet;

    public MessageMakeBet(Address from, Address to, String userName, int bet) {
        super(from, to);
        this.userName = userName;
        this.bet = bet;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        gameMechanics.makeBet(userName, bet);
    }
}
