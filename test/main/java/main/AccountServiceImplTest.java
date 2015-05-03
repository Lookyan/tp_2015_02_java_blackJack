package main;

import base.AccountService;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountServiceImplTest {

    @Test
    public void testAddSession() throws Exception {
        AccountService accountService = new AccountServiceImpl();

        accountService.addSession("132asd", "andrey");
        assertEquals("andrey", accountService.getUserBySession("132asd"));
    }

    @Test
    public void testDoubleAddSession() throws Exception {
        AccountService accountService = new AccountServiceImpl();

        accountService.addSession("132asd", "andrey");
        accountService.addSession("132asd", "bob");
        assertEquals("andrey", accountService.getUserBySession("132asd"));
    }

    @Test
    public void testLogout() throws Exception {
        AccountService accountService = new AccountServiceImpl();

        accountService.addSession("132asd", "andrey");
        accountService.logout("132asd");
        assertNull(accountService.getUserBySession("132asd"));
    }

    @Test
    public void testIsUserLoggedIn() throws Exception {
        AccountService accountService = new AccountServiceImpl();

        accountService.addSession("132asd", "andrey");
        assertTrue(accountService.isUserLoggedIn("132asd"));

        accountService.logout("132asd");
        assertFalse(accountService.isUserLoggedIn("132asd"));
    }
}