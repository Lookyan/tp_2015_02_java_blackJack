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
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SignInServlet extends HttpServlet {

    public static final String url = "/api/auth/signin";
    private AccountService accountService;
    private DBService dbService;

    private static final Logger logger = LogManager.getLogger();
    private static final JSONParser parser = new JSONParser();

    public SignInServlet(Context context) {
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

        JSONObject resp = new JSONObject();
        JSONObject body = new JSONObject();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "application/json");

        try {
            JSONObject message = (JSONObject) parser.parse(incomingJSON);
            logger.info("Incoming message: {}", incomingJSON);

            if (!message.containsKey("name")) {
                throw new Exception("Can't login, no 'name' field");
            }
            if (!message.containsKey("password")) {
                throw new Exception("Can't login, no 'password' field");
            }

            name = (String) message.get("name");
            password = (String) message.get("password");

            UserDataSet profile = dbService.getUserData(name);

            if (profile != null && profile.getPassword().equals(password)) {
                resp.put("status", "200");
                body.put("name", profile.getName());
                body.put("email", profile.getEmail());
                body.put("chips", profile.getChips());
                accountService.addSession(request.getSession().getId(), name);
            } else {
                resp.put("status", "404");
                body.put("error", "wrong");
            }

        } catch (ParseException e) {
            logger.error("Can't parse incoming JSON");
            resp.put("status", "400");
            body.put("error", "json");
        } catch (Exception e) {
            logger.error(e);
            resp.put("status", "500");
            body.put("error", "unknown");
        }

        resp.put("body", body);

        response.getWriter().println(resp.toJSONString());
    }

//    public void doPost(HttpServletRequest request,
//                       HttpServletResponse response) throws ServletException, IOException {
//        String email = request.getParameter("email");
//        String password = request.getParameter("password");
//
//        response.setStatus(HttpServletResponse.SC_OK);
//
//        Map<String, Object> pageVariables = new HashMap<>();
//        pageVariables.put("email", email == null ? "" : email);
//        pageVariables.put("password", password == null ? "" : password);
//
//        response.getWriter().println(PageGenerator.getPage("authresponse.txt", pageVariables));
//    }
}
