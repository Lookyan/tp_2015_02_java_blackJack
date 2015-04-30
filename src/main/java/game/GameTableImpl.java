package game;

import base.AccountService;
import base.GameTable;
import base.WebSocketService;
import main.Context;
import resourceSystem.GameConfig;
import resourceSystem.ResourceFactory;

import java.util.*;

public class GameTableImpl implements GameTable {

    private int MAX_PLAYERS;
    private String DEALER_NAME;

    // Фазы игры
//    TODO: избавиться от фазы END
    private static enum GamePhase { BET, PLAY, END }

    private WebSocketService webSocketService;
    private AccountService accountService;

    // Колода
    private Deck deck = new Deck();

    // Дилер и игроки
    private Player dealer = new Player();
    private Map<String, Player> players = new HashMap<>();

    // Состояние игры
    private Queue<String> playingQueue = new LinkedList<>();
    private String currentPlayer;
    private GamePhase currentPhase = GamePhase.BET;

    public GameTableImpl(Context context) {
        GameConfig config = (GameConfig) ResourceFactory.getInstance().get("data/game_config.xml");
        MAX_PLAYERS = config.getMaxPlayers();
        DEALER_NAME = config.getDealerName();

        this.webSocketService = (WebSocketService) context.get(WebSocketService.class);
        this.accountService = (AccountService) context.get(AccountService.class);
    }

    public boolean isFull() {
        return players.size() == MAX_PLAYERS;
    }

    public void addUser(String userName) throws GameTableException {
        if (!isFull()) {
            players.entrySet().stream().forEach(entry -> webSocketService.sendNewPlayer(entry.getKey(), userName));

            players.put(userName, new Player());
            Map<String, Player> state = new HashMap<>(players);
            state.put(DEALER_NAME, dealer);
            webSocketService.sendState(userName, state);

            // Если еще идет фаза ставок, игрок может присоединиться к игре
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
        if (player.isPlaying()) {
            throw new GameTableException("Can't update made bet");
        }

        // Если фишек у игрока меньше чем он пытается поставить - обрубаем
        if (accountService.getChips(userName) < bet) {
            bet = accountService.getChips(userName);
        }
        player.setBet(bet);
        player.setPlaying(true);

        // Отсылаем всем сделанную ставку
//        players.keySet().stream().forEach(p -> webSocketService.sendBet(p, userName, bet)); // error
        for (String p : players.keySet()) {
            webSocketService.sendBet(p, userName, bet);
        }

        // Если все сделали ставки - начинаем игру
        if (isAllPlaying()) {
            startGame();
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

        // Отсылаем всем карту игрока
        for (String user : players.keySet()) {
            webSocketService.sendCard(user, userName, card, player.getScore());
        }

//        TODO: использовать addCard
        // Если очки > 21 заканчиваем с ним, иначе делаем еще приглашение
        if (player.getScore() > 21) {
            webSocketService.sendPhase(userName, GamePhase.END.name());
            processStep();
        } else {
            webSocketService.sendPhase(userName, GamePhase.PLAY.name());
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
//        чекать на null
//            TODO
//        }

//        if (players.contains(userName)) {
//            for (String user : players) {
//                if (!user.equals(userName)) {
//                    webSocketService.sendRemovePlayer(user, userName);
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

        // Добавляем всех игроков в очередь
        playingQueue.clear();
        playingQueue.addAll(players.keySet());

        // в два круга сдается по одной карте и отсылается всем
        for(int i = 0; i < 2; i++) {
            players.entrySet().stream().filter(entry -> entry.getValue().isPlaying()).forEach(entry -> {
                Card card = getCard();
                entry.getValue().addCard(card);
                for (String userName : players.keySet()) {
                    webSocketService.sendCard(userName, entry.getKey(), card, entry.getValue().getScore());
                }
            });
        }
        // одна карта дилеру
        Card card = getCard();
        dealer.addCard(card);
        players.keySet().stream().forEach(player ->
                webSocketService.sendCard(player, DEALER_NAME, card, dealer.getScore()));

        processStep();
    }

    private void processStep() {
        if (!playingQueue.isEmpty()) {
            // Вытаскиваем игроков пока не найдем играющего
            do {
                currentPlayer = playingQueue.poll();
            } while (!players.get(currentPlayer).isPlaying());
            webSocketService.sendPhase(currentPlayer, GamePhase.PLAY.name());
            players.keySet().stream().filter(name -> name.compareTo(currentPlayer) != 0).forEach(name -> webSocketService.sendTurn(name, currentPlayer));
        } else {
            // Если очередь пуста - обрабатываем результаты
            finishGame();
        }
    }

    private void finishGame() {
        while (dealer.getScore() < 17) {
            Card card = getCard();
            dealer.addCard(card);
            players.keySet().stream().forEach(player ->
                    webSocketService.sendCard(player, DEALER_NAME, card, dealer.getScore()));
        }

        int dealerScore = dealer.getScore();
        Map<String, Integer> wins = new HashMap<>();
        players.entrySet().stream().filter(entry -> entry.getValue().isPlaying()).forEach(entry -> {
            Player player = entry.getValue();
            int playerScore = player.getScore();

/*          |  d  |  p  |  res
            +-----+-----+------
            | >21 | >21 | loose
            | >21 |<=21 | win
            |<=21 | >21 | loose
            |<=21 |<=21 |->|\__ d = p -> none
                           |\__ d > p -> loose
                           \___ d < p -> win
*/
            if (playerScore > 21 || dealerScore <= 21 && dealerScore > playerScore) {
                accountService.subChips(entry.getKey(), player.getBet());
                wins.put(entry.getKey(), -player.getBet());
            } else if (dealerScore > 21 || dealerScore < playerScore) {
                accountService.addChips(entry.getKey(), player.getBet());
                wins.put(entry.getKey(), player.getBet());
            } else {
                wins.put(entry.getKey(), 0);
            }
            player.reset();

        });
        dealer.reset();

        players.keySet().stream().filter(player -> player != null).forEach(player -> webSocketService.sendWins(player, wins));

        currentPhase = GamePhase.BET;
        players.keySet().stream().forEach(player -> webSocketService.sendPhase(player, GamePhase.BET.name()));
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
        if (deck.isEmpty()) {
            players.keySet().stream().forEach(player -> webSocketService.sendDeckShuffle(player));
            deck.fillDeck();
        }
        return deck.getCard();
    }

    @Override
    public String toString() {
        return "\n\tGameTable@" + hashCode() + "{" +
                "\n\t\tdeck=" + deck +
                ",\n\t\tplayers=" + players +
                ", playingQueue=" + playingQueue +
                ", currentPlayer='" + currentPlayer + '\'' +
                ", currentPhase=" + currentPhase +
                '}';
    }
}