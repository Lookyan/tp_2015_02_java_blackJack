package frontend.servlets;

import base.AccountService;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenServlet extends HttpServlet {

    public static final String url = "/api/auth/token";
    private AccountService accountService;

    private static final Logger logger = LogManager.getLogger();

    public TokenServlet(Context context) {
        this.accountService = (AccountService) context.get(AccountService.class);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        JSONObject resp = new JSONObject();
        JSONObject body = new JSONObject();
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            String name = accountService.getUserBySession(request.getSession().getId());
            if (name != null) {
                logger.info("Incoming request to get token");

                String token = accountService.generateTokenFor(name);

                resp.put("status", 200);
                body.put("token", token);
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
