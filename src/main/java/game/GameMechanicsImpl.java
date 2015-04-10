package game;

import base.GameMechanics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GameMechanicsImpl implements GameMechanics {

    private Map<String, GameTable> usersTables = new HashMap<>();
    private Queue<GameTable> freeTables = new LinkedList<>();

    public void addUser(String userSessionId) {
        GameTable table;
        if (freeTables.peek() == null) {
            table = new GameTable();
            table.addUser(userSessionId);
            freeTables.add(table);
        } else {
            table = freeTables.peek();
            table.addUser(userSessionId);
            if (table.isFull()) {
                freeTables.remove();
            }
        }
        usersTables.put(userSessionId, table);
    }

    public void removeUser(String userSessionId) {
        if (usersTables.containsKey(userSessionId)) {
            GameTable table = usersTables.get(userSessionId);
            table.removeUser(userSessionId);
            usersTables.remove(userSessionId);
        }
    }

    public void makeBet(String userSessionId, int bet) {
        if (usersTables.containsKey(userSessionId)) {
            GameTable table = usersTables.get(userSessionId);
            try {
                table.makeBet(userSessionId, bet);
            } catch (GameTableException e) {
                // log
            }
        }
    }

    public void hit(String userSessionId) {
        if (usersTables.containsKey(userSessionId)) {
            GameTable table = usersTables.get(userSessionId);
            try {
                table.hit(userSessionId);
            } catch (GameTableException e) {
                // log
            }
        }
    }

    public void stand(String userSessionId) {
        if (usersTables.containsKey(userSessionId)) {
            GameTable table = usersTables.get(userSessionId);
            table.stand(userSessionId);
        }
    }
}
