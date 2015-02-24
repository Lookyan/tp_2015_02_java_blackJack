package frontend;

import main.AccountService;
import main.UserProfile;
import org.eclipse.jetty.server.Server;
import templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 21.02.15.
 */
public class AdminServlet extends HttpServlet {
    private AccountService accountService;
    private Server server;

    public AdminServlet(AccountService accountService, Server server) {
        this.accountService = accountService;
        this.server = server;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("usersCount", accountService.getUsersCount());
        pageVariables.put("signedInCount", accountService.getSignedInUsersCount());
        String isStop = request.getParameter("stop");
        if(isStop != null) {
            try {
                server.stop();
            } catch (java.lang.Exception e) {
                pageVariables.put("message", "Smth went wrong!");
                response.getWriter().println(PageGenerator.getPage("admin.html", pageVariables));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } else {
            response.getWriter().println(PageGenerator.getPage("admin.html", pageVariables));
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
