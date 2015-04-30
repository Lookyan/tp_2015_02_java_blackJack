package game;

import base.GameMechanics;
import base.GameTable;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameMechanicsImpl implements GameMechanics {

    private static final Logger logger = LogManager.getLogger();

    private Context context;

    private Function<Context, GameTable> creator;

    private Map<String, GameTable> usersTables = new HashMap<>();

    // Очередь столов со свободными местами
    private Queue<GameTable> freeTables = new LinkedList<>();

    public GameMechanicsImpl(Context context, Function<Context, GameTable> creator) {
        this.creator = creator;
        this.context = context;
    }

    @Override
    public void addUser(String userName) {
        logger.info("Adding user '{}'", userName);
        if (freeTables.peek() == null) {
            freeTables.add(creator.apply(context));
            logger.info("New table was created and added to freeTables: {}",
                    freeTables.stream().map(t->"@" + Integer.toString(t.hashCode())).collect(Collectors.toList()));
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
            logger.info("Table removed from freeTables: {}",
                    freeTables.stream().map(t->"@" + Integer.toString(t.hashCode())).collect(Collectors.toList()));
        }
        usersTables.put(userName, table);
    }

    @Override
    public void removeUser(String userName) {
        logger.info("Removing user '{}'", userName);
        if (usersTables.containsKey(userName)) {
            GameTable table = usersTables.get(userName);
            logger.info("Obtained table: {}", table.hashCode());
            try {
                table.removeUser(userName);
            } catch (GameTableException e) {
                logger.error(e);
            }
            if (!freeTables.contains(table)) {
                freeTables.add(table);
                logger.info("Table added to freeTables: {}",
                        freeTables.stream().map(t->"@" + Integer.toString(t.hashCode())).collect(Collectors.toList()));
            }
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
            logger.info("Obtained table: GameTable@{}", table.hashCode());
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
            logger.info("Obtained table: GameTable@{}", table.hashCode());
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
            logger.info("Obtained table: GameTable@{}", table.hashCode());
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
