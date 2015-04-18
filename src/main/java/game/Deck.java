package game;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Deck {
    private Queue<String> deck = new LinkedList<>();

//    private GameTable table;

    private char[] suits = {'s', 'c', 'h', 'd'}; // spades, clubs, hearts, diamonds
    private char[] ranks = {'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'}; // ace, king, queen, jack, ten, ...

    public Deck() {
//        this.table = table;
        fillDeck();
    }

    public int cardsRemaining() {
        return deck.size();
    }

    public String getCard() {
        if (cardsRemaining() == 0) {
//            table.shuffleDeck();
            fillDeck();
        }
        return deck.remove();
    }

    private void shuffle() {
        Collections.shuffle((List) deck);
    }

    private void fillDeck() {
        for(char suit : suits) {
            for(char rank : ranks) {
                deck.add("" + rank + suit);
            }
        }
        shuffle();
    }

}
