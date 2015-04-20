package game;

import base.GameMechanics;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GameMechanicsImpl implements GameMechanics {

    private static final Logger logger = LogManager.getLogger();

    private Map<String, GameTable> usersTables = new HashMap<>();
    private Queue<GameTable> freeTables = new LinkedList<>();
    private Context context;
//    private AccountService accountService;
//    private WebSocketService webSocketService;

    public GameMechanicsImpl(Context context) {
        this.context = context;
//        accountService = (AccountService) context.get(AccountService.class);
//        webSocketService = (WebSocketService) context.get(WebSocketService.class);
    }

    @Override
    public void addUser(String userName) {
        logger.info("Adding user '{}'", userName);
        if (freeTables.peek() == null) {
            freeTables.add(new GameTable(context));
            logger.info("New table was created and added to freeTables: {}", freeTables);
        }

        GameTable table = freeTables.peek();
        try {
            table.addUser(userName);
        } catch (GameTableException e) {
            logger.error(e);
        }
        logger.info("User '{}' added to table: {}", userName, table);
        if (table.isFull()) {
            freeTables.remove();
        }
        usersTables.put(userName, table);
    }

    @Override
    public void removeUser(String userName) {
        logger.info("Removing user '{}'", userName);
        if (usersTables.containsKey(userName)) {
            GameTable table = usersTables.get(userName);
            logger.info("Obtained table: {}", table);
            try {
                table.removeUser(userName);
            } catch (GameTableException e) {
                logger.error(e);
            }
            freeTables.add(table);
            logger.info("Table added to freeTables: {}", freeTables);
            usersTables.remove(userName);
        } else {
            logger.warn("Cant remove user! No such user playing now!");
        }
    }

    @Override
    public void makeBet(String userName, int bet) {
        logger.info("Making bet {} by '{}'", bet, userName);
        if (usersTables.containsKey(userName)) {
            GameTable table = usersTables.get(userName);
            logger.info("Obtained table: {}", table);
            try {
                table.makeBet(userName, bet);
            } catch (GameTableException e) {
                logger.error(e);
            }
        } else {
            logger.warn("Cant make bet! No such user playing now!");
        }
    }

    @Override
    public void hit(String userName) {
        logger.info("Hitting by '{}'", userName);
        if (usersTables.containsKey(userName)) {
            GameTable table = usersTables.get(userName);
            logger.info("Obtained table: {}", table);
            try {
                table.hit(userName);
            } catch (GameTableException e) {
                logger.error(e);
            }
        } else {
            logger.warn("Cant hit! No such user playing now!");
        }
    }

    @Override
    public void stand(String userName) {
        logger.info("Standing by '{}'", userName);
        if (usersTables.containsKey(userName)) {
            GameTable table = usersTables.get(userName);
            logger.info("Obtained table: {}", table);
            try {
                table.stand(userName);
            } catch (GameTableException e) {
                logger.error(e);
            }
        } else {
            logger.warn("Cant stand! :) No such user playing now!");
        }
    }
}
