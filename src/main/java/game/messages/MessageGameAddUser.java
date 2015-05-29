package game.messages;

import base.GameMechanics;
import base.WebSocketService;
import frontend.PhoneWebSocket;
import frontend.messages.MessageToWebSocketService;
import messageSystem.Address;

public final class MessageGameAddUser extends MessageToGameMechanics {
    private String userName;

    public MessageGameAddUser(Address from, Address to, String userName) {
        super(from, to);
        this.userName = userName;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        gameMechanics.addUser(userName);
    }
}
