package main;

import base.AccountService;
import base.UserProfile;

import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {
    private Map<String, UserProfile> users = new HashMap<>();
    private Map<String, UserProfile> sessions = new HashMap<>();

    @Override
    public boolean addUser(String userName, UserProfile userProfile) {
        if (users.containsKey(userName))
            return false;
        users.put(userName, userProfile);
        return true;
    }

    @Override
    public void addSessions(String sessionId, UserProfile userProfile) {
        sessions.put(sessionId, userProfile);
    }

    @Override
    public UserProfile getUser(String userName) {
        return users.get(userName);
    }

    @Override
    public UserProfile getSessions(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public void logout(String sessionId) {
        sessions.remove(sessionId);
    }

    @Override
    public int getUsersCount() {
        return users.size();
    }

    @Override
    public int getSignedInUsersCount() {
        return sessions.size();
    }

    @Override
    public int getChips(String userName) {
        return users.get(userName).getChips();
    }

    @Override
    public void addChips(String userName, int amount) {
        users.get(userName).setChips(users.get(userName).getChips() + amount);
    }

    @Override
    public void subChips(String userName, int amount) {
        users.get(userName).setChips(users.get(userName).getChips() - amount);
    }

    @Override
    public boolean isLoggedIn(String userName) {
        return false;
    }

    @Override
    public UserProfile getUserBySession(String sessionId) {
        return sessions.get(sessionId);
    }
}
