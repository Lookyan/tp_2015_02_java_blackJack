package game;

import base.*;
import main.Context;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import resourceSystem.ResourceFactory;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class GameTableImplTest {

    private Context context;
    private WebSocketService webSocketServiceMock;
    private DBService dbServiceMock;

    private class DeckStub implements Deck {

        private final char[] ranks = {'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'};
        private final char[] suits = {'s', 'c', 'h', 'd'};

        private Random r = new Random();
        private Queue<Card> nextCards = new LinkedList<>();
//        private Card nextCard = new Card(ranks[r.nextInt(ranks.length)], suits[r.nextInt(suits.length)]);

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Card getCard() {
            if (nextCards.isEmpty()) {
                nextCards.add(new Card(ranks[r.nextInt(ranks.length)], suits[r.nextInt(suits.length)]));
            }
            return nextCards.remove();
        }

        @Override
        public void fillDeck() {}

        public void addNextCards(String... cards) {
            for (String card : cards) {
                nextCards.add(new Card(card.charAt(0), card.charAt(1)));
            }
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        ResourceFactory.getInstance().init("data");
    }

    @Before
    public void setUp() throws Exception {
        context = new Context();
        dbServiceMock = mock(DBService.class);
        when(dbServiceMock.getChipsByName(anyString())).thenReturn(1000);

        webSocketServiceMock = mock(WebSocketService.class);

        context.add(WebSocketService.class, webSocketServiceMock);
        context.add(DBService.class, dbServiceMock);
    }

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAddUser() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckStub());

        gameTable.addUser("andrey");
        verify(webSocketServiceMock).sendState(eq("andrey"), anyMap());
        verify(webSocketServiceMock).sendPhase("andrey", "BET");

        gameTable.addUser("alex");
        verify(webSocketServiceMock).sendNewPlayer("andrey", "alex");
        verify(webSocketServiceMock).sendState(eq("alex"), anyMap());
        verify(webSocketServiceMock).sendPhase("alex", "BET");

        gameTable.addUser("bob");
        verify(webSocketServiceMock).sendNewPlayer("andrey", "bob");
        verify(webSocketServiceMock).sendNewPlayer("alex", "bob");
        verify(webSocketServiceMock).sendState(eq("bob"), anyMap());
        verify(webSocketServiceMock).sendPhase("bob", "BET");

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't add new user, table is full!");
        gameTable.addUser("chris");
    }

    @Test
    public void testAddUserAtPlay() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckStub());
        gameTable.addUser("andrey");
        gameTable.addUser("alex");
        gameTable.makeBet("andrey", 100);
        gameTable.makeBet("alex", 200);
        verify(webSocketServiceMock).sendPhase(anyString(), eq("PLAY"));

        gameTable.addUser("bob");
        verify(webSocketServiceMock).sendNewPlayer("andrey", "bob");
        verify(webSocketServiceMock).sendNewPlayer("alex", "bob");
        verify(webSocketServiceMock).sendState(eq("bob"), anyMap());
        verify(webSocketServiceMock, never()).sendPhase("bob", "BET");
    }

    @Test
    public void testMakeBetOnePlayer() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckStub());
        gameTable.addUser("andrey");

        gameTable.makeBet("andrey", 100);
        verify(dbServiceMock).getChipsByName("andrey");
        verify(webSocketServiceMock).sendBet("andrey", "andrey", 100);
    }

    @Test
    public void testMakeBetNoPlayer() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckStub());
        gameTable.addUser("andrey");

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't make bet, no such player at this table");
        gameTable.makeBet("bob", 100);
    }

    @Test
    public void testMakeBetNotBetTime() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckStub());
        gameTable.addUser("andrey");
        gameTable.makeBet("andrey", 100);

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't make bet, not bet time");
        gameTable.makeBet("andrey", 100);
    }

    @Test
    public void testMakeBetNoUpdate() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckStub());
        gameTable.addUser("andrey");
        gameTable.addUser("alex");
        gameTable.makeBet("andrey", 100);

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't update made bet");
        gameTable.makeBet("andrey", 200);
    }

    @Test
    public void testMakeBetTwoPlayers() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckStub());
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        gameTable.makeBet("andrey", 100);
        verify(dbServiceMock).getChipsByName("andrey");
        verify(webSocketServiceMock).sendBet("andrey", "andrey", 100);
        verify(webSocketServiceMock).sendBet("alex", "andrey", 100);

        gameTable.makeBet("alex", 2000);
        verify(dbServiceMock).getChipsByName("alex");
        verify(webSocketServiceMock).sendBet("andrey", "alex", 1000);
        verify(webSocketServiceMock).sendBet("alex", "alex", 1000);
    }

    @Test
    public void testStartGameOnePlayer() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckStub());
        gameTable.addUser("andrey");
        gameTable.makeBet("andrey", 100);

