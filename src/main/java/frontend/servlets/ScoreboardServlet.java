package frontend.servlets;

import base.AccountService;
import base.DBService;
import base.dataSets.UserDataSet;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ScoreboardServlet extends HttpServlet {

    public static final String url = "/api/top";
    private DBService dbService;

    private static final Logger logger = LogManager.getLogger();

    public ScoreboardServlet(Context context) {
        this.dbService = (DBService) context.get(DBService.class);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        JSONObject resp = new JSONObject();
        JSONArray body = new JSONArray();
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            logger.info("Incoming request to get top");

            List list = dbService.getTop();

            resp.put("status", 200);
//            body.put("token", token);
            list.stream().forEach(user -> {
                UserDataSet userDS = (UserDataSet) user;
                JSONObject userJSON = new JSONObject();
                userJSON.put("player", userDS.getName());
                userJSON.put("score", userDS.getChips());
                body.add(userJSON);
            });
        } catch (Exception e) {
            logger.error(e);
            resp.put("status", 500);
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
