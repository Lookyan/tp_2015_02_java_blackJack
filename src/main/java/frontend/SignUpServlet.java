package frontend;

import base.AccountService;
import main.Context;
import main.UserProfile;
import templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUpServlet extends HttpServlet {
    private AccountService accountService;

    public SignUpServlet() {
        this.accountService = (AccountService) Context.getInstance().get(AccountService.class);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if(name == null || password == null || email == null) {
            //
        }

        Map<String, Object> pageVariables = new HashMap<>();
        if (accountService.addUser(name, new UserProfile(name, password, email))) {
            pageVariables.put("signUpStatus", "New user created");
        } else {
            pageVariables.put("signUpStatus", "User with name: " + name + " already exists");
        }

        response.getWriter().println(PageGenerator.getPage("signupstatus.html", pageVariables));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        Map<String, Object> pageVariables = new HashMap<>();
        if(name == null || password == null || email == null) {

        } else {
            if (accountService.addUser(name, new UserProfile(name, password, email))) {
                pageVariables.put("signUpStatus", "New user created");
            } else {
                pageVariables.put("signUpStatus", "User with name: " + name + " already exists");
            }
        }
        response.getWriter().println(PageGenerator.getPage("signupstatus.html", pageVariables));
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
