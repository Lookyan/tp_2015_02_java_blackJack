package base;

import frontend.GameWebSocket;

public interface WebSocketService {

    void addUser(GameWebSocket user);

    void sendPhase(String userSessionId, String gamePhase);

    void sendOk(String userSessionId);

    void sendCard(String userSessionId, String card, int owner);

    void sendWins();

//    void updateCards();

//    void notifyMyNewScore(GameUser user);
//
//    void notifyEnemyNewScore(GameUser user);
//
//    void notifyStartGame(GameUser user);
//
//    void notifyGameOver(GameUser user, boolean win);
}
