package game;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Deck {
    private Queue<Card> deck = new LinkedList<>();


    private char[] suits = {'s', 'c', 'h', 'd'}; // spades, clubs, hearts, diamonds
    private char[] ranks = {'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'}; // ace, king, queen, jack, ten, ...

    public Deck() {
        fillDeck();
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

    public Card getCard() {
        return deck.remove();
    }

    public void fillDeck() {
        for(char suit : suits) {
            for(char rank : ranks) {
                deck.add(new Card(rank, suit));
            }
        }
        Collections.shuffle((List) deck);
    }

    @Override
    public String toString() {
        return "Deck@" + hashCode() + "{" +
                "deck=" + deck +
                '}';
    }
}
