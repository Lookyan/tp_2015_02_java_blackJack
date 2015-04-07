package interfaces;

public interface GameTable {

    void addUser(String userSessionId);

    String processStep();

    boolean isFull();
}