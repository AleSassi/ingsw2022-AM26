package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.exceptions.model.UnavailableStopCardsException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.student.StudentCollection;

import java.util.List;
/**
 * This Class represent the {@code StopCardActivatorCard}, a card that holds some Stop cards and can place them on Islands
 * @author Alessandro Sassi
 */
public class StopCardActivatorCard extends CharacterCard {

    private int availableStopCards;
    
    /**
     * Creates the Card by initializing its character
     * @param character The character represented by the card
     * @see CharacterCard#CharacterCard(Character)
     */
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
                int islandIdx = userInfo.getSourceIslandIndex();
                if (islandIdx < 0 || islandIdx >= t.getNumberOfIslands()) throw new CharacterCardIncorrectParametersException("StopCardActivatorCard ERROR: sourceIslandIndex must be set in UserInfo to collect the StopCard from the Island!");
                if (t.getIslandAtIndex(islandIdx).itHasStopCard()) {
                    if (availableStopCards == 4) throw new CharacterCardNoMoreUsesAvailableException("StopCardActivatorCard ERROR: you can collect only up to 4 StopCards!");
                    restoreStopCard();
                    t.getIslandAtIndex(islandIdx).setStopCard(false);
                }
            }
            case ToIsland -> {
                int islandIdx = userInfo.getTargetIslandIndex();
                if (islandIdx < 0 || islandIdx >= t.getNumberOfIslands()) throw new CharacterCardIncorrectParametersException("StopCardActivatorCard ERROR: targetIslandIndex must be set in UserInfo to place a StopCard on an Island!");
                if (!t.getIslandAtIndex(islandIdx).itHasStopCard()) {
                    try {
                        removeStopCard();
                        t.getIslandAtIndex(islandIdx).setStopCard(true);
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

    /**
     * Removes a Stop card from the card
     * @throws UnavailableStopCardsException if there are no more Stop Cards on the card
     */
     private void removeStopCard() throws UnavailableStopCardsException {
        if (availableStopCards == 0) throw new UnavailableStopCardsException("StopCardActivatorCard ERROR: to remove a StopCard you need to have more than 1 available card");
        availableStopCards -= 1;
    }

    /**
     * Adds a Stop Card to the card
     */
    private void restoreStopCard() {
        availableStopCards += 1;
    }
    
    @Override
    public void deactivate() {
    }
    
    @Override
    public CharacterCardBean beanify() {
        return new CharacterCardBean(getCharacter(), getPrice(), null, -1, availableStopCards, null);
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
