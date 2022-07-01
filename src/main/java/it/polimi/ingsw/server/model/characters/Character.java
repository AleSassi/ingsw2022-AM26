package it.polimi.ingsw.server.model.characters;

/**
 * This Class represent the {@code Character}
 * @author Alessandro Sassi
 */
public enum Character {
    /**
     initialize value of enum
     */
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
    /**
     initialize {@code Character}
     */
    private final int initialPrice;
    private final int hostedStudentsCount;


    /**
     * Constructor
     * @param initialPrice (type int) price of the card at the start
     * @param hostedStudentsCount (type int) number of {@link it.polimi.ingsw.server.model.student.Student Student} on the card
     */
    Character(int initialPrice, int hostedStudentsCount) {
        this.initialPrice = initialPrice;
        this.hostedStudentsCount = hostedStudentsCount;
    }

    /**
     * Constructor
     * @param initialPrice (type int) price of the card at the start
     */
    Character(int initialPrice) {
        this(initialPrice, 0);
    }

    /**
     * Getter
     * @return (type int) the price of card
     */
    public int getInitialPrice() {
        return initialPrice;
    }

    /**
     * Getter
     * @return (type int) counter of {@link it.polimi.ingsw.server.model.student.Student Student} on the card
     */
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

    /**
     * Getter
     * @return (type bool) if the influence is changed or not
     */
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

    /**
     * Getter
     * @return (type boolean) if the MNstep are changed
     */
    public boolean getChangesMNSteps() {
        return this == Character.Magician;
    }

    /**
     * Getter
     * @return (type boolean) if the MNstep are changed
     */
    public boolean getChangesProfControl() {
        return this == Character.CheeseMan;
    }
    
    public boolean getHostsStopCards() { return this == Herbalist; };
}
