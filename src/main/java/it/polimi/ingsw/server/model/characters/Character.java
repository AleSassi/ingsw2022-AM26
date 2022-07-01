package it.polimi.ingsw.server.model.characters;

/**
 * This Class represents the {@code Character} used for Character cards
 * @author Alessandro Sassi
 */
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


    /**
     * Constructor, creates the Character with the specified parameters
     * @param initialPrice (type int) The price of the card at the start of the match
     * @param hostedStudentsCount (type int) The number of {@link it.polimi.ingsw.server.model.student.Student Students} on the card
     */
    Character(int initialPrice, int hostedStudentsCount) {
        this.initialPrice = initialPrice;
        this.hostedStudentsCount = hostedStudentsCount;
    }

    /**
     * Constructor, creates the Character with the specified parameters and 0 hosted students
     * @param initialPrice (type int) The price of the card at the start of the match
     */
    Character(int initialPrice) {
        this(initialPrice, 0);
    }

    /**
     * Extracts the initial price of the card
     * @return (type int) the initial price of card
     */
    public int getInitialPrice() {
        return initialPrice;
    }

    /**
     * Extracts the number of hosted Students
     * @return (type int) number of {@link it.polimi.ingsw.server.model.student.Student Student} on the card
     */
    public int getHostedStudentsCount() {
        return hostedStudentsCount;
    }
    
    /**
     * Finds the maximum number of times the card can be used by the same player in the same turn
     * @return The maximum number of times the card can be used by the same player in the same turn
     */
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
     * Finds whether the card effect is related to changing the influence count
     * @return (type bool) If the card effect is related to changing the influence count
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
     * Finds whether the card effect is related to changing the number of Mother Nature steps
     * @return (type boolean) If the card effect is related to changing the number of Mother Nature steps
     */
    public boolean getChangesMNSteps() {
        return this == Character.Magician;
    }

    /**
     * Finds whether the card effect is related to changing the Professor control
     * @return (type boolean) If the card effect is related to changing the Professor control
     */
    public boolean getChangesProfControl() {
        return this == Character.CheeseMan;
    }
    
    /**
     * Finds whether the card effect is related to hosting Stop Cards
     * @return (type boolean) If the card effect is related to hosting Stop Cards
     */
    public boolean getHostsStopCards() { return this == Herbalist; };
}
