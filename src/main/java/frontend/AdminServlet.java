package frontend;

import interfaces.AccountService;
import main.AccountServiceImpl;
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
    private AccountService accountService;

    public AdminServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("usersCount", accountService.getUsersCount());
        pageVariables.put("signedInCount", accountService.getSignedInUsersCount());
        String stopString = request.getParameter("stop");
        if(stopString != null) {
            int timeMS = Integer.valueOf(stopString);
            System.out.print("Server will be down after: "+ timeMS + " ms");
            TimeHelper.sleep(timeMS);
            System.out.print("\nShutdown");
            System.exit(0);
        } else {
            response.getWriter().println(PageGenerator.getPage("admin.html", pageVariables));
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
