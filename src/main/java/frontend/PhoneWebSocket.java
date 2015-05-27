package frontend;

import base.AccountService;
import base.GameMechanics;
import base.WebSocketService;
import game.Card;
import game.Player;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

@SuppressWarnings("unchecked")
@WebSocket
public class PhoneWebSocket {

    private static final Logger logger = LogManager.getLogger();
    private static final JSONParser parser = new JSONParser();

    private String userName;

    private Session socketSession;

    private AccountService accountService;
    private GameMechanics gameMechanics;
    private WebSocketService webSocketService;

    public PhoneWebSocket(String token, Context context) {
        this.accountService = (AccountService) context.get(AccountService.class);
        this.userName = accountService.getUserByToken(token);
        this.gameMechanics = (GameMechanics) context.get(GameMechanics.class);
        this.webSocketService = (WebSocketService) context.get(WebSocketService.class);
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        logger.info("Opened new socket");
        socketSession = session;

        if (userName != null) {
            webSocketService.addPhone(this);
//            gameMechanics.addUser(userName);
        } else {
            sendWrongToken();
            socketSession.close();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        try {
            JSONObject message = (JSONObject) parser.parse(data);
            logger.info("Incoming message: {}", data);
            if (!message.containsKey("type")) {
                throw new Exception("Can't classify JSON, no 'type' field");
            }
            switch ((String) message.get("type")) {
                case "bet":
                    if (!message.containsKey("bet")) {
                        throw new Exception("Can't make bet, no 'bet' field in JSON");
                    }
                    gameMechanics.makeBet(userName, ((Long) message.get("bet")).intValue());
                    break;
                case "hit":
                    gameMechanics.hit(userName);
                    break;
                case "stand":
                    gameMechanics.stand(userName);
                    break;
                default:
                    throw new Exception("Unknown type of message");
            }
        } catch (ParseException e) {
            logger.error("Can't parse incoming JSON");
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        logger.info("Closing socket on '{}' with code {}, reason: '{}'", userName, statusCode, reason);
        if (userName != null) {
            webSocketService.removePhone(this);
//            gameMechanics.removeUser(userName);
        }
    }

    private void sendWrongToken() {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", 403);
            resp.put("body", "wrong token");

            logger.info("Sending resp: {}", resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("PhoneWebSocket@" + hashCode(), e);
        }
    }

    public void sendPhase(String gamePhase) {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", 200);
            JSONObject body = new JSONObject();
            body.put("type", "phase");
            body.put("phase", gamePhase);
            resp.put("body", body);

            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("PhoneWebSocket@" + hashCode(), e);
        }
    }

//    public void sendOk() {
//        try {
//            JSONObject resp = new JSONObject();
//            resp.put("status", 200);
//            JSONObject body = new JSONObject();
//            body.put("type", "ok");
//            resp.put("body", body);
//
//            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
//            socketSession.getRemote().sendString(resp.toJSONString());
//        } catch (Exception e) {
//            logger.error("GameWebSocket@" + hashCode(), e);
//        }
//    }

//    public void sendState(Map<String, Player> players) {
//        try {
//            JSONObject resp = new JSONObject();
//            resp.put("status", 200);
//            JSONObject body = new JSONObject();
//            body.put("type", "state");
//            JSONArray jsonPlayers = new JSONArray();
//            players.entrySet().stream().forEach(entry -> {
//                JSONObject player = new JSONObject();
//                player.put("bet", entry.getValue().getBet());
//                player.put("score", entry.getValue().getScore());
//                player.put("name", entry.getKey());
//
//                JSONArray cards = new JSONArray();
//                entry.getValue().getCards().stream().forEach(card -> cards.add(card));
//                player.put("cards", cards);
//
//                jsonPlayers.add(player);
//            });
//            body.put("players", jsonPlayers);
//            resp.put("body", body);
//
//            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
//            socketSession.getRemote().sendString(resp.toJSONString());
//        } catch (Exception e) {
//            logger.error("GameWebSocket@" + hashCode(), e);
//        }
//    }
//
//    public void sendCard(String owner, Card card, int score) {
//        try {
//            JSONObject resp = new JSONObject();
//            resp.put("status", 200);
//            JSONObject body = new JSONObject();
//            body.put("type", "card");
//            body.put("owner", owner);
//            body.put("card", card.toString());
//            body.put("score", score);
//            resp.put("body", body);
//
//            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
//            socketSession.getRemote().sendString(resp.toJSONString());
//        } catch (Exception e) {
//            logger.error("GameWebSocket@" + hashCode(), e);
//        }
//    }
//
//    public void sendBet(String owner, int bet) {
//        try {
//            JSONObject resp = new JSONObject();
//            resp.put("status", 200);
//            JSONObject body = new JSONObject();
//            body.put("type", "bet");
//            body.put("owner", owner);
//            body.put("bet", bet);
//            resp.put("body", body);
//
//            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
//            socketSession.getRemote().sendString(resp.toJSONString());
//        } catch (Exception e) {
//            logger.error("GameWebSocket@" + hashCode(), e);
//        }
//    }
//
//    public void sendWins(Map<String, Integer> wins) {
//        try {
//            JSONObject resp = new JSONObject();
//            resp.put("status", 200);
//            JSONObject body = new JSONObject();
//            body.put("type", "wins");
//            JSONObject jsonWins = new JSONObject();
//            wins.entrySet().stream().forEach(entry -> jsonWins.put(entry.getKey(), entry.getValue()));
//            body.put("wins", jsonWins);
//            resp.put("body", body);
//
//            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
//            socketSession.getRemote().sendString(resp.toJSONString());
//        } catch (Exception e) {
//            logger.error("GameWebSocket@" + hashCode(), e);
//        }
//    }
//
//    public void sendDeckShuffle() {
//        try {
//            JSONObject resp = new JSONObject();
//            resp.put("status", 200);
//            JSONObject body = new JSONObject();
//            body.put("type", "shuffle");
//            resp.put("body", body);
//
//            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
//            socketSession.getRemote().sendString(resp.toJSONString());
//        } catch (Exception e) {
//            logger.error("GameWebSocket@" + hashCode(), e);
//        }
//    }
//
//    public void sendRemovePlayer(String removedUserName) {
//        try {
//            JSONObject resp = new JSONObject();
//            resp.put("status", 200);
//            JSONObject body = new JSONObject();
//            body.put("type", "exit");
//            body.put("player", removedUserName);
//            resp.put("body", body);
//
//            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
//            socketSession.getRemote().sendString(resp.toJSONString());
//        } catch (Exception e) {
//            logger.error("GameWebSocket@" + hashCode(), e);
//        }
//    }
//
//    public void sendNewPlayer(String newPlayerName) {
//        try {
//            JSONObject resp = new JSONObject();
//            resp.put("status", 200);
//            JSONObject body = new JSONObject();
//            body.put("type", "new");
//            body.put("player", newPlayerName);
//            resp.put("body", body);
//
//            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
//            socketSession.getRemote().sendString(resp.toJSONString());
//        } catch (Exception e) {
//            logger.error("GameWebSocket@" + hashCode(), e);
//        }
//    }
//
//    public void sendTurn(String player) {
//        try {
//            JSONObject resp = new JSONObject();
//            resp.put("status", 200);
//            JSONObject body = new JSONObject();
//            body.put("type", "turn");
//            body.put("player", player);
//            resp.put("body", body);
//
//            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
//            socketSession.getRemote().sendString(resp.toJSONString());
//        } catch (Exception e) {
//            logger.error("GameWebSocket@" + hashCode(), e);
//        }
//    }

    public void sendEnd() {
        try {
            JSONObject resp = new JSONObject();
            resp.put("status", 200);
            JSONObject body = new JSONObject();
            body.put("type", "END");
            resp.put("body", body);

            logger.info("Sending to '{}' resp: {}", userName, resp.toJSONString());
            socketSession.getRemote().sendString(resp.toJSONString());
        } catch (Exception e) {
            logger.error("PhoneWebSocket@" + hashCode(), e);
        }
    }

    public String getUserName() {
        return userName;
    }

}
