package frontend.servlets;


import base.AccountService;
import main.Context;
import base.UserProfile;
import templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileServlet extends HttpServlet {
    private AccountService accountService;

    public ProfileServlet(Context context) {
        this.accountService = (AccountService) context.get(AccountService.class);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserProfile profile = accountService.getSessions(session.toString());
        Map<String, Object> pageVariables = new HashMap<>();
        if(profile != null) {
            pageVariables.put("profileMessage", "It's your profile, " + profile.getName());
            pageVariables.put("email", profile.getEmail());
            response.getWriter().println(PageGenerator.getPage("profile.html", pageVariables));
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            String signInUrl = response.encodeURL(SignInServlet.url);
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.addHeader("Location", signInUrl);
        }
    }
}
