package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.exceptions.model.UnavailableStopCardsException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.student.StudentCollection;

import java.util.List;
/**
 * This Class represent the {@code StopCardActivatorCard}
 * @author Alessandro Sassi
 */
public class StopCardActivatorCard extends CharacterCard {

    private int availableStopCards;
    /**
     * constructor
     * @param character (type {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard}) character that card represent
     */
    public StopCardActivatorCard(Character character) {
        super(character);
    }

    /**
     * setup parameter of table
     * @param t (type {@link it.polimi.ingsw.server.model.TableManager Table}) link to access the Table
     */
    @Override
    public void setupWithTable(TableManager t) {
        availableStopCards = 4;
    }

    /**
     * this method is used to to activate the effect of card, setting the correct variable
     * @param t (type {@link it.polimi.ingsw.server.model.TableManager Table}) link to access the manager
     * @param players      (type list of Player){@link it.polimi.ingsw.server.model.Player Player} list of the player
     * @param currentPlayer    (type Player) {@code Player} the player which are playing
     * @param userInfo (type CharacterCardParamSet){@link it.polimi.ingsw.server.model.characters.CharacterCardParamSet CharacterCardParamSet}
        @throws CharacterCardIncorrectParametersException if the parameter of the effect of card are incorrect
     @throws  CharacterCardNoMoreUsesAvailableException player finished the uses of card
     */
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
    /**
     * getter
     * @return (type collection){@link it.polimi.ingsw.server.model.student.StudentCollection collection} of student hosted by the card
     */
    @Override
    public StudentCollection getHostedStudents() {
        return new StudentCollection();
    }

    /**
     *decrement the number of stop card
     @throws UnavailableStopCardsException if stop card are 0
     */
     private void removeStopCard() throws UnavailableStopCardsException {
        if (availableStopCards == 0) throw new UnavailableStopCardsException("StopCardActivatorCard ERROR: to remove a StopCard you need to have more than 1 available card");
        availableStopCards -= 1;
    }

    /**
     *increment the number of stop card
     */
    private void restoreStopCard() {
        availableStopCards += 1;
    }
    
    @Override
    /**
     *delete effect of the card
     */
    public void deactivate() {
    }
    
    @Override
    /**
     * create a CharacterCardBean from the parameter of this class
     * @return (type CharacterCardBean) {@link it.polimi.ingsw.server.model.characters.CharacterCardBean CharacterCardBean}) that card rapresent
     */
    public CharacterCardBean beanify() {
        return new CharacterCardBean(getCharacter(), getPrice(), null, -1, availableStopCards, null);
    }
    /**
     * create a CharacterCardBean from the parameter of this class
     * @return (type CharacterCardBean) {@link it.polimi.ingsw.server.model.characters.CharacterCardBean CharacterCardBean}) that card rapresent
     */
    @Override
    protected void copyTo(CharacterCard dstCard) {
        super.copyTo(dstCard);
        ((StopCardActivatorCard)dstCard).availableStopCards = availableStopCards;
    }
    /**verify if this class is equal to
     * @param(type Object)
     * @return (type bool) true if class are equal, false otherwise
     */
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
