// TODO: safe checks (current phase, player in playing ...)
// TODO: send for players not playing
package game;

import base.AccountService;
import base.WebSocketService;
import main.Context;

import java.util.*;

public class GameTable {

    private enum GamePhase { BET, PLAY, END }

    private WebSocketService webSocketService;
    private AccountService accountService;

    private Deck deck = new Deck();

    // Заполнение null'ами важно при удалении игрока для сохранения позиций в массиве
    private List<String> players = new ArrayList<>(Collections.nCopies(3, null));
    private List<String> playing = new ArrayList<>(Collections.nCopies(3, null));
    private List<Integer> bets = new ArrayList<>(Collections.nCopies(3, null));
    private List<Integer> scores = new ArrayList<>(Collections.nCopies(3, null));

    private boolean isFull = false;
    private GamePhase currentPhase = GamePhase.BET;
    private int currentPlayer;


    public GameTable() {
        this.webSocketService = (WebSocketService) Context.getInstance().get(WebSocketService.class);
        this.accountService = (AccountService) Context.getInstance().get(AccountService.class);
    }

    public boolean isFull() {
        return this.isFull;
    }

    public void addUser(String userSessionId) {
        players.set(players.indexOf(null), userSessionId);

        if (Collections.frequency(players, null) == 0) {
            isFull = true;
        }

        if (currentPhase == GamePhase.BET) {
            webSocketService.sendPhase(userSessionId, GamePhase.BET.name());
        }
    }

    public void removeUser(String userSessionId) {
//        TODO: send remove player
        players.set(players.indexOf(userSessionId), null);
        isFull = false;
    }

    public void makeBet(String userSessionId, int bet) throws GameTableException {
        if (currentPhase != GamePhase.BET) {
            throw new GameTableException("Not bet time");
        }

        int indexOfPlayer = players.indexOf(userSessionId);
        if (indexOfPlayer == -1) {
//            GameMech works wrong
            throw new GameTableException("No such player at this table");
        }
//        TODO: check bet
//        if (accountService.getMoney(user) < bet) ...
//              sendError(user)?
        bets.set(indexOfPlayer, bet);
        webSocketService.sendOk(userSessionId);
        playing.set(indexOfPlayer, userSessionId);

        if (Collections.frequency(players, null) == Collections.frequency(playing, null)) {
            currentPhase = GamePhase.PLAY;
            for(String user : playing) {
                if (user != null) {
                    webSocketService.sendPhase(user, GamePhase.END.name());
                }
            }
            startGame();
        }
    }

    public void hit(String userSessionId) throws GameTableException {
        if (currentPhase != GamePhase.PLAY) {
            return;
        }

        int indexOfPlayer = players.indexOf(userSessionId);
        if (indexOfPlayer == -1) {
            return;
        }

        String card = deck.getCard();
        scores.set(indexOfPlayer, scores.get(indexOfPlayer) + 6);
        for (String user : playing) {
            if (user != null) {
                webSocketService.sendCard(user, card, indexOfPlayer); // 3 - dealer index
            }
        }
        if (scores.get(indexOfPlayer) > 21) {
            webSocketService.sendPhase(userSessionId, GamePhase.END.name());
            processStep();
        }
        webSocketService.sendOk(userSessionId);
    }

    public void stand(String userSessionId) {
        webSocketService.sendPhase(userSessionId, GamePhase.END.name());
        processStep();
    }

    private void processWins() {
        currentPhase = GamePhase.BET;
        for (String user : players) {
            if (user != null) {
                webSocketService.sendWins(/*user, winnings*/);
                webSocketService.sendPhase(user, GamePhase.BET.name());
            }
        }
    }

    private void startGame() {
        // в два круга сдается по одной карте и отсылается всем
        for(int i = 0; i < 2; i++) {
            for (int j = 0; i < playing.size(); i++) {
                if (playing.get(j) != null) {
                    String card = deck.getCard();
                    scores.set(j, scores.get(j) + 6); // TODO: card.getValue() вместо константы
                    for (String user : playing) {
                        if (user != null) {
                            webSocketService.sendCard(user, card, j);
                        }
                    }
                }
            }
        }

        // одна карта дилеру
        String card = deck.getCard();
        for (String user : playing) {
            if (user != null) {
                webSocketService.sendCard(user, card, 3); // 3 - dealer index
            }
        }

        currentPlayer = 0;
        processStep();
    }

    private void processStep() {
        while (playing.get(currentPlayer) == null) {
            currentPlayer++;
//           TODO: if currentPlayer > size ... processWins()
        }
        webSocketService.sendPhase(playing.get(currentPlayer), GamePhase.PLAY.name());
    }
}