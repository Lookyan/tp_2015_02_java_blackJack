package interfaces;

public interface GameMechanics {

    public void addUser(String sessionId);

    public void hit(String sessionId);

    public void stand(String sessionId);

//    public void incrementScore(String userName);
//
//    public void run();
}
