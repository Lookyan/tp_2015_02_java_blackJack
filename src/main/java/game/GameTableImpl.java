package game;

import base.*;
import frontend.messages.*;
import main.Context;
import messageSystem.MessageSystem;
import resourceSystem.GameConfig;
import resourceSystem.ResourceFactory;

import java.util.*;

public class GameTableImpl implements GameTable {

    private int MAX_PLAYERS;
    private String DEALER_NAME;

    // Фазы игры
    private static enum GamePhase { BET, PLAY }

//    private WebSocketService webSocketService;
    private DBService dbService;
    private MessageSystem messageSystem;
    private GameMechanics gameMechanics;

    // Колода
    private Deck deck;

    // Дилер и игроки
    private Player dealer = new Player();
    private Map<String, Player> players = new HashMap<>();

    // Состояние игры
    private Queue<String> playingQueue = new LinkedList<>();
    private String currentPlayer;
    private GamePhase currentPhase = GamePhase.BET;

    public GameTableImpl(Context context, Deck deck, GameMechanics gameMechanics) {
        GameConfig config = (GameConfig) ResourceFactory.getInstance().get("data/game_config.xml");
        MAX_PLAYERS = config.getMaxPlayers();
        DEALER_NAME = config.getDealerName();

        this.deck = deck;
//        this.webSocketService = (WebSocketService) context.get(WebSocketService.class);
        this.dbService = (DBService) context.get(DBService.class);
        this.messageSystem = (MessageSystem) context.get(MessageSystem.class);
        this.gameMechanics = gameMechanics;

    }

    @Override
    public boolean isFull() {
        return players.size() == MAX_PLAYERS;
    }

    @Override
    public void addUser(String userName) throws GameTableException {
        if (!isFull()) {
            players.entrySet().stream().forEach(entry -> messageSystem.sendMessage(new MessageSendNewPlayer(
                    gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), entry.getKey(), userName
            )));

            players.put(userName, new Player());
            Map<String, Player> state = new HashMap<>(players);
            state.put(DEALER_NAME, dealer);
            messageSystem.sendMessage(new MessageSendState(
                    gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), userName, state
            ));
//            webSocketService.sendState(userName, state);

            // Если еще идет фаза ставок, игрок может присоединиться к игре
            if (currentPhase == GamePhase.BET) {
                messageSystem.sendMessage(new MessageSendPhase(
                        gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), userName, GamePhase.BET.name()
                ));
//                webSocketService.sendPhase(userName, GamePhase.BET.name());
            }
        } else {
            throw new GameTableException("Can't add new user, table is full!");
        }
    }

    @Override
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
        int playerChips = dbService.getChipsByName(userName);
        if (playerChips < bet) {
            bet = playerChips;
        }
        player.setBet(bet);
        player.setPlaying(true);

        // Отсылаем всем сделанную ставку
//        players.keySet().stream().forEach(p -> webSocketService.sendBet(p, userName, bet)); // error
        for (String p : players.keySet()) {
            messageSystem.sendMessage(new MessageSendBet(
                    gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), p, userName, bet
            ));
//            webSocketService.sendBet(p, userName, bet);
        }
        messageSystem.sendMessage(new MessageSendEnd(
                gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), userName
        ));
//        webSocketService.sendEnd(userName);

        // Если все сделали ставки - начинаем игру
        if (isAllPlaying()) {
            startGame();
        }
    }

    @Override
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
            messageSystem.sendMessage(new MessageSendCard(
                    gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), user, userName, card, player.getScore()
            ));
//            webSocketService.sendCard(user, userName, card, player.getScore());
        }

        // Если очки > 21 заканчиваем с ним, иначе делаем еще приглашение
        if (player.getScore() > 21) {
            messageSystem.sendMessage(new MessageSendEnd(
                    gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), userName
            ));
//            webSocketService.sendEnd(userName);
            processStep();
        } else {
            messageSystem.sendMessage(new MessageSendPhase(
                    gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), userName, GamePhase.PLAY.name()
            ));
//            webSocketService.sendPhase(userName, GamePhase.PLAY.name());
        }
    }

    @Override
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

        messageSystem.sendMessage(new MessageSendEnd(
                gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), userName
        ));
