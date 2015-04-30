package base;

import game.GameTableException;

public interface GameTable {

    public boolean isFull();

    public void addUser(String userName) throws GameTableException;

    public void makeBet(String userName, int bet) throws GameTableException;

    public void hit(String userName) throws GameTableException;

    public void stand(String userName) throws GameTableException;

    public void removeUser(String userName) throws GameTableException;

}
