package game.messages;

import base.GameMechanics;
import messageSystem.Address;

public final class MessageGameRemoveUser extends MessageToGameMechanics {
    private String userName;

    public MessageGameRemoveUser(Address from, Address to, String userName) {
        super(from, to);
        this.userName = userName;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        gameMechanics.removeUser(userName);
    }
}
