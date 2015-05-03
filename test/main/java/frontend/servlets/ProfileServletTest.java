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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileServletTest {

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
        ProfileServlet profileServlet = new ProfileServlet(context);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        HttpSession sessionMock = mock(HttpSession.class);

        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getId()).thenReturn("123asd");

        when(accountService.getUserBySession("123asd")).thenReturn("andrey");
        when(dbService.getUserData("andrey")).thenReturn(new UserDataSet("andrey", "123", "a@a", 1000));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(responseMock.getWriter()).thenReturn(printWriter);

        profileServlet.doGet(requestMock, responseMock);
        verify(responseMock).setStatus(200);
        assertThat(stringWriter.toString(), CoreMatchers.containsString("Your e-mail address: a@a"));
        assertThat(stringWriter.toString(), CoreMatchers.containsString("It's your profile, andrey"));
    }

    @Test
    public void testNotLoggedIn() throws Exception {
        ProfileServlet profileServlet = new ProfileServlet(context);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        HttpSession sessionMock = mock(HttpSession.class);

        when(accountService.getUserBySession("123asd")).thenReturn(null);
        when(dbService.getUserData(null)).thenReturn(null);

        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getId()).thenReturn("123asd");
        when(responseMock.encodeURL(anyString())).thenReturn("url");

        profileServlet.doGet(requestMock, responseMock);
        verify(responseMock).setStatus(301);
        verify(responseMock).addHeader("Location", "url");
    }
}