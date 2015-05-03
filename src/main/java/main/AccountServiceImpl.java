package main;

import base.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LogManager.getLogger();

    private Map<String, String> sessions = new HashMap<>();

    @Override
    public void addSession(String sessionId, String userName) {
        if (!sessions.containsKey(sessionId)) {
            sessions.put(sessionId, userName);
            logger.info("Added logged in user '{}'", userName);
        } else {
            logger.error("Adding logged in user");
        }
    }

    @Override
    public void logout(String sessionId) {
        logger.info("User '{}' logged out", sessions.get(sessionId));
        sessions.remove(sessionId);
    }

    @Override
    public int getSignedInUsersCount() {
        return sessions.size();
    }

    @Override
    public boolean isUserLoggedIn(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    @Override
    public String getUserBySession(String sessionId) {
        return sessions.get(sessionId);
    }
}
