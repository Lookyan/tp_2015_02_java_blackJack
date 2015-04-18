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

    void addChips(int amount);

    void subChips(int amount);

    boolean isLoggedIn(String userName);

    UserProfile getUserBySession(String sessionId);
}