//        webSocketService.sendEnd(userName);
        processStep();
    }

    @Override
    public void removeUser(String userName) throws GameTableException {
        if (!players.containsKey(userName)) {
            throw new GameTableException("Can't remove user, no such player at this table");
        }

        Player removedPlayer =  players.remove(userName);

        for (String user : players.keySet()) {
            messageSystem.sendMessage(new MessageSendRemovePlayer(
                    gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), user, userName
            ));
//            webSocketService.sendRemovePlayer(user, userName);
        }

        if (playingQueue.contains(userName)) {
            playingQueue.remove(userName);
        }

        if (currentPhase == GamePhase.PLAY && currentPlayer != null && currentPlayer.compareTo(userName) == 0) {
            processStep();
        } else if (currentPhase == GamePhase.BET && isAllPlaying()) {
            startGame();
        }

        dbService.subChipsByName(userName, removedPlayer.getBet());
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
                    messageSystem.sendMessage(new MessageSendCard(
                            gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), userName, entry.getKey(), card, entry.getValue().getScore()
                    ));
//                    webSocketService.sendCard(userName, entry.getKey(), card, entry.getValue().getScore());
                }
            });
        }
        // одна карта дилеру
        Card card = getCard();
        dealer.addCard(card);
        players.keySet().stream().forEach(player ->
                messageSystem.sendMessage(new MessageSendCard(
                        gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), player, DEALER_NAME, card, dealer.getScore()
                ))
//                webSocketService.sendCard(player, DEALER_NAME, card, dealer.getScore())
        );

        processStep();
    }

    private void processStep() {
//        Вытаскиваем игроков пока не найдем играющего
        do {
            currentPlayer = playingQueue.poll();
        } while (currentPlayer != null && !players.get(currentPlayer).isPlaying());

        if (currentPlayer != null) {
            messageSystem.sendMessage(new MessageSendPhase(
                    gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), currentPlayer, GamePhase.PLAY.name()
            ));
//            webSocketService.sendPhase(currentPlayer, GamePhase.PLAY.name());
            players.keySet().stream().filter(name -> name.compareTo(currentPlayer) != 0).forEach(name ->
                            messageSystem.sendMessage(new MessageSendTurn(
                                    gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), name, currentPlayer
                            ))
//                    webSocketService.sendTurn(name, currentPlayer)
            );
        } else {
//            Если очередь пуста - обрабатываем результаты
            finishGame();
        }
    }

    private void finishGame() {
        while (dealer.getScore() < 17) {
            Card card = getCard();
            dealer.addCard(card);
            players.keySet().stream().forEach(player ->
//                    webSocketService.sendCard(player, DEALER_NAME, card, dealer.getScore())
                    messageSystem.sendMessage(new MessageSendCard(
                            gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), player, DEALER_NAME, card, dealer.getScore()
                    ))
            );
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
                dbService.subChipsByName(entry.getKey(), player.getBet());
                wins.put(entry.getKey(), -player.getBet());
            } else if (dealerScore > 21 || dealerScore < playerScore) {
                dbService.addChipsByName(entry.getKey(), player.getBet());
                wins.put(entry.getKey(), player.getBet());
            } else {
                wins.put(entry.getKey(), 0);
            }
            player.reset();

        });
        dealer.reset();

        players.keySet().stream().filter(player -> player != null).forEach(player ->
//                webSocketService.sendWins(player, wins)
                messageSystem.sendMessage(new MessageSendWins(
                        gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), player, wins
                ))
        );

        currentPhase = GamePhase.BET;
        players.keySet().stream().forEach(player ->
//                webSocketService.sendPhase(player, GamePhase.BET.name())
                messageSystem.sendMessage(new MessageSendPhase(
                        gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), player, GamePhase.BET.name()
                ))
        );
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
            players.keySet().stream().forEach(player ->
//                    webSocketService.sendDeckShuffle(player)
                    messageSystem.sendMessage(new MessageSendDeckShuffle(
                            gameMechanics.getAddress(), messageSystem.getAddressService().getWebSocketService(), player
                    ))
            );
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