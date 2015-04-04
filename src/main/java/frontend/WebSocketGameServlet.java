package frontend;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "WebSocketGameServlet", urlPatterns = {"/gameplay"})
public class WebSocketGameServlet extends WebSocketServlet {
    private final static int IDLE_TIME = 60 * 1000;

    @Override
    public void configure(WebSocketServletFactory zavod) {
        zavod.getPolicy().setIdleTimeout(IDLE_TIME);
        zavod.setCreator(new GameWebSocketCreator());
    }
}
