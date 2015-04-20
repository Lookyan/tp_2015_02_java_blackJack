package base;

public interface AccountService {

    boolean addUser(String userName, UserProfile userProfile);

    void addSessions(String sessionId, UserProfile userProfile);

    UserProfile getUser(String userName);

    UserProfile getSessions(String sessionId);

    void logout(String sessionId);

    int getUsersCount();

    int getSignedInUsersCount();



    int getChips(String userName);

    void addChips(String userName, int amount);

    void subChips(String userName, int amount);

    boolean isLoggedIn(String userName);

    UserProfile getUserBySession(String sessionId);
}
