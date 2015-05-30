package messageSystem;

import base.GameMechanics;
import base.WebSocketService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AddressService {

    private Address webSocketService;

    private List<Address> gameMechanicsList = new ArrayList<>();

    public void registerGameMechanics(GameMechanics gameMechanics) {
        gameMechanicsList.add(gameMechanics.getAddress());
    }

    public void registerWebSocketService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService.getAddress();
    }

    public Address getWebSocketService() {
        return webSocketService;
    }

    public synchronized Address getGameMechanicsAddressFor(String userName) {
        int index = Math.abs(userName.hashCode()) % gameMechanicsList.size();
        return gameMechanicsList.get(index);
    }
}
