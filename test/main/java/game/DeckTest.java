package game;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeckTest {

    @Test
    public void testIsEmpty() throws Exception {
        Deck deck = new Deck();
        assertFalse(deck.isEmpty());
        for (int i = 0; i < 52; i++) {
            deck.getCard();
        }
        assertTrue(deck.isEmpty());
    }

    @Test
    public void testFillDeck() throws Exception {
        Deck deck = new Deck();
        assertFalse(deck.isEmpty());
        for (int i = 0; i < 52; i++) {
            deck.getCard();
        }
        assertTrue(deck.isEmpty());

        deck.fillDeck();
        assertFalse(deck.isEmpty());
        for (int i = 0; i < 52; i++) {
            deck.getCard();
        }
        assertTrue(deck.isEmpty());
    }
}