package base;

import game.Card;

public interface Deck {

    public boolean isEmpty();

    public Card getCard();

    public void fillDeck();
}
