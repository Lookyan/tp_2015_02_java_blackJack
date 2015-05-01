package game;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private boolean isPlaying = false;

    private List<Card> cards = new ArrayList<>();

    private int score = 0;
    private int bet = 0;
    private boolean hasAce = false;

    public void addCard(Card card) {
        cards.add(card);
        if (card.isAce()) {
            if (hasAce) {
                card.changeAceValue();
            } else {
                hasAce = true;
            }
        }

        score += card.getValue();
        if (score > 21 && hasAce) {
            for (Card c : cards) {
                if (c.isAce() && c.getValue() == 11) {
                    c.changeAceValue();
                    score -= 10;
                    break;
                }
            }
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

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getBet() {
        return bet;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void reset() {
        bet = 0;
        score = 0;
        cards.clear();
        hasAce = false;
        isPlaying = false;
    }

    @Override
    public String toString() {
        return "\n\t\t\tPlayer@" + hashCode() + "{" +
                ", isPlaying=" + isPlaying +
                ", cards=" + cards +
                ", score=" + score +
                ", bet=" + bet +
                ", hasAce=" + hasAce +
                "}\n\t\t";
    }
}
