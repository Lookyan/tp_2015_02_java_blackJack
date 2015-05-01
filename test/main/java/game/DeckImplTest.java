package game;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeckImplTest {

    @Test
    public void testIsEmpty() throws Exception {
        DeckImpl deck = new DeckImpl();
        assertFalse(deck.isEmpty());
        for (int i = 0; i < 52; i++) {
            deck.getCard();
        }
        assertTrue(deck.isEmpty());
    }

    @Test
    public void testFillDeck() throws Exception {
        DeckImpl deck = new DeckImpl();
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