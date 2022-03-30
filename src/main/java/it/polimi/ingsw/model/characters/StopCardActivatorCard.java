package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TableManager;
import it.polimi.ingsw.model.student.StudentCollection;

import java.util.List;

public class StopCardActivatorCard extends CharacterCard {

    private int availableStopCards;

    public StopCardActivatorCard(Character character) {
        super(character);
    }

    @Override
    public void setupWithTable(TableManager t) {
        availableStopCards = 4;
    }

    @Override
    public int useCard(TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException {
        if (userInfo.getStopCardMovementMode() == null) throw new CharacterCardIncorrectParametersException("StopCardActivatorCard ERROR: stopCardMovementMode must be set in UserInfo!");
        switch (userInfo.getStopCardMovementMode()) {
            case ToCard -> {
                if (userInfo.getSourceIslandIndex() == -1) throw new CharacterCardIncorrectParametersException("StopCardActivatorCard ERROR: sourceIslandIndex must be set in UserInfo to collect the StopCard from the Island!");
                if (t.getIslandAtIndex(userInfo.getSourceIslandIndex()).itHasStopCard()) {
                    if (availableStopCards == 4) throw new CharacterCardNoMoreUsesAvailableException("StopCardActivatorCard ERROR: you can collect only up to 4 StopCards!");
                    restoreStopCard();
                    t.getIslandAtIndex(userInfo.getTargetIslandIndex()).setStopCard(false);
                }
            }
            case ToIsland -> {
                if (userInfo.getTargetIslandIndex() == -1) throw new CharacterCardIncorrectParametersException("StopCardActivatorCard ERROR: targetIslandIndex must be set in UserInfo to place a StopCard on an Island!");
                if (!t.getIslandAtIndex(userInfo.getTargetIslandIndex()).itHasStopCard()) {
                    try {
                        removeStopCard();
                        t.getIslandAtIndex(userInfo.getTargetIslandIndex()).setStopCard(true);
                    } catch (UnavailableStopCardsException e) {
                        throw new CharacterCardNoMoreUsesAvailableException("StopCardActivatorCard ERROR: you can place only up to 4 StopCards!");
                    }
                }
            }
        }
        return availableStopCards;
    }

    @Override
    public StudentCollection getHostedStudents() {
        return new StudentCollection();
    }

    private void removeStopCard() throws UnavailableStopCardsException {
        if (availableStopCards == 0) throw new UnavailableStopCardsException("StopCardActivatorCard ERROR: to remove a StopCard you need to have more than 1 available card");
        availableStopCards -= 1;
    }

    private void restoreStopCard() {
        availableStopCards += 1;
    }

    @Override
    protected void copyTo(CharacterCard dstCard) {
        super.copyTo(dstCard);
        ((StopCardActivatorCard)dstCard).availableStopCards = availableStopCards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StopCardActivatorCard that = (StopCardActivatorCard) o;

        return availableStopCards == that.availableStopCards;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + availableStopCards;
        return result;
    }
}
