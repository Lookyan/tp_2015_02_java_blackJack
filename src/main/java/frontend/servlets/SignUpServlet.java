package frontend.servlets;

import base.AccountService;
import base.DBService;
import base.dataSets.UserDataSet;
import main.Context;
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
    private DBService dbService;

    public SignUpServlet(Context context) {
        this.accountService = (AccountService) context.get(AccountService.class);
        this.dbService = (DBService) context.get(DBService.class);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        Map<String, Object> pageVariables = new HashMap<>();
        if(name == null || password == null || email == null) {
            pageVariables.put("signUpStatus", "Fields can't be empty");
        } else {
            if (dbService.getUserData(name) == null) {
//                accountService.addUser(name, new UserProfile(name, password, email))
                dbService.saveUser(new UserDataSet(name, password, email, 1000));
                pageVariables.put("signUpStatus", "New user created");
            } else {
                pageVariables.put("signUpStatus", "User with name: " + name + " already exists");
            }
        }

        response.getWriter().println(PageGenerator.getPage("signupstatus.html", pageVariables));
        response.setStatus(HttpServletResponse.SC_OK);
    }

//    public void doPost(HttpServletRequest request,
//                       HttpServletResponse response) throws ServletException, IOException {
//        String name = request.getParameter("name");
//        String password = request.getParameter("password");
//        String email = request.getParameter("email");
//
//        Map<String, Object> pageVariables = new HashMap<>();
//        if(name == null || password == null || email == null) {
//
//        } else {
//            if (accountService.addUser(name, new UserProfile(name, password, email))) {
//                pageVariables.put("signUpStatus", "New user created");
//            } else {
//                pageVariables.put("signUpStatus", "User with name: " + name + " already exists");
//            }
//        }
//        response.getWriter().println(PageGenerator.getPage("signupstatus.html", pageVariables));
//        response.setStatus(HttpServletResponse.SC_OK);
//    }

}