//        startGame
        verify(webSocketServiceMock, times(2)).sendCard(eq("andrey"), eq("andrey"), any(Card.class), anyInt());
        verify(webSocketServiceMock).sendCard(eq("andrey"), eq("#dealer"), any(Card.class), anyInt());

//        processStep
        verify(webSocketServiceMock).sendPhase("andrey", "PLAY");
    }

    @Test
    public void testStartGameTwoPlayers() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckStub());
        gameTable.addUser("andrey");
        gameTable.addUser("alex");
        gameTable.makeBet("andrey", 100);
        gameTable.makeBet("alex", 2000);

//        startGame
        verify(webSocketServiceMock, times(2)).sendCard(eq("andrey"), eq("andrey"), any(Card.class), anyInt());
        verify(webSocketServiceMock, times(2)).sendCard(eq("alex"), eq("andrey"), any(Card.class), anyInt());
        verify(webSocketServiceMock, times(2)).sendCard(eq("andrey"), eq("alex"), any(Card.class), anyInt());
        verify(webSocketServiceMock, times(2)).sendCard(eq("alex"), eq("alex"), any(Card.class), anyInt());
        verify(webSocketServiceMock).sendCard(eq("andrey"), eq("#dealer"), any(Card.class), anyInt());
        verify(webSocketServiceMock).sendCard(eq("alex"), eq("#dealer"), any(Card.class), anyInt());

