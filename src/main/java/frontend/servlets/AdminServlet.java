package frontend.servlets;

import base.AccountService;
import base.DBService;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import time.TimeHelper;

public class AdminServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger();

    private AccountService accountService;
    private DBService dbService;

    public AdminServlet(Context context) {
        this.accountService = (AccountService) context.get(AccountService.class);
        this.dbService = (DBService) context.get(DBService.class);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pass = request.getParameter("pass");
        if (pass == null || !pass.equals("qwe123asd123zxc")) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.addHeader("Location", "/");
        } else {

            Map<String, Object> pageVariables = new HashMap<>();
            pageVariables.put("usersCount", dbService.countAllUsers());
            pageVariables.put("signedInCount", accountService.getSignedInUsersCount());

            String stopParam = request.getParameter("stop");
            if (stopParam != null) {
                int timeMS = Integer.valueOf(stopParam);
                logger.info("Server will be down after: " + timeMS + " ms");
                TimeHelper.sleep(timeMS);
                logger.info("Shutdown");
                dbService.shutdown();
                System.exit(0);
            } else {
                response.getWriter().println(PageGenerator.getPage("admin.html", pageVariables));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }
}
