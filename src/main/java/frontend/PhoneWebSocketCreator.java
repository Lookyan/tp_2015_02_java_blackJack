package frontend;

import main.Context;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import java.util.List;

public class PhoneWebSocketCreator implements WebSocketCreator {
    private Context context;

    public PhoneWebSocketCreator(Context context) {
        this.context = context;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        List<String> list = req.getParameterMap().get("token");
        String token;
        if (list != null) {
            token = list.get(0);
        } else {
            token = "$";
        }
        return new PhoneWebSocket(token, context);
    }
}