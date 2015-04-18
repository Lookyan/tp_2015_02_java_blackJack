package game;

public class Card {

    private char rank;
    private char suit;

    private int value;

    public Card(char rank, char suit) {
        this.rank = rank;
        this.suit = suit;
        calcValue();
    }

    private void calcValue() {
        value = Character.digit(rank, 10);
        if (value == -1) {
            switch (rank) {
                case 'A':
                    value = 11; // TODO: change
                    break;

                case 'K':case 'Q':
                case 'J':case 'T':
                    value = 10;
                    break;
            }
        }
    }

    public boolean isAce() {
        return rank == 'A';
    }

    public void changeAceValue() {
        if (isAce()) {
            value = value == 11 ? 1 : 11;
        }
    }

    @Override
    public String toString() {
        return "" + rank + suit;
    }

    public int getValue() {
        return value;
    }
}
