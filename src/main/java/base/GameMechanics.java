package base;

public interface GameMechanics {

    void addUser(String userName);

    void removeUser(String userName);

    void makeBet(String userName, int bet);

    void hit(String userName);

    void stand(String userName);

}
