package base;

import main.UserProfile;

public interface AccountService {
    public boolean addUser(String userName, UserProfile userProfile);

    public void addSessions(String sessionId, UserProfile userProfile);

    public UserProfile getUser(String userName);

    public UserProfile getSessions(String sessionId);

    public void logout(String sessionId);

    public int getUsersCount();

    public int getSignedInUsersCount();

    public int getMoney(String sessionId);
}
