package frontend;

import base.AccountService;
import base.GameMechanics;
import base.UserProfile;
import base.WebSocketService;
import game.Card;
import main.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

@SuppressWarnings("unchecked")
@WebSocket
public class GameWebSocket {

    private static final Logger logger = LogManager.getLogger();
    private static final JSONParser parser = new JSONParser();

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
        logger.info("Opened new socket");
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
        try {
            JSONObject message = (JSONObject) parser.parse(data);
            logger.info("Incoming message: {}", message);
            switch ((String) message.get("type")) {
                case "bet":
                    gameMechanics.makeBet(userName, (new Long((long) message.get("bet")).intValue()));
                    break;
                case "hit":
                    gameMechanics.hit(userName);
                    break;
                case "stand":
                    gameMechanics.stand(userName);
                    break;
            }
        } catch (ParseException e) {
            logger.error("Can't parse incoming JSON", e);
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        logger.info("Closing socket on '{}' with code {}, reason: '{}'", userName, statusCode, reason);
        if (accountService != null) {
            webSocketService.removeUser(this);
            gameMechanics.removeUser(userName);
        }
    }

    private void sendNotLogged() {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", "403");
            resp.put("body", "not logged");

            logger.info("Sending resp: {}", resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("GameWebSocket@" + hashCode(), e);
        }
    }

    public void sendPhase(String gamePhase) {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            JSONObject body = new JSONObject();
            body.put("type", "phase");
            body.put("phase", gamePhase);
            resp.put("body", body);

            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("GameWebSocket@" + hashCode(), e);
        }
    }

    public void sendOk() {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            JSONObject body = new JSONObject();
            body.put("type", "ok");
            resp.put("body", body);

            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("GameWebSocket@" + hashCode(), e);
        }
    }

    public void sendCard(String owner, Card card, int score) {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            JSONObject body = new JSONObject();
            body.put("type", "card");
            body.put("owner", owner);
            body.put("card", card.toString());
            body.put("score", score);
            resp.put("body", body);

            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("GameWebSocket@" + hashCode(), e);
        }
    }

    public void sendBet(String owner, int bet) {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            JSONObject body = new JSONObject();
            body.put("type", "bet");
            body.put("owner", owner);
            body.put("bet", bet);
            resp.put("body", body);

            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("GameWebSocket@" + hashCode(), e);
        }
    }

    public void sendWins(Map<String, Integer> wins) {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            JSONObject body = new JSONObject();
            body.put("type", "wins");
            JSONObject jsonWins = new JSONObject();
            wins.entrySet().stream().forEach(entry -> jsonWins.put(entry.getKey(), entry.getValue()));
            body.put("wins", jsonWins);
            resp.put("body", body);

            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("GameWebSocket@" + hashCode(), e);
        }
    }

    public void sendDeckShuffle() {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            JSONObject body = new JSONObject();
            body.put("type", "shuffle");
            resp.put("body", body);

            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("GameWebSocket@" + hashCode(), e);
        }
    }

    public void sendRemovePlayer(String removedUserName) {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            JSONObject body = new JSONObject();
            body.put("type", "exit");
            body.put("player", removedUserName);
            resp.put("body", body);

            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("GameWebSocket@" + hashCode(), e);
        }
    }

    public void sendError() {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            JSONObject body = new JSONObject();
            body.put("type", "error");
            resp.put("body", body);

            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("GameWebSocket@" + hashCode(), e);
        }
    }

    public String getUserName() {
        return userName;
    }

}
