package base;

public interface AccountService {

    void addSession(String sessionId, String userName);

//    void addSessions(String sessionId, String userName);

//    UserProfile getUser(String userName);

//    UserProfile getSessions(String sessionId);

    void logout(String sessionId);

//    int getUsersCount();

    int getSignedInUsersCount();

    boolean isUserLoggedIn(String sessionId);

//    int getChips(String userName);

//    void addChips(String userName, int amount);

//    void subChips(String userName, int amount);

    String getUserBySession(String sessionId);

    String generateTokenFor(String name);

    String getUserByToken(String hex);
}
