package base;

import frontend.GameWebSocket;
import game.Card;

public interface WebSocketService {

    void addUser(GameWebSocket userSocket);

    void removeUser(GameWebSocket userSocket);

    void sendPhase(String userName, String gamePhase);

    void sendOk(String userName);

    void sendCard(String userName, String owner, Card card);

    void sendWins(String userName);

    void sendDeckShuffle(String userName);

    void sendRemovePlayer(String userName, String removedUserName);

    void sendError(String userName);

//    void updateCards();

//    void notifyMyNewScore(GameUser user);
//
//    void notifyEnemyNewScore(GameUser user);
//
//    void notifyStartGame(GameUser user);
//
//    void notifyGameOver(GameUser user, boolean win);
}
