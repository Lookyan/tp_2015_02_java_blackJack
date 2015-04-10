package base;

public interface GameMechanics {

    void addUser(String userSessionId);

    void removeUser(String userSessionId);

    void makeBet(String userSessionId, int bet);

    void hit(String userSessionId);

    void stand(String userSessionId);

}