//        processStep
        ArgumentCaptor<String> player = ArgumentCaptor.forClass(String.class);
        verify(webSocketServiceMock).sendPhase(player.capture(), eq("PLAY"));
        verify(webSocketServiceMock).sendTurn(player.getValue().compareTo("alex") == 0 ? "andrey" : "alex", player.getValue());
    }

    @Test
    public void testHitOnePlayer() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");

        deck.addNextCards("5d", "Th");
        gameTable.makeBet("andrey", 100);

        ArgumentCaptor<Integer> scoreCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(webSocketServiceMock, times(2)).sendCard(eq("andrey"), eq("andrey"), any(Card.class), scoreCaptor.capture());
        assertEquals(15, scoreCaptor.getValue().intValue());

        deck.addNextCards("5s");
        gameTable.hit("andrey");
        verify(webSocketServiceMock, times(3)).sendCard(eq("andrey"), eq("andrey"), any(Card.class), scoreCaptor.capture());
        assertEquals(20, scoreCaptor.getValue().intValue());
        verify(webSocketServiceMock, times(2)).sendPhase("andrey", "PLAY");

        deck.addNextCards("5d");
        gameTable.hit("andrey");
        verify(webSocketServiceMock, times(4)).sendCard(eq("andrey"), eq("andrey"), any(Card.class), scoreCaptor.capture());
        assertEquals(25, scoreCaptor.getValue().intValue());
        verify(webSocketServiceMock).sendEnd("andrey");
    }

    @Test
    public void testHitTwoPlayers() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        deck.addNextCards("Kd", "Th", "Qs", "Jc");
        gameTable.makeBet("andrey", 100);
        gameTable.makeBet("alex", 200);

        ArgumentCaptor<Integer> andreyScoreCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> alexScoreCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(webSocketServiceMock, times(2)).sendCard(eq("andrey"), eq("andrey"), any(Card.class), andreyScoreCaptor.capture());
        verify(webSocketServiceMock, times(2)).sendCard(eq("andrey"), eq("alex"), any(Card.class), anyInt());
        verify(webSocketServiceMock, times(2)).sendCard(eq("alex"), eq("andrey"), any(Card.class), anyInt());
        verify(webSocketServiceMock, times(2)).sendCard(eq("alex"), eq("alex"), any(Card.class), alexScoreCaptor.capture());

        ArgumentCaptor<String> firstPlayer = ArgumentCaptor.forClass(String.class);
        verify(webSocketServiceMock).sendPhase(firstPlayer.capture(), eq("PLAY"));
        String first = firstPlayer.getValue(), second = first.compareTo("andrey") == 0 ? "alex" : "andrey";

        deck.addNextCards("5s");
        gameTable.hit(first);
        verify(webSocketServiceMock).sendCard(eq("andrey"), eq(first), any(Card.class), eq(25));
        verify(webSocketServiceMock).sendCard(eq("alex"), eq(first), any(Card.class), eq(25));
        verify(webSocketServiceMock).sendEnd(first);
        verify(webSocketServiceMock).sendPhase(second, "PLAY");

        deck.addNextCards("6d");
        gameTable.hit(second);
        verify(webSocketServiceMock).sendCard(eq("andrey"), eq(second), any(Card.class), eq(26));
        verify(webSocketServiceMock).sendCard(eq("alex"), eq(second), any(Card.class), eq(26));
        verify(webSocketServiceMock).sendEnd(second);
    }

    @Test
    public void testHitNotPlayTime() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't hit, not playing time");
        gameTable.hit("andrey");
    }

    @Test
    public void testHitNoPlayer() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.makeBet("andrey", 100);

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't hit, no such player at this table");
        gameTable.hit("bob");
    }

    @Test
    public void testHitNotCurrentPlayer() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        gameTable.makeBet("andrey", 100);
        gameTable.makeBet("alex", 200);

        ArgumentCaptor<String> firstPlayer = ArgumentCaptor.forClass(String.class);
        verify(webSocketServiceMock).sendPhase(firstPlayer.capture(), eq("PLAY"));
        String first = firstPlayer.getValue(), second = first.compareTo("andrey") == 0 ? "alex" : "andrey";

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't hit, wrong player");
        gameTable.hit(second);
    }

    @Test
    public void testStand() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        gameTable.makeBet("andrey", 100);
        gameTable.makeBet("alex", 200);

        ArgumentCaptor<String> firstPlayer = ArgumentCaptor.forClass(String.class);
        verify(webSocketServiceMock).sendPhase(firstPlayer.capture(), eq("PLAY"));
        String first = firstPlayer.getValue(), second = first.compareTo("andrey") == 0 ? "alex" : "andrey";

        verify(webSocketServiceMock).sendTurn(second, first);
        gameTable.stand(first);
        verify(webSocketServiceMock).sendEnd(first);
        verify(webSocketServiceMock).sendTurn(first, second);
        gameTable.stand(second);
        verify(webSocketServiceMock).sendEnd(second);
    }

    @Test
    public void testStandNotPlayTime() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't stand, not playing time");
        gameTable.stand("andrey");
    }

    @Test
    public void testStandNoPlayer() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.makeBet("andrey", 100);

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't stand, no such player at this table");
        gameTable.stand("bob");
    }

    @Test
    public void testStandNotCurrentPlayer() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        gameTable.makeBet("andrey", 100);
        gameTable.makeBet("alex", 200);

        ArgumentCaptor<String> firstPlayer = ArgumentCaptor.forClass(String.class);
        verify(webSocketServiceMock).sendPhase(firstPlayer.capture(), eq("PLAY"));
        String first = firstPlayer.getValue(), second = first.compareTo("andrey") == 0 ? "alex" : "andrey";

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't stand, wrong player");
        gameTable.stand(second);
    }

    @Test
    public void testFinishGame() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.makeBet("andrey", 100);
        gameTable.stand("andrey");
        verify(webSocketServiceMock, atLeast(2)).sendCard(eq("andrey"), eq("#dealer"), any(Card.class), anyInt());

        ArgumentCaptor<Map> winsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(webSocketServiceMock).sendWins(eq("andrey"), winsCaptor.capture());
        assertTrue(winsCaptor.getValue().containsKey("andrey"));
        assertTrue((int) winsCaptor.getValue().get("andrey") == 100 || (int) winsCaptor.getValue().get("andrey") == -100);
    }

    @Test
    public void testFinishGameLoose1() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        deck.addNextCards("Kd", "Th", "Qs");
        gameTable.makeBet("andrey", 100);
        deck.addNextCards("5s", "6c", "Jc");
