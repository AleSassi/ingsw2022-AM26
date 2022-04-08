package it.polimi.ingsw.model.characters;

public enum Character {
    Abbot(1, 4),
    CheeseMan(2),
    Ambassador(3),
    Magician(1),
    Herbalist(2),
    Centaurus(3),
    Circus(1, 6),
    Swordsman(2),
    Mushroom(3),
    Musician(1),
    Queen(2, 4),
    Thief(3);

    private final int initialPrice;
    private final int hostedStudentsCount;

    Character(int initialPrice, int hostedStudentsCount) {
        this.initialPrice = initialPrice;
        this.hostedStudentsCount = hostedStudentsCount;
    }

    Character(int initialPrice) {
        this(initialPrice, 0);
    }

    public int getInitialPrice() {
        return initialPrice;
    }

    public int getHostedStudentsCount() {
        return hostedStudentsCount;
    }

    public int getMaxNumberOfUsesInTurn() {
        switch (this) {
            case Circus -> {
                return 3;
            }
            case Musician -> {
                return 2;
            }
            default -> {
                return 1;
            }
        }
    }

    public boolean getChangesInfluence() {
        switch (this) {
            case Centaurus, Swordsman, Mushroom -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean getChangesMNSteps() {
        return this == Character.Magician;
    }
    
    public boolean getChangesProfControl() {
        return this == Character.CheeseMan;
    }
}
