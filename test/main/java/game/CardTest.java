package game;

import org.junit.Test;

import javax.xml.stream.events.Characters;

import static org.junit.Assert.*;

public class CardTest {

    @Test
    public void testIsAce() throws Exception {
        Card card = new Card('A', 'd');
        assertTrue(card.isAce());

        card = new Card('A', 's');
        assertTrue(card.isAce());

        card = new Card('T', 'd');
        assertFalse(card.isAce());

        card = new Card('5', 's');
        assertFalse(card.isAce());
    }

    @Test
    public void testChangeAceValue() throws Exception {
        Card card = new Card('A', 'd');

        assertEquals(11, card.getValue());
        card.changeAceValue();
        assertEquals(1, card.getValue());
        card.changeAceValue();
        assertEquals(11, card.getValue());
    }

    @Test
    public void testGetValue() throws Exception {
        Card card = new Card('A', 'd');
        assertEquals(11, card.getValue());

        card = new Card('K', 'd');
        assertEquals(10, card.getValue());

        card = new Card('Q', 'd');
        assertEquals(10, card.getValue());

        card = new Card('J', 'd');
        assertEquals(10, card.getValue());

        card = new Card('T', 'd');
        assertEquals(10, card.getValue());

        for (int i = 9; i >= 2; i-- ) {
            card = new Card(Integer.toString(i).charAt(0), 'd');
            assertEquals(i, card.getValue());
        }
    }
}