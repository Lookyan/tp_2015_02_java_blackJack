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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class IdentifyServlet extends HttpServlet {

    public static final String url = "/api/auth/identify";
    private AccountService accountService;
    private DBService dbService;

    private static final Logger logger = LogManager.getLogger();

    public IdentifyServlet(Context context) {
        this.accountService = (AccountService) context.get(AccountService.class);
        this.dbService = (DBService) context.get(DBService.class);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        JSONObject resp = new JSONObject();
        JSONObject body = new JSONObject();
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            String name = accountService.getUserBySession(request.getSession().getId());
            if (name != null) {
                UserDataSet profile = dbService.getUserData(name);
                logger.info("Incoming request to identify");

                if (profile != null) {
                    resp.put("status", 200);
                    body.put("name", profile.getName());
                    body.put("email", profile.getEmail());
                    body.put("chips", profile.getChips());
                } else {
                    throw new Exception("No user in db, though is in account service");
                }
            } else {
                resp.put("status", 404);
                body.put("error", "notLogged");
            }

        } catch (Exception e) {
            logger.error(e);
            resp.put("status", 500);
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