//        andrey=25, dealer=26
        gameTable.hit("andrey");

        ArgumentCaptor<Map> winsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(webSocketServiceMock).sendWins(eq("andrey"), winsCaptor.capture());
        assertTrue(winsCaptor.getValue().containsKey("andrey"));
        assertTrue((int) winsCaptor.getValue().get("andrey") == -100);
        verify(dbServiceMock).subChipsByName("andrey", 100);
    }

    @Test
    public void testFinishGameLoose2() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        deck.addNextCards("Kd", "Th", "Qs");
        gameTable.makeBet("andrey", 100);
        deck.addNextCards("5s", "Tc");
//        andrey=25, dealer=20
        gameTable.hit("andrey");

        ArgumentCaptor<Map> winsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(webSocketServiceMock).sendWins(eq("andrey"), winsCaptor.capture());
        assertTrue(winsCaptor.getValue().containsKey("andrey"));
        assertTrue((int) winsCaptor.getValue().get("andrey") == -100);
        verify(dbServiceMock).subChipsByName("andrey", 100);
    }

    @Test
    public void testFinishGameLoose3() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        deck.addNextCards("2d", "3h", "Qs");
        gameTable.makeBet("andrey", 100);
        deck.addNextCards("5s", "Tc");
//        andrey=10, dealer=20
        gameTable.hit("andrey");
        gameTable.stand("andrey");

        ArgumentCaptor<Map> winsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(webSocketServiceMock).sendWins(eq("andrey"), winsCaptor.capture());
        assertTrue(winsCaptor.getValue().containsKey("andrey"));
        assertTrue((int) winsCaptor.getValue().get("andrey") == -100);
        verify(dbServiceMock).subChipsByName("andrey", 100);
    }

    @Test
    public void testFinishGameWin1() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        deck.addNextCards("2d", "3h", "Qs");
        gameTable.makeBet("andrey", 100);
        deck.addNextCards("5s", "6c", "Jc");
//        andrey=10, dealer=26
        gameTable.hit("andrey");
        gameTable.stand("andrey");

        ArgumentCaptor<Map> winsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(webSocketServiceMock).sendWins(eq("andrey"), winsCaptor.capture());
        assertTrue(winsCaptor.getValue().containsKey("andrey"));
        assertTrue((int) winsCaptor.getValue().get("andrey") == 100);
        verify(dbServiceMock).addChipsByName("andrey", 100);
    }

    @Test
    public void testFinishGameWin2() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        deck.addNextCards("Td", "Jh", "Qs");
        gameTable.makeBet("andrey", 100);
        deck.addNextCards("As", "Tc");
//        andrey=21, dealer=20
        gameTable.hit("andrey");
        gameTable.stand("andrey");

        ArgumentCaptor<Map> winsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(webSocketServiceMock).sendWins(eq("andrey"), winsCaptor.capture());
        assertTrue(winsCaptor.getValue().containsKey("andrey"));
        assertTrue((int) winsCaptor.getValue().get("andrey") == 100);
        verify(dbServiceMock).addChipsByName("andrey", 100);
    }

    @Test
    public void testFinishGameNone() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        deck.addNextCards("Td", "5h", "Qs");
        gameTable.makeBet("andrey", 100);
        deck.addNextCards("5s", "Tc");
//        andrey=20, dealer=20
        gameTable.hit("andrey");
        gameTable.stand("andrey");

        ArgumentCaptor<Map> winsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(webSocketServiceMock).sendWins(eq("andrey"), winsCaptor.capture());
        assertTrue(winsCaptor.getValue().containsKey("andrey"));
        assertTrue((int) winsCaptor.getValue().get("andrey") == 0);
