package frontend;

import base.WebSocketService;
import game.Card;

import java.util.HashMap;
import java.util.Map;

public class WebSocketServiceImpl implements WebSocketService {
    private Map<String, GameWebSocket> userSockets = new HashMap<>();

    @Override
    public void addUser(GameWebSocket userSocket) {
        userSockets.put(userSocket.getUserName(), userSocket);
    }

    @Override
    public void removeUser(GameWebSocket userSocket) {
        userSockets.remove(userSocket.getUserName());
    }

    @Override
    public void sendPhase(String userName, String gamePhase) {

    }

    @Override
    public void sendOk(String userName) {

    }

    @Override
    public void sendCard(String userName, String owner, Card card) {

    }

    @Override
    public void sendWins(String userName) {

    }

    @Override
    public void sendDeckShuffle(String userName) {

    }

    @Override
    public void sendRemovePlayer(String userName, String removedUserName) {

    }

    @Override
    public void sendError(String userName) {

    }
}
