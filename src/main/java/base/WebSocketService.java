package base;

import frontend.GameWebSocket;
import game.Card;

import java.util.Map;

public interface WebSocketService {

    void addUser(GameWebSocket userSocket);

    void removeUser(GameWebSocket userSocket);

    void sendPhase(String userName, String gamePhase);

//    void sendOk(String userName);

    void sendCard(String userName, String owner, Card card, int score);

    void sendWins(String userName, Map<String, Integer> wins);

    void sendDeckShuffle(String userName);

    void sendRemovePlayer(String userName, String removedUserName);

    void sendBet(String userName, String owner, int bet);

//    void sendError(String userName);

}
