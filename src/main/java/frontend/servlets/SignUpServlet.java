package frontend.servlets;

import base.AccountService;
import base.DBService;
import base.dataSets.UserDataSet;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUpServlet extends HttpServlet {

    public static final String url = "/api/auth/signup";

    private AccountService accountService;
    private DBService dbService;

    private static final Logger logger = LogManager.getLogger();
    private static final JSONParser parser = new JSONParser();

    public SignUpServlet(Context context) {
        this.accountService = (AccountService) context.get(AccountService.class);
        this.dbService = (DBService) context.get(DBService.class);
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        BufferedReader bufferedReader = request.getReader();
        String incomingJSON = "";
        if (bufferedReader != null) {
            while (bufferedReader.ready())
                incomingJSON += bufferedReader.readLine();
        }

        String name;
        String password;
        String email;

        JSONObject resp = new JSONObject();
        JSONObject body = new JSONObject();
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            JSONObject message = (JSONObject) parser.parse(incomingJSON);
            logger.info("Incoming message: {}", incomingJSON);

            if (!message.containsKey("name")) {
                throw new Exception("Can't signup, no 'name' field");
            }
            if (!message.containsKey("password")) {
                throw new Exception("Can't signup, no 'password' field");
            }
            if (!message.containsKey("email")) {
                throw new Exception("Can't signup, no 'email' field");
            }

            name = (String) message.get("name");
            name = name.replaceAll("[^A-Za-z0-9]", "");
            password = (String) message.get("password");
            email = (String) message.get("email");

            if (dbService.getUserData(name) == null) {
                if (dbService.getUserDataByEmail(email) == null) {
                    dbService.saveUser(new UserDataSet(name, password, email, 1000));
                    resp.put("status", 200);
                    body.put("chips", 1000);
                    accountService.addSession(request.getSession().getId(), name);
                } else {
                    resp.put("status", 400);
                    body.put("error", "exists");
                    body.put("what", "email");
                }
            } else {
                resp.put("status", 400);
                body.put("error", "exists");
                body.put("what", "name");

            }

        } catch (ParseException e) {
            logger.error("Can't parse incoming JSON");
            resp.put("status", 400);
            body.put("error", "json");
        } catch (Exception e) {
            logger.error(e);
            resp.put("status", 500);
            body.put("error", "unknown");
        }

        resp.put("body", body);

        response.getWriter().println(resp.toJSONString());
    }

//    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String name = request.getParameter("name");
//        String password = request.getParameter("password");
//        String email = request.getParameter("email");
//
//        Map<String, Object> pageVariables = new HashMap<>();
//        if(name == null || password == null || email == null) {
//            pageVariables.put("signUpStatus", "Fields can't be empty");
//        } else {
//            if (dbService.getUserData(name) == null) {
////                accountService.addUser(name, new UserProfile(name, password, email))
//                dbService.saveUser(new UserDataSet(name, password, email, 1000));
//                pageVariables.put("signUpStatus", "New user created");
//            } else {
//                pageVariables.put("signUpStatus", "User with name: " + name + " already exists");
//            }
//        }
//
//        response.getWriter().println(PageGenerator.getPage("signupstatus.html", pageVariables));
//        response.setStatus(HttpServletResponse.SC_OK);
//    }

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
