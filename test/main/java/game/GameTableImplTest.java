package game;

import base.AccountService;
import base.Deck;
import base.GameTable;
import base.WebSocketService;
import main.Context;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import resourceSystem.ResourceFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class GameTableImplTest {

    private Context context;
    private WebSocketService webSocketServiceMock;
    private AccountService accountServiceMock;

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
        ResourceFactory.getInstance().init();
    }

    @Before
    public void setUp() throws Exception {
        context = new Context();
        accountServiceMock = mock(AccountService.class);
        when(accountServiceMock.getChips(anyString())).thenReturn(1000);

        webSocketServiceMock = mock(WebSocketService.class);

        context.add(WebSocketService.class, webSocketServiceMock);
        context.add(AccountService.class, accountServiceMock);
    }

//    @After
//    public void tearDown() throws Exception {
//
//    }
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
    public void testMakeBetOnePlayer() throws Exception {
        GameTable gameTable = new GameTableImpl(context, new DeckStub());
        gameTable.addUser("andrey");

        gameTable.makeBet("andrey", 100);
        verify(accountServiceMock).getChips("andrey");
        verify(webSocketServiceMock).sendBet("andrey", "andrey", 100);

//        startGame
        verify(webSocketServiceMock, times(2)).sendCard(eq("andrey"), eq("andrey"), any(Card.class), anyInt());
        verify(webSocketServiceMock).sendCard(eq("andrey"), eq("#dealer"), any(Card.class), anyInt());

//        processStep
        verify(webSocketServiceMock).sendPhase("andrey", "PLAY");
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
        verify(accountServiceMock).getChips("andrey");
        verify(webSocketServiceMock).sendBet("andrey", "andrey", 100);
        verify(webSocketServiceMock).sendBet("alex", "andrey", 100);

        gameTable.makeBet("alex", 200);
        verify(accountServiceMock).getChips("alex");
        verify(webSocketServiceMock).sendBet("andrey", "alex", 200);
        verify(webSocketServiceMock).sendBet("alex", "alex", 200);

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
    public void testHit() throws Exception {
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
        verify(webSocketServiceMock, times(2)).sendPhase("andrey", "PLAY");
    }

    @Test
    public void testStand() throws Exception {

    }

    @Test
    public void testRemoveUser() throws Exception {

    }

    @Test
    public void testIsFull() throws Exception {

    }
}