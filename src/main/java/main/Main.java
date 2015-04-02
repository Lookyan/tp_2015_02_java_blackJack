package main;

import frontend.*;
import interfaces.AccountService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            String portString = args[0];
            port = Integer.valueOf(portString);
        }
        Server server = new Server(port);

        System.out.append("Starting at port: ").append(String.valueOf(port)).append('\n');

        AccountService accountService = new AccountServiceImpl();

        Context.getInstance().add(AccountService.class, accountService);


        Servlet signIn = new SignInServlet();
        Servlet signUp = new SignUpServlet();
        Servlet profile = new ProfileServlet();
        Servlet logout = new LogoutServlet();
        Servlet admin = new AdminServlet();

        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.addServlet(new ServletHolder(signIn), SignInServlet.url);
        servletContext.addServlet(new ServletHolder(signUp), "/api/v1/auth/signup");
        servletContext.addServlet(new ServletHolder(profile), "/api/v1/auth/profile");
        servletContext.addServlet(new ServletHolder(logout), "/api/v1/auth/logout");
        servletContext.addServlet(new ServletHolder(admin), "/admin");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("public_html");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, servletContext});

        server.setHandler(handlers);

        server.start();
        server.join();
    }
}