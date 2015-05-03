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

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AdminServletTest {

    Context context;
    AccountService accountService;
    DBService dbService;

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Before
    public void setUp() throws Exception {
        context = new Context();
        accountService = mock(AccountService.class);
        dbService = mock(DBService.class);

        when(accountService.getSignedInUsersCount()).thenReturn(54);
        when(dbService.countAllUsers()).thenReturn((long) 128);

        context.add(AccountService.class, accountService);
        context.add(DBService.class, dbService);
    }

    @Test
    public void testDoGet() throws Exception {
        AdminServlet adminServlet = new AdminServlet(context);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(requestMock.getParameter("stop")).thenReturn(null);
        when(responseMock.getWriter()).thenReturn(printWriter);

        adminServlet.doGet(requestMock, responseMock);
        verify(responseMock).setStatus(200);
        assertThat(stringWriter.toString(), CoreMatchers.containsString("Registered users: 128"));
        assertThat(stringWriter.toString(), CoreMatchers.containsString("Signed in users: 54"));
    }

    @Test
    public void testShutdown() throws Exception {
        AdminServlet adminServlet = new AdminServlet(context);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);

        when(requestMock.getParameter("stop")).thenReturn("50");

        exit.expectSystemExit();
        adminServlet.doGet(requestMock, responseMock);
    }
}