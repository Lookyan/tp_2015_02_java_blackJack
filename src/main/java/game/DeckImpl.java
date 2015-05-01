package game;

import base.Deck;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DeckImpl implements Deck {
    private Queue<Card> deck = new LinkedList<>();


    private static final char[] suits = {'s', 'c', 'h', 'd'}; // spades, clubs, hearts, diamonds
    private static final char[] ranks = {'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'}; // ace, king, queen, jack, ten, ...

    public DeckImpl() {
        fillDeck();
    }

    @Override
    public boolean isEmpty() {
        return deck.isEmpty();
    }

    @Override
    public Card getCard() {
        return deck.remove();
    }

    @Override
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
