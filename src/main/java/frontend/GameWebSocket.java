package frontend;

import base.AccountService;
import base.GameMechanics;
import base.UserProfile;
import base.WebSocketService;
import game.Card;
import main.Context;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class GameWebSocket {

    private String userName;

    private Session socketSession;

    private AccountService accountService;
    private GameMechanics gameMechanics;
    private WebSocketService webSocketService;

    public GameWebSocket(String userSessionId, Context context) {
        UserProfile user = ((AccountService) context.get(AccountService.class)).getUserBySession(userSessionId);
        if (user != null) {
            this.userName = user.getName();
            this.accountService = (AccountService) context.get(AccountService.class);
            this.gameMechanics = (GameMechanics) context.get(GameMechanics.class);
            this.webSocketService = (WebSocketService) context.get(WebSocketService.class);
        } else {
            // not logged in или нас хакают :)
        }
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        socketSession = session;

        if (accountService != null) {
            webSocketService.addUser(this);
            gameMechanics.addUser(userName);
        } else {
            sendNotLogged();
            socketSession.close();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
//        gameMechanics.incrementScore(myName);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        if (accountService != null) {
            webSocketService.removeUser(this);
            gameMechanics.removeUser(userName);
        }
    }

    private void sendNotLogged() {
    }

    public void sendPhase(String gamePhase) {

    }

    public void sendOk() {

    }

    public void sendCard(String owner, Card card) {

    }

    public void sendWins() {

    }

    public void sendDeckShuffle() {

    }

    public void sendRemovePlayer(String removedUserName) {

    }

    public void sendError() {

    }

    public String getUserName() {
        return userName;
    }

//    public Session getSocketSession() {
//        return socketSession;
//    }

//    public String getMyName() {
//        return myName;
//    }

//    public void startGame(GameUser user) {
//        try {
//            JSONObject jsonStart = new JSONObject();
//            jsonStart.put("status", "start");
//            jsonStart.put("enemyName", user.getEnemyName());
//            socketSession.getRemote().sendString(jsonStart.toJSONString());
//        } catch (Exception e) {
//            System.out.print(e.toString());
//        }
//    }
//
//    public void gameOver(GameUser user, boolean win) {
//        try {
//            JSONObject jsonStart = new JSONObject();
//            jsonStart.put("status", "finish");
//            jsonStart.put("win", win);
//            socketSession.getRemote().sendString(jsonStart.toJSONString());
//        } catch (Exception e) {
//            System.out.print(e.toString());
//        }
//    }
//
//
//
//
//
//    public void setMyScore(GameUser user) {
//        JSONObject jsonStart = new JSONObject();
//        jsonStart.put("status", "increment");
//        jsonStart.put("name", myName);
//        jsonStart.put("score", user.getMyScore());
//        try {
//            socketSession.getRemote().sendString(jsonStart.toJSONString());
//        } catch (Exception e) {
//            System.out.print(e.toString());
//        }
//    }
//
//    public void setEnemyScore(GameUser user) {
//        JSONObject jsonStart = new JSONObject();
//        jsonStart.put("status", "increment");
//        jsonStart.put("name", user.getEnemyName());
//        jsonStart.put("score", user.getEnemyScore());
//        try {
//            socketSession.getRemote().sendString(jsonStart.toJSONString());
//        } catch (Exception e) {
//            System.out.print(e.toString());
//        }
//    }

}