//        verify(dbServiceMock);
    }

    @Test
    public void testRemoveUserAtBet() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        gameTable.removeUser("andrey");
        verify(webSocketServiceMock).sendRemovePlayer("alex", "andrey");

        gameTable.makeBet("alex", 100);
        verify(webSocketServiceMock).sendPhase("alex", "PLAY");
    }

    @Test
    public void testRemoveUserAtBetAfterBet1() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        gameTable.makeBet("andrey", 200);
        gameTable.removeUser("andrey");
        verify(webSocketServiceMock).sendRemovePlayer("alex", "andrey");

        gameTable.makeBet("alex", 100);
        verify(webSocketServiceMock).sendPhase("alex", "PLAY");
    }

    @Test
    public void testRemoveUserAtBetAfterBet2() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        gameTable.makeBet("andrey", 200);
        gameTable.removeUser("alex");
        verify(webSocketServiceMock).sendRemovePlayer("andrey", "alex");
        verify(webSocketServiceMock).sendPhase("andrey", "PLAY");
    }

    @Test
    public void testRemoveUserAtPlayFirst() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        gameTable.makeBet("andrey", 200);
        gameTable.makeBet("alex", 100);

        ArgumentCaptor<String> firstPlayer = ArgumentCaptor.forClass(String.class);
        verify(webSocketServiceMock).sendPhase(firstPlayer.capture(), eq("PLAY"));
        String first = firstPlayer.getValue(), second = first.compareTo("andrey") == 0 ? "alex" : "andrey";

        gameTable.removeUser(first);
        verify(dbServiceMock).subChipsByName(eq(first), anyInt());
        verify(webSocketServiceMock).sendRemovePlayer(second, first);
        verify(webSocketServiceMock).sendPhase(second, "PLAY");

        gameTable.stand(second);
        verify(webSocketServiceMock).sendWins(eq(second), anyMap());
    }

    @Test
    public void testRemoveUserAtPlaySecond() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        gameTable.makeBet("andrey", 200);
        gameTable.makeBet("alex", 100);

        ArgumentCaptor<String> firstPlayer = ArgumentCaptor.forClass(String.class);
        verify(webSocketServiceMock).sendPhase(firstPlayer.capture(), eq("PLAY"));
        String first = firstPlayer.getValue(), second = first.compareTo("andrey") == 0 ? "alex" : "andrey";

        gameTable.removeUser(second);
        verify(dbServiceMock).subChipsByName(eq(second), anyInt());
        verify(webSocketServiceMock).sendRemovePlayer(first, second);

        gameTable.stand(first);
        verify(webSocketServiceMock).sendWins(eq(first), anyMap());
    }

    @Test
    public void testRemoveUserNotPlaying() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");
        gameTable.addUser("alex");

        gameTable.makeBet("andrey", 200);
        gameTable.makeBet("alex", 100);

        gameTable.addUser("bob");
        gameTable.removeUser("bob");
        verify(webSocketServiceMock).sendRemovePlayer("andrey", "bob");
        verify(webSocketServiceMock).sendRemovePlayer("alex", "bob");
    }

    @Test
    public void testRemoveUserNoPlayer() throws Exception {
        DeckStub deck = new DeckStub();
        GameTable gameTable = new GameTableImpl(context, deck);
        gameTable.addUser("andrey");

        thrown.expect(GameTableException.class);
        thrown.expectMessage("Can't remove user, no such player at this table");
        gameTable.removeUser("bob");
    }

    @Test
    public void testDeckShuffle() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckImpl());
        gameTable.addUser("andrey");
        gameTable.addUser("alex");
        int cardsInDeck = 52;

        do {
            gameTable.makeBet("andrey", 1);
            gameTable.makeBet("alex", 1);
            cardsInDeck -= 5;

            ArgumentCaptor<String> firstPlayer = ArgumentCaptor.forClass(String.class);
            verify(webSocketServiceMock, atLeastOnce()).sendPhase(firstPlayer.capture(), eq("PLAY"));
            String first = firstPlayer.getValue(), second = first.compareTo("andrey") == 0 ? "alex" : "andrey";

            gameTable.stand(first);
            gameTable.stand(second);
            cardsInDeck--; // как минимум еще одна карта уйдет дилеру

        } while(cardsInDeck >= 0);

        verify(webSocketServiceMock).sendDeckShuffle("andrey");
        verify(webSocketServiceMock).sendDeckShuffle("alex");

    }
}