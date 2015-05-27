package frontend.servlets;

import frontend.GameWebSocketCreator;
import frontend.PhoneWebSocketCreator;
import main.Context;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "WebSocketGameServlet", urlPatterns = {"/phone"})
public class WebSocketPhoneServlet extends WebSocketServlet {

    //    private final static int IDLE_TIME = 60 * 1000;
    private Context context;

    public WebSocketPhoneServlet(Context context) {
        this.context = context;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
//        factory.getPolicy().setIdleTimeout(IDLE_TIME);
        factory.setCreator(new PhoneWebSocketCreator(context));
    }
}
