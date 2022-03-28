package it.polimi.ingsw.model.student;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tower;

public class Island extends StudentHost{
    private int towerCount = 0;
    private boolean isMotherNaturePresent = false;
    private Tower activeTowerType = null;

    private Island island = new Island();


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

    public int getInfluence(Player P) {
        int count = 0;
        return count;
    }

    public int getNumberOfSameStudents (Student s){
        return island.getCount(s);
    }
    public void setTower(Tower t){
        activeTowerType = t;
    }
    public void setMotherNaturePresent(Boolean present) {
        isMotherNaturePresent = present;
    }



}
