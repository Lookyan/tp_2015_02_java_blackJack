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
import static org.mockito.Mockito.*;

public class SignUpServletTest {

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
        SignUpServlet signUpServlet= new SignUpServlet(context);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);

        when(requestMock.getParameter("name")).thenReturn("andrey");
        when(requestMock.getParameter("password")).thenReturn("123");
        when(requestMock.getParameter("email")).thenReturn("a@a");

        when(dbService.getUserData("andrey")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(responseMock.getWriter()).thenReturn(printWriter);

//        signUpServlet.doGet(requestMock, responseMock);
        verify(responseMock).setStatus(200);
        assertThat(stringWriter.toString(), CoreMatchers.containsString("Sign up status: New user created"));
    }

    @Test
    public void testEmptyField() throws Exception {
        SignUpServlet signUpServlet= new SignUpServlet(context);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);

        when(requestMock.getParameter("name")).thenReturn("andrey");
        when(requestMock.getParameter("password")).thenReturn("123");
        when(requestMock.getParameter("email")).thenReturn(null);

        when(dbService.getUserData("andrey")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(responseMock.getWriter()).thenReturn(printWriter);

//        signUpServlet.doGet(requestMock, responseMock);
        verify(responseMock).setStatus(200);
        assertThat(stringWriter.toString(), CoreMatchers.containsString("Sign up status: Fields can't be empty"));
    }

    @Test
    public void testExistingUser() throws Exception {
        SignUpServlet signUpServlet= new SignUpServlet(context);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);

        when(requestMock.getParameter("name")).thenReturn("andrey");
        when(requestMock.getParameter("password")).thenReturn("123");
        when(requestMock.getParameter("email")).thenReturn("a@a");

        when(dbService.getUserData("andrey")).thenReturn(new UserDataSet("andrey", "123", "a@a", 1000));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(responseMock.getWriter()).thenReturn(printWriter);

//        signUpServlet.doGet(requestMock, responseMock);
        verify(responseMock).setStatus(200);
        assertThat(stringWriter.toString(), CoreMatchers.containsString("Sign up status: User with name: andrey already exists"));
    }
}