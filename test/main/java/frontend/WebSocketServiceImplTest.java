package frontend;

import base.WebSocketService;
import game.Card;
import game.Player;
import main.Context;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WebSocketServiceImplTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = mock(Context.class);
    }

    @Test
    public void testAddUser() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);

        webSocketService.addUser(webSock1);
        verify(webSock1, times(2)).getUserName();

//        проверяем, работает ли
        webSocketService.sendEnd("andrey");
        verify(webSock1).sendEnd();
    }

    @Test
    public void testAddTwoUsers() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");

        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);

        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);
        verify(webSock1, times(2)).getUserName();
        verify(webSock2, times(2)).getUserName();

//        проверяем, работает ли
        webSocketService.sendEnd("andrey");
        verify(webSock1).sendEnd();
        webSocketService.sendEnd("alex");
        verify(webSock2).sendEnd();
    }

    @Test
    public void testRemoveUser() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        webSocketService.removeUser(webSock1);
        webSocketService.removeUser(webSock2);
        verify(webSock1, times(4)).getUserName();
        verify(webSock2, times(4)).getUserName();
    }

    @Test
    public void testSendPhase() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        webSocketService.sendPhase("alex", "BET");
        verify(webSock2).sendPhase("BET");

        webSocketService.sendPhase("andrey", "BET");
        verify(webSock1).sendPhase("BET");
    }

    @Test
    public void testSendCard() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        Card card = new Card('3', 's');
        webSocketService.sendCard("alex", "bob", card, 3);
        verify(webSock2).sendCard("bob", card, 3);

        card = new Card('K', 'h');
        webSocketService.sendCard("andrey", "alex", card, 10);
        verify(webSock1).sendCard("alex", card, 10);
    }

    @Test
    public void testSendWins() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        Map<String, Integer> wins = new HashMap<>();
        wins.put("andrey", 123);
        wins.put("alex", 321);
        webSocketService.sendWins("alex", wins);
        verify(webSock2).sendWins(wins);

        webSocketService.sendWins("andrey", wins);
        verify(webSock1).sendWins(wins);
    }

    @Test
    public void testSendDeckShuffle() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        webSocketService.sendDeckShuffle("alex");
        verify(webSock2).sendDeckShuffle();

        webSocketService.sendDeckShuffle("andrey");
        verify(webSock1).sendDeckShuffle();
    }

    @Test
    public void testSendRemovePlayer() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        webSocketService.sendRemovePlayer("alex", "bob");
        verify(webSock2).sendRemovePlayer("bob");

        webSocketService.sendRemovePlayer("andrey", "chris");
        verify(webSock1).sendRemovePlayer("chris");
    }

    @Test
    public void testSendBet() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        webSocketService.sendBet("alex", "bob", 123);
        verify(webSock2).sendBet("bob", 123);

        webSocketService.sendBet("andrey", "chris", 321);
        verify(webSock1).sendBet("chris", 321);
    }

    @Test
    public void testSendState() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        Map<String, Player> state = new HashMap<>();
        state.put("bob", new Player());
        state.put("chris", new Player());
        webSocketService.sendState("alex", state);
        verify(webSock2).sendState(state);

        webSocketService.sendState("andrey", state);
        verify(webSock1).sendState(state);
    }

    @Test
    public void testSendNewPlayer() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        webSocketService.sendNewPlayer("alex", "bob");
        verify(webSock2).sendNewPlayer("bob");

        webSocketService.sendNewPlayer("andrey", "chris");
        verify(webSock1).sendNewPlayer("chris");
    }

    @Test
    public void testSendTurn() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        webSocketService.sendTurn("alex", "bob");
        verify(webSock2).sendTurn("bob");

        webSocketService.sendTurn("andrey", "chris");
        verify(webSock1).sendTurn("chris");
    }

    @Test
    public void testSendEnd() throws Exception {
        GameWebSocket webSock1 = mock(GameWebSocket.class);
        when(webSock1.getUserName()).thenReturn("andrey");
        GameWebSocket webSock2 = mock(GameWebSocket.class);
        when(webSock2.getUserName()).thenReturn("alex");

        WebSocketService webSocketService = new WebSocketServiceImpl(context);
        webSocketService.addUser(webSock1);
        webSocketService.addUser(webSock2);

        webSocketService.sendEnd("alex");
        verify(webSock2).sendEnd();

        webSocketService.sendEnd("andrey");
        verify(webSock1).sendEnd();
    }
}