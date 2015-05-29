package base;

import frontend.GameWebSocket;
import frontend.PhoneWebSocket;
import game.Card;
import game.Player;
import messageSystem.Abonent;

import java.util.Map;

public interface WebSocketService extends Runnable, Abonent {

    void addUser(GameWebSocket userSocket);

    void removeUser(GameWebSocket userSocket);

    void addPhone(PhoneWebSocket phoneSocket);

    void removePhone(PhoneWebSocket phoneSocket);

    void sendPhase(String userName, String gamePhase);

//    void sendOk(String userName);

    void sendCard(String userName, String owner, Card card, int score);

    void sendWins(String userName, Map<String, Integer> wins);

    void sendDeckShuffle(String userName);

    void sendRemovePlayer(String userName, String removedUserName);

    void sendBet(String userName, String owner, int bet);

    void sendState(String userName, Map<String, Player> players);

    void sendNewPlayer(String userName, String newPlayerName);

    void sendTurn(String userName, String player);

    void sendEnd(String userName);

}
