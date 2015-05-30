package frontend;

import base.AccountService;
import base.GameMechanics;
import base.WebSocketService;
import frontend.messages.MessageSocketAddUser;
import frontend.messages.MessageSocketRemoveUser;
import game.Card;
import game.Player;
import game.messages.*;
import main.Context;
import messageSystem.AddressService;
import messageSystem.Message;
import messageSystem.MessageSystem;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.*;

public class GameWebSocketTest {

    private Context context;
    private static final String USER_SESSION_ID = "abcdef12345";
//    private WebSocketService webSocketServiceMock;
    private AccountService accountServiceMock;
    private MessageSystem messageSystemMock;
    private AddressService addressServiceMock;
//    private GameMechanics gameMechanicsMock;

    private GameWebSocket webSock;
    private Session sessionMock;
    private RemoteEndpoint endpointMock;

//    @Rule
//    public final ExpectedException thrown = ExpectedException.none();

    private void initSocket() {
        webSock = new GameWebSocket(USER_SESSION_ID, context);

        sessionMock = mock(Session.class);
        endpointMock = mock(RemoteEndpoint.class);
        when(sessionMock.getRemote()).thenReturn(endpointMock);
        webSock.onOpen(sessionMock);
    }

    @Before
    public void setUp() throws Exception {
        context = new Context();
//        webSocketServiceMock = mock(WebSocketService.class);
        accountServiceMock = mock(AccountService.class);
        messageSystemMock = mock(MessageSystem.class);
        addressServiceMock = mock(AddressService.class);

        when(addressServiceMock.getGameMechanicsAddressFor(anyString())).thenReturn(null);
        when(addressServiceMock.getWebSocketService()).thenReturn(null);
        when(messageSystemMock.getAddressService()).thenReturn(addressServiceMock);

//        gameMechanicsMock = mock(GameMechanics.class);
        when(accountServiceMock.getUserBySession(USER_SESSION_ID)).thenReturn("bob");
        when(accountServiceMock.isUserLoggedIn(USER_SESSION_ID)).thenReturn(true);

//        context.add(WebSocketService.class, webSocketServiceMock);
        context.add(AccountService.class, accountServiceMock);
        context.add(MessageSystem.class, messageSystemMock);
//        context.add(GameMechanics.class, gameMechanicsMock);
    }

    @Test
    public void testOnOpen() throws Exception {
        GameWebSocket webSock = new GameWebSocket(USER_SESSION_ID, context);
//        verify(accountServiceMock).getUserBySession(USER_SESSION_ID);
        verify(accountServiceMock).isUserLoggedIn(USER_SESSION_ID);

        Session sessionMock = mock(Session.class);
        webSock.onOpen(sessionMock);
        verify(messageSystemMock, times(2)).sendMessage(any(Message.class));
//        verify(messageSystemMock).sendMessage(any(MessageGameAddUser.class));
//        verify(webSocketServiceMock).addUser(webSock);
//        verify(gameMechanicsMock).addUser("bob");
    }

