package frontend;

import base.AccountService;
import main.Context;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    private AccountService accountService;

    public LogoutServlet() {
        this.accountService = (AccountService) Context.getInstance().get(AccountService.class);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        accountService.logout(request.getSession().toString());

        String signInUrl = response.encodeURL(SignInServlet.url);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.addHeader("Location", signInUrl);

    }
}
