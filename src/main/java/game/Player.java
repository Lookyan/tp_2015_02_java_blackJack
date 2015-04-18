package game;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    private boolean isPlaying = true;

    private List<Card> cards = new ArrayList<>();

    private int chips;
    private int score = 0;
    private int bet = 0;
    private boolean hasAce = false;

    public Player(String name, int chips) {
        this.name = name;
        this.chips = chips;
    }

    public boolean addCard(Card card) {
        cards.add(card);
        if (card.isAce()) {
            if (hasAce) {
                card.changeAceValue();
            } else {
                hasAce = true;
            }
        }

        score += card.getValue();
        if (score > 21) {
            for (Card c : cards) {
                if (c.isAce()) {
                    if ( c.getValue() == 11) {
                        c.changeAceValue();
                        score -= 10;
                        break;
                    }
                }
            }
        }

        if ( score > 21) {
            return false;
        } else {
            return true;
        }
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getScore() {
        return score;
    }

    public void clearBet() {
        bet = 0;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getBet() {
        return bet;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

}
