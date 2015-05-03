package main;

import base.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LogManager.getLogger();

//    private Map<String, UserProfile> users = new HashMap<>();
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

////    @Override
//    public void addSessions(String sessionId, UserProfile userProfile) {
//        sessions.put(sessionId, userProfile);
//    }
//
////    @Override
//    public UserProfile getUser(String userName) {
//        return users.get(userName);
//    }
//
////    @Override
//    public UserProfile getSessions(String sessionId) {
//        return sessions.get(sessionId);
//    }

    @Override
    public void logout(String sessionId) {
        logger.info("User '{}' logged out", sessions.get(sessionId));
        sessions.remove(sessionId);
    }

////    @Override
//    public int getUsersCount() {
//        return users.size();
//    }

    @Override
    public int getSignedInUsersCount() {
        return sessions.size();
    }

    @Override
    public boolean isUserLoggedIn(String sessionId) {
        return sessions.containsKey(sessionId);
    }

////    @Override
//    public int getChips(String userName) {
//        return users.get(userName).getChips();
//    }
//
////    @Override
//    public void addChips(String userName, int amount) {
//        users.get(userName).setChips(users.get(userName).getChips() + amount);
//    }
//
////    @Override
//    public void subChips(String userName, int amount) {
//        users.get(userName).setChips(users.get(userName).getChips() - amount);
//    }

    @Override
    public String getUserBySession(String sessionId) {
        return sessions.get(sessionId);
    }
}
