package it.polimi.ingsw.model.student;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Professor;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;
import java.util.List;

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
        return (this.activeTowerType == island.activeTowerType && (this.towerCount > 0 && island.towerCount > 0));
    }

    public void acquireIsland(Island island) {
        if (isUnifiableWith(island)) {
            this.towerCount += island.getTowerCount();
            mergeHostedStudentWith(island);
            isMotherNaturePresent = isMotherNaturePresent || island.isMotherNaturePresent;
        }
    }

    public int getNumberOfSameStudents(Student s) {
        return getCount(s);
    }

    public void setTower(Tower t) {
        activeTowerType = t;
        towerCount = 1;
    }

    public void setMotherNaturePresent(Boolean present) {
        isMotherNaturePresent = present;
    }


}