    @Test
    public void testOnOpenFail() throws Exception {
        GameWebSocket webSock = new GameWebSocket("123", context);
//        verify(accountServiceMock).getUserBySession("123");
        verify(accountServiceMock).isUserLoggedIn("123");

        Session sessionMock = mock(Session.class);
        RemoteEndpoint endpointMock = mock(RemoteEndpoint.class);
        when(sessionMock.getRemote()).thenReturn(endpointMock);

        webSock.onOpen(sessionMock);
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("not logged"));
        verify(sessionMock).close();
    }

    @Test
    public void testOnMessageBet() throws Exception {
        GameWebSocket webSock = new GameWebSocket(USER_SESSION_ID, context);

        webSock.onMessage("{\"type\":\"bet\",\"bet\":123}");
        verify(messageSystemMock).sendMessage(any(MessageMakeBet.class));
//        verify(gameMechanicsMock).makeBet("bob", 123);
    }

    @Test
    public void testOnMessageHit() throws Exception {
        GameWebSocket webSock = new GameWebSocket(USER_SESSION_ID, context);

        webSock.onMessage("{\"type\":\"hit\"}");
        verify(messageSystemMock).sendMessage(any(MessageHit.class));
//        verify(gameMechanicsMock).hit("bob");
    }

    @Test
    public void testOnMessageStand() throws Exception {
        GameWebSocket webSock = new GameWebSocket(USER_SESSION_ID, context);

        webSock.onMessage("{\"type\":\"stand\"}");
        verify(messageSystemMock).sendMessage(any(MessageStand.class));
//        verify(gameMechanicsMock).stand("bob");
    }

    @Test
    public void testOnMessageBetErr() throws Exception {
        GameWebSocket webSock = new GameWebSocket(USER_SESSION_ID, context);

        webSock.onMessage("{\"type\":\"bet\"}");
        verify(messageSystemMock, never()).sendMessage(any(Message.class));
//        verifyZeroInteractions(gameMechanicsMock);
    }

    @Test
    public void testOnMessageTypeErr() throws Exception {
        GameWebSocket webSock = new GameWebSocket(USER_SESSION_ID, context);

        webSock.onMessage("{\"hello\":\"world\"}");
        verify(messageSystemMock, never()).sendMessage(any(Message.class));
//        verifyZeroInteractions(gameMechanicsMock);
    }

    @Test
    public void testOnMessageParseErr() throws Exception {
        GameWebSocket webSock = new GameWebSocket(USER_SESSION_ID, context);

        webSock.onMessage("{\"hello:\"world\"]");
        verify(messageSystemMock, never()).sendMessage(any(Message.class));
//        verifyZeroInteractions(gameMechanicsMock);
    }

    @Test
    public void testOnClose() throws Exception {
        GameWebSocket webSock = new GameWebSocket(USER_SESSION_ID, context);

        webSock.onClose(0, "");
        verify(messageSystemMock, times(2)).sendMessage(any(Message.class));
//        verify(messageSystemMock).sendMessage(any(MessageGameRemoveUser.class));
//        verify(webSocketServiceMock).removeUser(webSock);
//        verify(gameMechanicsMock).removeUser("bob");
    }

    @Test
    public void testSendPhase() throws Exception {
        initSocket();

        webSock.sendPhase("BET");
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("\"phase\":\"BET\""));
        verify(endpointMock).sendString(contains("\"type\":\"phase\""));
    }

    @Test
    public void testSendState() throws Exception {
        initSocket();

        Map<String, Player> state = new HashMap<>();
        state.put("bob", new Player());
        state.put("chris", new Player());

        webSock.sendState(state);
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("\"type\":\"state\""));
        verify(endpointMock).sendString(contains("\"name\":\"bob\""));
        verify(endpointMock).sendString(contains("\"name\":\"chris\""));
        verify(endpointMock).sendString(contains("\"score\":0"));
        verify(endpointMock).sendString(contains("\"bet\":0"));
    }

    @Test
    public void testSendCard() throws Exception {
        initSocket();

        webSock.sendCard("alex", new Card('Q', 'd'), 10);
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("\"type\":\"card\""));
        verify(endpointMock).sendString(contains("\"owner\":\"alex\""));
        verify(endpointMock).sendString(contains("\"card\":\"Qd\""));
        verify(endpointMock).sendString(contains("\"score\":10"));
    }

    @Test
    public void testSendBet() throws Exception {
        initSocket();

        webSock.sendBet("alex", 123);
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("\"type\":\"bet\""));
        verify(endpointMock).sendString(contains("\"owner\":\"alex\""));
        verify(endpointMock).sendString(contains("\"bet\":123"));
    }

    @Test
    public void testSendWins() throws Exception {
        initSocket();

        Map<String, Integer> wins = new HashMap<>();
        wins.put("andrey", 123);
        wins.put("alex", 321);

        webSock.sendWins(wins);
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("\"type\":\"wins\""));
        verify(endpointMock).sendString(contains("\"andrey\":123"));
        verify(endpointMock).sendString(contains("\"alex\":321"));
    }

    @Test
    public void testSendDeckShuffle() throws Exception {
        initSocket();

        webSock.sendDeckShuffle();
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("\"type\":\"shuffle\""));
    }

    @Test
    public void testSendRemovePlayer() throws Exception {
        initSocket();

        webSock.sendRemovePlayer("andrey");
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("\"type\":\"exit\""));
        verify(endpointMock).sendString(contains("\"player\":\"andrey\""));
    }

    @Test
    public void testSendNewPlayer() throws Exception {
        initSocket();

        webSock.sendNewPlayer("andrey");
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("\"type\":\"new\""));
        verify(endpointMock).sendString(contains("\"player\":\"andrey\""));
    }

    @Test
    public void testSendTurn() throws Exception {
        initSocket();

        webSock.sendTurn("andrey");
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("\"type\":\"turn\""));
        verify(endpointMock).sendString(contains("\"player\":\"andrey\""));
    }

    @Test
    public void testSendEnd() throws Exception {
        initSocket();

        webSock.sendEnd();
        verify(sessionMock).getRemote();
        verify(endpointMock).sendString(contains("\"type\":\"END\""));
    }

    @Test
    public void testGetUserName() throws Exception {
        GameWebSocket webSock = new GameWebSocket(USER_SESSION_ID, context);
        assertEquals("bob", webSock.getUserName());
    }
}