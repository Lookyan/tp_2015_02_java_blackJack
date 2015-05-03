package frontend.servlets;

import base.AccountService;
import base.DBService;
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

public class LogoutServletTest {

    Context context;
    AccountService accountService;

    @Before
    public void setUp() throws Exception {
        context = new Context();
        accountService = mock(AccountService.class);

        context.add(AccountService.class, accountService);
    }

    @Test
    public void testDoGet() throws Exception {
        LogoutServlet logoutServlet = new LogoutServlet(context);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        HttpSession sessionMock = mock(HttpSession.class);

        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getId()).thenReturn("123asd");

//        when(requestMock.getSession().getId()).thenReturn("123asd");
        when(responseMock.encodeURL(anyString())).thenReturn("url");

        logoutServlet.doGet(requestMock, responseMock);
        verify(accountService).logout("123asd");
        verify(responseMock).setStatus(301);
        verify(responseMock).addHeader("Location", "url");
    }

}