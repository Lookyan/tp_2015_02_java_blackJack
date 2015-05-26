package main;

import base.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LogManager.getLogger();
    private static final Random random = new Random();

    private Map<String, String> sessions = new HashMap<>();
    private Map<String, String> phoneHexes = new HashMap<>();

    private static final int TOKEN_LENGTH = 4;

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

    @Override
    public String generateTokenFor(String name) {
        String newToken;
        do {
            StringBuffer stringBuffer = new StringBuffer();
            while (stringBuffer.length() < TOKEN_LENGTH) {
                stringBuffer.append(Integer.toHexString(random.nextInt()));
            }
            newToken = stringBuffer.toString().substring(0, TOKEN_LENGTH);
        } while(phoneHexes.containsValue(newToken));

        phoneHexes.put(newToken, name);
        return newToken;
    }

    @Override
    public String getUserByToken(String token) {
        return phoneHexes.get(token);
    }
}
