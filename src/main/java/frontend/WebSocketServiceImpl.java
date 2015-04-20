package frontend;

import base.WebSocketService;
import game.Card;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class WebSocketServiceImpl implements WebSocketService {

    private static final Logger logger = LogManager.getLogger();

    private Map<String, GameWebSocket> userSockets = new HashMap<>();

    @Override
    public void addUser(GameWebSocket userSocket) {
        logger.info("Adding user '{}'", userSocket.getUserName());
        userSockets.put(userSocket.getUserName(), userSocket);
    }

    @Override
    public void removeUser(GameWebSocket userSocket) {
        logger.info("Removing user '{}'", userSocket.getUserName());
        userSockets.remove(userSocket.getUserName());
    }

    @Override
    public void sendPhase(String userName, String gamePhase) {
        logger.info("Sending phase '{}' to user '{}'", gamePhase, userName);
        userSockets.get(userName).sendPhase(gamePhase);
    }

//    @Override
//    public void sendOk(String userName) {
//        logger.info("Sending ok to user '{}'", userName);
//        userSockets.get(userName).sendOk();
//    }

    @Override
    public void sendCard(String userName, String owner, Card card, int score) {
        logger.info("Sending card '{}' of '{}' (score {}) to user '{}'", card, owner, score, userName);
        userSockets.get(userName).sendCard(owner, card, score);
    }

    @Override
    public void sendWins(String userName, Map<String, Integer> wins) {
        logger.info("Sending wins to user '{}'", userName);
        userSockets.get(userName).sendWins(wins);
    }

    @Override
    public void sendDeckShuffle(String userName) {
        logger.info("Sending deck shuffle to user '{}'", userName);
        userSockets.get(userName).sendDeckShuffle();
    }

    @Override
    public void sendRemovePlayer(String userName, String removedUserName) {
        logger.info("Sending removed user '{}' to user '{}'", removedUserName, userName);
        userSockets.get(userName).sendRemovePlayer(removedUserName);
    }

    @Override
    public void sendBet(String userName, String owner, int bet) {
        logger.info("Sending bet={} of '{}' to user '{}'", bet, owner, userName);
        userSockets.get(userName).sendBet(owner, bet);
    }

//    @Override
//    public void sendError(String userName) {
//        logger.info("Sending error to user '{}'", userName);
//        userSockets.get(userName).sendError();
//    }
}
