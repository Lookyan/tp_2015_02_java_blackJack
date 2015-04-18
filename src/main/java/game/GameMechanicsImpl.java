package game;

import base.GameMechanics;
import main.Context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GameMechanicsImpl implements GameMechanics {

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
        if (freeTables.peek() == null) {
            freeTables.add(new GameTable(context));
        }

        GameTable table = freeTables.peek();
        try {
            table.addUser(userName);
        } catch (GameTableException e) {
//                TODO: log
        }
        if (table.isFull()) {
            freeTables.remove();
        }
        usersTables.put(userName, table);
    }

    @Override
    public void removeUser(String userName) {
        if (usersTables.containsKey(userName)) {
            GameTable table = usersTables.get(userName);
            try {
                table.removeUser(userName);
            } catch (GameTableException e) {
//                TODO: log
            }
            freeTables.add(table);
            usersTables.remove(userName);
        } else {
//            TODO: log
        }
    }

    @Override
    public void makeBet(String userName, int bet) {
        if (usersTables.containsKey(userName)) {
            GameTable table = usersTables.get(userName);
            try {
                table.makeBet(userName, bet);
            } catch (GameTableException e) {
//                TODO: log
            }
        } else {
//            TODO: log
        }
    }

    @Override
    public void hit(String userName) {
        if (usersTables.containsKey(userName)) {
            GameTable table = usersTables.get(userName);
            try {
                table.hit(userName);
            } catch (GameTableException e) {
//                TODO: log
            }
        } else {
//            TODO: log
        }
    }

    @Override
    public void stand(String userName) {
        if (usersTables.containsKey(userName)) {
            GameTable table = usersTables.get(userName);
            try {
                table.stand(userName);
            } catch (GameTableException e) {
//                TODO: log
            }
        } else {
//            TODO: log
        }
    }
}
