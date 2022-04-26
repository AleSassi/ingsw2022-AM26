package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Tower;

public class Island extends StudentHost {
    private int towerCount = 0;
    private boolean isMotherNaturePresent = false;
    private Tower activeTowerType = null;
    private boolean hasStopCard = false;

    public boolean itHasStopCard() {
        return hasStopCard;
    }

    public Tower getActiveTowerType() {
        return activeTowerType;
    }

    public int getTowerCount() {
        return towerCount;
    }

    public boolean isMotherNaturePresent() {
        return isMotherNaturePresent;
    }

    public void setStopCard(boolean hasStopCard) {
        this.hasStopCard = hasStopCard;
    }

    /**
     * This method returns the influence of a given Player
     * @param p Player which we calculate the influence
     * @return the value of the Player
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

    public boolean isUnifiableWith(Island island) {
        if (island == null) return false;

        return (this.activeTowerType == island.activeTowerType && (this.towerCount > 0 && island.towerCount > 0));
    }

    public void acquireIsland(Island island) {
        if (island == null) return;

        if (isUnifiableWith(island)) {
            this.towerCount += island.getTowerCount();
            mergeHostedStudentWith(island);
            isMotherNaturePresent = isMotherNaturePresent || island.isMotherNaturePresent;
        }
    }

    public void mergeHostedStudentWith(Island other) {
        if (other == null) return;

        for (Student student: Student.values()) {
            placeStudents(student, other.getCount(student));
        }
    }

    public int getNumberOfSameStudents(Student s) {
        if (s == null) return 0;

        return getCount(s);
    }

    public void setTower(Tower t) {
        if (t == null) return;

        activeTowerType = t;
        towerCount = 1;
    }

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
