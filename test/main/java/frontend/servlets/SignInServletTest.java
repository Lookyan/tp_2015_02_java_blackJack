package frontend.servlets;

import base.AccountService;
import base.DBService;
import base.dataSets.UserDataSet;
import main.Context;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SignInServletTest {

    Context context;
    AccountService accountService;
    DBService dbService;

    @Before
    public void setUp() throws Exception {
        context = new Context();
        accountService = mock(AccountService.class);
        dbService = mock(DBService.class);

        context.add(AccountService.class, accountService);
        context.add(DBService.class, dbService);
    }

    @Test
    public void testDoGet() throws Exception {
        SignInServlet signInServlet= new SignInServlet(context);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        HttpSession sessionMock = mock(HttpSession.class);

        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getId()).thenReturn("123asd");
        when(requestMock.getParameter("name")).thenReturn("andrey");
        when(requestMock.getParameter("password")).thenReturn("123");

        when(dbService.getUserData("andrey")).thenReturn(new UserDataSet("andrey", "123", "a@a", 1000));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(responseMock.getWriter()).thenReturn(printWriter);

//        signInServlet.doGet(requestMock, responseMock);
        verify(accountService).addSession("123asd", "andrey");
        verify(responseMock).setStatus(200);
        assertThat(stringWriter.toString(), CoreMatchers.containsString("Login status: Login passed"));
    }

    @Test
    public void testWrongLogIn() throws Exception {
        SignInServlet signInServlet= new SignInServlet(context);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        HttpSession sessionMock = mock(HttpSession.class);

        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getId()).thenReturn("123asd");
        when(requestMock.getParameter("name")).thenReturn("andrey");
        when(requestMock.getParameter("password")).thenReturn("321");

        when(dbService.getUserData("andrey")).thenReturn(new UserDataSet("andrey", "123", "a@a", 1000));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(responseMock.getWriter()).thenReturn(printWriter);

//        signInServlet.doGet(requestMock, responseMock);
        verify(accountService, never()).addSession("123asd", "andrey");
        verify(responseMock).setStatus(200);
        assertThat(stringWriter.toString(), CoreMatchers.containsString("Login status: Wrong login/password"));
    }
}