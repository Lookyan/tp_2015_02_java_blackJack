package messageSystem;

import base.GameMechanics;
import base.WebSocketService;
import frontend.GameWebSocket;
import frontend.PhoneWebSocket;
import frontend.WebSocketServiceImpl;
import game.Card;
import game.GameMechanicsImpl;
import game.GameTableImpl;
import game.Player;
import main.Context;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class AddressServiceTest {

    private class GameMechanicsStub implements GameMechanics {

        private Address address = new Address();

        @Override
        public void addUser(String userName) {

        }

        @Override
        public void removeUser(String userName) {

        }

        @Override
        public void makeBet(String userName, int bet) {

        }

        @Override
        public void hit(String userName) {

        }

        @Override
        public void stand(String userName) {

        }

        @Override
        public Address getAddress() {
            return address;
        }

        @Override
        public void run() {

        }
    }

    private class WebSocketServiceStub implements WebSocketService {

        private Address address = new Address();

        @Override
        public void addUser(GameWebSocket userSocket) {

        }

        @Override
        public void removeUser(GameWebSocket userSocket) {

        }

        @Override
        public void addPhone(PhoneWebSocket phoneSocket) {

        }

        @Override
        public void removePhone(PhoneWebSocket phoneSocket) {

        }

        @Override
        public void sendPhase(String userName, String gamePhase) {

        }

        @Override
        public void sendCard(String userName, String owner, Card card, int score) {

        }

        @Override
        public void sendWins(String userName, Map<String, Integer> wins) {

        }

        @Override
        public void sendDeckShuffle(String userName) {

        }

        @Override
        public void sendRemovePlayer(String userName, String removedUserName) {

        }

        @Override
        public void sendBet(String userName, String owner, int bet) {

        }

        @Override
        public void sendState(String userName, Map<String, Player> players) {

        }

        @Override
        public void sendNewPlayer(String userName, String newPlayerName) {

        }

        @Override
        public void sendTurn(String userName, String player) {

        }

        @Override
        public void sendEnd(String userName) {

        }

        @Override
        public Address getAddress() {
            return address;
        }

        @Override
        public void run() {

        }
    }

    private AddressService addressService;

    @Before
    public void setUp() throws Exception {
        addressService = new AddressService();
    }

    @Test
    public void testGameMechanics() throws Exception {
        GameMechanics gameMechanics1 = new GameMechanicsStub();
        GameMechanics gameMechanics2 = new GameMechanicsStub();

        addressService.registerGameMechanics(gameMechanics1);
        addressService.registerGameMechanics(gameMechanics2);

        assertEquals(gameMechanics1.getAddress(), addressService.getGameMechanicsAddressFor("alex"));
        assertEquals(gameMechanics2.getAddress(), addressService.getGameMechanicsAddressFor("andrey"));
    }

    @Test
    public void testWebSocketService() throws Exception {
        WebSocketService webSocketService = new WebSocketServiceStub();

        addressService.registerWebSocketService(webSocketService);

        assertEquals(webSocketService.getAddress(), addressService.getWebSocketService());
    }
}