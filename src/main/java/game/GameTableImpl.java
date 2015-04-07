package game;

import interfaces.GameTable;

import java.util.*;

public class GameTableImpl implements GameTable {

    private enum GamePhase { BET, PLAY, FINISH }

    private Set<String> players = new LinkedHashSet<>();
    private Set<String> playing = new LinkedHashSet<>();

    private boolean isFull = false;
    private GamePhase currentPhase;
    private Iterator currentPlaying;

    public void addUser(String userSessionId) {
        players.add(userSessionId);
        if (players.size() == 3) {
            isFull = true;
        }
    }

    public String processStep() {
//        if (currentPlaying == null) {
//            currentPlaying = playing.iterator();
//        }
//
//        if (currentPlaying.hasNext()) {
//            String nextPlayer = (String) currentPlaying.next();
//            return nextPlayer;
//        } else {
//            // TODO: calc winnings and update scores
//        }
        return "";
    }

    public boolean isFull() {
        return this.isFull;
    }

}