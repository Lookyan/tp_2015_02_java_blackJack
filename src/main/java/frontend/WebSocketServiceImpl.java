package frontend;

import base.WebSocketService;
import game.Card;
import game.Player;
import main.Context;
import messageSystem.Abonent;
import messageSystem.Address;
import messageSystem.Message;
import messageSystem.MessageSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class WebSocketServiceImpl implements WebSocketService {

    private static final Logger logger = LogManager.getLogger();
    private final Address address = new Address();

    private Map<String, GameWebSocket> userSockets = new HashMap<>();
    private Map<String, PhoneWebSocket> phoneSockets = new HashMap<>();

    private MessageSystem messageSystem;

    public WebSocketServiceImpl(Context context) {
        messageSystem = (MessageSystem) context.get(MessageSystem.class);
    }

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
    public void addPhone(PhoneWebSocket phoneSocket) {
        logger.info("Adding phone of '{}'", phoneSocket.getUserName());
        phoneSockets.put(phoneSocket.getUserName(), phoneSocket);
    }

    @Override
    public void removePhone(PhoneWebSocket phoneSocket) {
        logger.info("Removing phone of '{}'", phoneSocket.getUserName());
        phoneSockets.remove(phoneSocket.getUserName());
    }

    @Override
    public void sendPhase(String userName, String gamePhase) {
        logger.info("Sending phase '{}' to user '{}'", gamePhase, userName);
        try {
            userSockets.get(userName).sendPhase(gamePhase);
        } catch (NullPointerException e) {
            logger.error("No socket for user '{}'", userName);
        }
        PhoneWebSocket phoneSocket = phoneSockets.get(userName);
        if (phoneSocket != null) {
            phoneSocket.sendPhase(gamePhase);
        }
    }

    @Override
    public void sendCard(String userName, String owner, Card card, int score) {
        logger.info("Sending card '{}' of '{}' (score {}) to user '{}'", card, owner, score, userName);
        try {
            userSockets.get(userName).sendCard(owner, card, score);
        } catch (NullPointerException e) {
            logger.error("No socket for user '{}'", userName);
        }
    }

    @Override
    public void sendWins(String userName, Map<String, Integer> wins) {
        logger.info("Sending wins to user '{}'", userName);
        try {
            userSockets.get(userName).sendWins(wins);
        } catch (NullPointerException e) {
            logger.error("No socket for user '{}'", userName);
        }
    }

    @Override
    public void sendDeckShuffle(String userName) {
        logger.info("Sending deck shuffle to user '{}'", userName);
        try {
            userSockets.get(userName).sendDeckShuffle();
        } catch (NullPointerException e) {
            logger.error("No socket for user '{}'", userName);
        }
    }

    @Override
    public void sendRemovePlayer(String userName, String removedUserName) {
        logger.info("Sending removed user '{}' to user '{}'", removedUserName, userName);
        try {
            userSockets.get(userName).sendRemovePlayer(removedUserName);
        } catch (NullPointerException e) {
            logger.error("No socket for user '{}'", userName);
        }
    }

    @Override
    public void sendBet(String userName, String owner, int bet) {
        logger.info("Sending bet={} of '{}' to user '{}'", bet, owner, userName);
        try {
            userSockets.get(userName).sendBet(owner, bet);
        } catch (NullPointerException e) {
            logger.error("No socket for user '{}'", userName);
        }
    }

    @Override
    public void sendState(String userName, Map<String, Player> players) {
        logger.info("Sending state to user '{}'", userName);
        try {
            userSockets.get(userName).sendState(players);
        } catch (NullPointerException e) {
            logger.error("No socket for user '{}'", userName);
        }
    }

    @Override
    public void sendNewPlayer(String userName, String newPlayerName) {
        logger.info("Sending new player '{}' to user '{}'", newPlayerName, userName);
        try {
            userSockets.get(userName).sendNewPlayer(newPlayerName);
        } catch (NullPointerException e) {
            logger.error("No socket for user '{}'", userName);
        }
    }

    @Override
    public void sendTurn(String userName, String player) {
        logger.info("Sending '{}'s turn to user '{}'", player, userName);
        try {
            userSockets.get(userName).sendTurn(player);
        } catch (NullPointerException e) {
            logger.error("No socket for user '{}'", userName);
        }
    }

    @Override
    public void sendEnd(String userName) {
        logger.info("Sending end turn to user '{}'", userName);
        try {
            userSockets.get(userName).sendEnd();
        } catch (NullPointerException e) {
            logger.error("No socket for user '{}'", userName);
        }
        PhoneWebSocket phoneSocket = phoneSockets.get(userName);
        if (phoneSocket != null) {
            phoneSocket.sendEnd();
        }
    }


    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void run() {
        while (true){
            messageSystem.execForAbonent(this);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
