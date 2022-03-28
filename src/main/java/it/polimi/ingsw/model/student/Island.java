package it.polimi.ingsw.model.student;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tower;

public class Island extends StudentHost{
    private int towerCount = 0;
    private boolean isMotherNaturePresent = false;
    private Tower activeTowerType = null;
    private boolean hasStopCard = false;

    public boolean isHasStopCard() {
        return hasStopCard;
    }

    public Tower getActiveTowerType() throws  NoTowerActiveException{
        if(activeTowerType == null) throw new NoTowerActiveException();
        else
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


    public int getInfluence(Player P) {
        int count = 0;
        return count;
    }
    public boolean isUnifiableWith(Island island){
        return (this.activeTowerType == island.activeTowerType && (this.towerCount > 0 && island.towerCount > 0));
    }
    public void acquireIsland(Island island){
        if(isUnifiableWith(island)) {
            this.towerCount += island.getTowerCount();

        }
    }

    public int getNumberOfSameStudents (Student s){
        return getCount(s);
    }
    public void setTower(Tower t){
        activeTowerType = t;
    }
    public void setMotherNaturePresent(Boolean present) {
        isMotherNaturePresent = present;
    }


}
