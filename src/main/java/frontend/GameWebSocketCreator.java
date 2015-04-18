package frontend;

import main.Context;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class GameWebSocketCreator implements WebSocketCreator {
    private Context context;

    public GameWebSocketCreator(Context context) {
        this.context = context;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        String sessionId = req.getHttpServletRequest().getSession().getId();
        return new GameWebSocket(sessionId, context);
    }
}