package game;

import base.AccountService;
import base.WebSocketService;
import main.Context;

import java.util.*;
import java.util.Map.Entry;

public class GameTable {

    private static final int PLAYERS_QUANTITY = 3;
    private static final String DEALER_NAME = "#dealer";

    private static enum GamePhase { BET, PLAY, END }

    private WebSocketService webSocketService;
    private AccountService accountService;

    private Deck deck = new Deck();

    // Заполнение null'ами важно при удалении игрока для сохранения позиций в массиве
//    private List<String> players = new ArrayList<>(Collections.nCopies(PLAYERS_QUANTITY, null));
    private Map<String, Player> players = new HashMap<>();
    private Queue<String> playingQueue = new LinkedList<>();

//    private List<String> playing = new ArrayList<>(Collections.nCopies(PLAYERS_QUANTITY, null));
//    private List<Integer> bets = new ArrayList<>(Collections.nCopies(PLAYERS_QUANTITY, null));
//    private List<Integer> cardScores = new ArrayList<>(Collections.nCopies(PLAYERS_QUANTITY, null));

    private String currentPlayer;
    private GamePhase currentPhase = GamePhase.BET;

    public GameTable(Context context) {
        this.webSocketService = (WebSocketService) context.get(WebSocketService.class);
        this.accountService = (AccountService) context.get(AccountService.class);
    }

    public boolean isFull() {
        return players.size() == PLAYERS_QUANTITY;
    }

    public void addUser(String userName) throws GameTableException {
        if (!isFull()) {
            players.put(userName, new Player(userName, accountService.getChips(userName)));

            if (currentPhase == GamePhase.BET) {
                webSocketService.sendPhase(userName, GamePhase.BET.name());
            }
        } else {
            throw new GameTableException("Can't add new user, table is full!");
        }
    }

    public void makeBet(String userName, int bet) throws GameTableException {
        if (currentPhase != GamePhase.BET) {
            throw new GameTableException("Can't make bet, not bet time");
        }
        if (!players.containsKey(userName)) {
            throw new GameTableException("Can't make bet, no such player at this table");
        }

        Player player = players.get(userName);
        if (player.getChips() < bet) {
            webSocketService.sendError(userName);
        } else {
            player.setBet(bet);
            player.setPlaying(true);
            webSocketService.sendOk(userName);

            if (isAllPlaying()) {
                startGame();
            }
        }
    }

    public void hit(String userName) throws GameTableException {
        if (currentPhase != GamePhase.PLAY) {
            throw new GameTableException("Can't hit, not playing time");
        }
        if (!players.containsKey(userName)) {
            throw new GameTableException("Can't hit, no such player at this table");
        }
        if (userName.compareTo(currentPlayer) != 0) {
            throw new GameTableException("Can't hit, wrong player");
        }

        Card card = getCard();
        Player player = players.get(currentPlayer);
        player.addCard(card);

        for (String user : players.keySet()) {
            webSocketService.sendCard(user, userName, card);
        }

        if (player.getScore() > 21) {
            webSocketService.sendPhase(userName, GamePhase.END.name());
            processStep();
        } else {
            webSocketService.sendOk(userName);
        }
    }

    public void stand(String userName) throws GameTableException {
        if (currentPhase != GamePhase.PLAY) {
            throw new GameTableException("Can't stand, not playing time");
        }
        if (!players.containsKey(userName)) {
            throw new GameTableException("Can't stand, no such player at this table");
        }
        if (userName.compareTo(currentPlayer) != 0) {
            throw new GameTableException("Can't stand, wrong player");
        }

        webSocketService.sendPhase(userName, GamePhase.END.name());
        processStep();
    }

    public void removeUser(String userName) throws GameTableException {
        if (!players.containsKey(userName)) {
            throw new GameTableException("Can't remove user, no such player at this table");
        }

        players.remove(userName);
        if (playingQueue.contains(userName)) {
            playingQueue.remove(userName);
        }

        for (String user : players.keySet()) {
            webSocketService.sendRemovePlayer(user, userName);
        }

//        if (currentPlayer.compareTo(userName) == 0) {
//            TODO
//        }

//        if (players.contains(userName)) {
//            for (String user : players) {
//                if (!user.equals(userName)) {
//                    webSocketService.sendRemovePlayer(user, userName); // TODO: send index
//                }
//            }
//            int index = players.indexOf(userName);
//            players.set(index, null);
//            playing.set(index, null);
//            bets.set(index, null);
//        } else {
//            throw new GameTableException("Can't remove player, no such player at this table");
//        }
    }

    private void startGame() {
        currentPhase = GamePhase.PLAY;
        playingQueue.clear();
        playingQueue.addAll(players.keySet());

        // в два круга сдается по одной карте и отсылается всем
        for(int i = 0; i < 2; i++) {
            for (Entry<String, Player> entry : players.entrySet()) {
                if (entry.getValue().isPlaying()) {
                    Card card = getCard();
                    entry.getValue().addCard(card);
                    for (String userName : players.keySet()) {
                        webSocketService.sendCard(userName, entry.getKey(), card);
                    }
                }
            }
        }
        // одна карта дилеру
        Card card = getCard();
        for (String userName : players.keySet()) {
            webSocketService.sendCard(userName, DEALER_NAME, card); // 3 - dealer index
        }

        processStep();
    }

    private void processStep() {
        if (!playingQueue.isEmpty()) {
            do {
                currentPlayer = playingQueue.poll();
            } while (!players.get(currentPlayer).isPlaying());
            webSocketService.sendPhase(currentPlayer, GamePhase.PLAY.name());
        } else {
            processWins();
        }
    }

    private void processWins() {
//        TODO
//        currentPhase = GamePhase.BET;
//        for (String user : players) {
//            if (user != null) {
//                webSocketService.sendWins(/*user, winnings*/);
//                webSocketService.sendPhase(user, GamePhase.BET.name());
//            }
//        }
    }

    private boolean isAllPlaying() {
        for (Player player : players.values()) {
            if (!player.isPlaying()) {
                return false;
            }
        }
        return true;
    }

    private Card getCard() {
//        TODO
        return new Card('4','d');
    }

    private void shuffleDeck() {
//        TODO
//        for (String user : players) {
//            webSocketService.sendDeckShuffle(user);
//        }
    }

    @Override
    public String toString() {
        return "GameTable@" + hashCode() + "{" +
                "deck=" + deck +
                ", players=" + players +
                ", playingQueue=" + playingQueue +
                ", currentPlayer='" + currentPlayer + '\'' +
                ", currentPhase=" + currentPhase +
                '}';
    }
}