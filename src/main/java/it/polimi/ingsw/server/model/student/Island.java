package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Tower;

/**
 * Island Class represent the Islands of the game
 *
 * @author Federico Albertini
 */
public class Island extends StudentHost {

    private int towerCount = 0;
    private boolean isMotherNaturePresent = false;
    private Tower activeTowerType = null;
    private boolean hasStopCard = false;

    /**
     * Returns true if the {@code StopCard} is present
     * @return (type boolean) returns true if the {@code Island} has the {@code StopCard}
     */
    public boolean itHasStopCard() {
        return hasStopCard;
    }

    /**
     * Returns the current active {@link Tower} on this {@code Island}
     * @return (type Tower) the {@code Tower} active on this {@code Island}
     */
    public Tower getActiveTowerType() {
        return activeTowerType;
    }

    /**
     * Gets the {@link  Tower} count on this Island
     * @return  (type int) the {@code Tower} count
     */
    public int getTowerCount() {
        return towerCount;
    }

    /**
     * Returns true if Mother Nature is present on this {@code Island}
     * @return (type boolean) isMotherNaturePresent
     */
    public boolean isMotherNaturePresent() {
        return isMotherNaturePresent;
    }

    /**
     * Sets the stop card
     * @param hasStopCard (type boolean) true if the stop card needs to be set
     */
    public void setStopCard(boolean hasStopCard) {
        this.hasStopCard = hasStopCard;
    }

    /**
     * This method returns the influence of a given {@link Player}
     * @param p (type Player) {@code Player} which it calculates the influence
     * @return (type int) the value of the {@code Player's} influence
     */
    public int getInfluence(Player p) {
        if (p == null) return 0;

        int influence = 0;
        for (Student s : Student.values()) {
            if (p.getControlledProfessors().contains(s.getAssociatedProfessor())) {
                influence += getCount(s);
            }
        }
        if (p.getTowerType() == getActiveTowerType()) {
            influence += getTowerCount();
        }
        return influence;
    }

    /**
     * Checks if this {@code Island} is unifiable with {@code Island}
     * @param island (type Island) the {@code Island} to check
     * @return (type boolean) true if the two {@code Islands} are unifiable
     */
    public boolean isUnifiableWith(Island island) {
        if (island == null) return false;

        return (this.activeTowerType == island.activeTowerType && (this.towerCount > 0 && island.towerCount > 0));
    }

    /**
     * Unifies the {@code Island} by merging the two {@link StudentCollection}
     * @param island (type Island) - the {@code Island} to unify with
     */
    public void acquireIsland(Island island) {
        if (island == null) return;

        if (isUnifiableWith(island)) {
            this.towerCount += island.getTowerCount();
            mergeHostedStudentWith(island);
            isMotherNaturePresent = isMotherNaturePresent || island.isMotherNaturePresent;
        }
    }

    /**
     * Merges the two {@link StudentCollection}
     * @param other (type Island) - {@code Island's} {@code StudentCollection} to merge with
     */
    public void mergeHostedStudentWith(Island other) {
        if (other == null) return;

        for (Student student: Student.values()) {
            placeStudents(student, other.getCount(student));
        }
    }

    /**
     * Counts the number of same {@link  Student} present on this {@code Island}
     * @param s (type Student)  the type of {@code Students} to count
     * @return the counts of same {@code Students}
     */
    public int getNumberOfSameStudents(Student s) {
        if (s == null) return 0;

        return getCount(s);
    }

    /**
     * Sets the {@link Tower} type on this {@code Island}
     * @param t (type Tower) - {@code Tower} to set on this {@code Island}
     */
    public void setTower(Tower t) {
        if (t == null) return;

        activeTowerType = t;
        towerCount = 1;
    }

    /**
     * Sets the {@code Mother Nature} on this {@code Island}
     * @param present (type boolean)
     */
    public void setMotherNaturePresent(boolean present) {
        isMotherNaturePresent = present;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Island island = (Island) o;

        if (towerCount != island.towerCount) return false;
        if (isMotherNaturePresent != island.isMotherNaturePresent) return false;
        if (hasStopCard != island.hasStopCard) return false;
        return activeTowerType == island.activeTowerType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + towerCount;
        result = 31 * result + (isMotherNaturePresent ? 1 : 0);
        result = 31 * result + (activeTowerType != null ? activeTowerType.hashCode() : 0);
        result = 31 * result + (hasStopCard ? 1 : 0);
        return result;
    }
}
