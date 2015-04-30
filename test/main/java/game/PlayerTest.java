package game;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PlayerTest {

    @Test
    public void testAddCard() throws Exception {
        Player player = new Player();

        List<Card> cards = player.getCards();
        assertEquals(0, cards.size());

        player.addCard(new Card('5', 's'));

        cards = player.getCards();
        assertEquals(1, cards.size());
        assertEquals("5s", cards.get(0).toString());

        player.addCard(new Card('K', 'h'));
        cards = player.getCards();
        assertEquals(2, cards.size());
        assertEquals("5s", cards.get(0).toString());
        assertEquals("Kh", cards.get(1).toString());
    }

    @Test
    public void testSetPlaying() throws Exception {
        Player player = new Player();
        assertFalse(player.isPlaying());

        player.setPlaying(true);
        assertTrue(player.isPlaying());

        player.setPlaying(false);
        assertFalse(player.isPlaying());
    }

    @Test
    public void testSetBet() throws Exception {
        Player player = new Player();
        assertEquals(0, player.getBet());

        player.setBet(123);
        assertEquals(123, player.getBet());

        player.setBet(1234);
        assertEquals(1234, player.getBet());
    }

    @Test
    public void testReset() throws Exception {
        Player player = new Player();

        player.setBet(123);
        player.setPlaying(true);
        player.addCard(new Card('5', 's'));
        player.addCard(new Card('K', 'h'));
        player.reset();

        assertEquals(0, player.getBet());
        assertFalse(player.isPlaying());
        assertEquals(0, player.getCards().size());
    }
}