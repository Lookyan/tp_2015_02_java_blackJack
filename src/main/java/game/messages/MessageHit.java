package game.messages;

import base.GameMechanics;
import messageSystem.Address;

public final class MessageHit extends MessageToGameMechanics {
    private String userName;

    public MessageHit(Address from, Address to, String userName) {
        super(from, to);
        this.userName = userName;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        gameMechanics.hit(userName);
    }
}
