package it.polimi.ingsw.server.model.characters;
import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.server.model.TableManager;

import java.util.*;
/**
 * This Class is the abstract base class representing the {@code CharacterCard}s
 * @author Alessandro Sassi
 */
public abstract class CharacterCard {
    
    private Character character;
    private int priceIncrement = 0;
    private int timesUsedInCurrentTurn = 0;
    protected transient CharacterCardExecutor executor; //Make transient to avoid serialization

    /**
     * Constructor, creates the Character ard with the related Character
     * @param character (type {@link it.polimi.ingsw.server.model.characters.Character Character}) The Character that the card represents
     */
    public CharacterCard(Character character) {
        this.character = character;
        executor = null;
    }

    /**
     * Purchase the card, incrementing its price if needed and resetting the number of times used in the same turn
     */
    public void purchase() {
        if (priceIncrement == 0) {
            priceIncrement += 1;
        }
        timesUsedInCurrentTurn = 0;
    }

    /**
     * Marks a card as used in the current turn
     */
    protected void markCardAsUsedInTurn() {
        timesUsedInCurrentTurn += 1;
    }

    //region Abstract methods that are implemented by concrete subclasses to provide the card functionality
    /**
     * Sets up the card data, optionally taking some data from the table
     * @param t The Table Manager object which might be used to get data (such as Students)
     */
    public abstract void setupWithTable(TableManager t);
    
    /**
     * Uses a Character Card effect
     * @param t The Table Manager object which might be used to get data (such as Students)
     * @param players The List of Players in the match
     * @param currentPlayer The current Player who used the card
     * @param userInfo Additional card parameters for the action
     * @return an integer holding the numeric result of the effect
     * @throws CharacterCardIncorrectParametersException If the parameters are incorrect
     * @throws CharacterCardNoMoreUsesAvailableException If the card cannot be used anymore in the same turn
     */
    public abstract int useCard(TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException;
    
    /**
     * Gets the collection of Students hosted by the Card
     * @return The collection of Students hosted by the card
     */
    public abstract StudentCollection getHostedStudents();
    
    /**
     * Builds the Bean of the card, a representation suited to be sent over the network
     * @return The Bean of the card, a representation suited to be sent over the network
     */
    public abstract CharacterCardBean beanify();
    
    /**
     * Deactivates the card, cleaning up its internal transient state
     */
    public abstract void deactivate();
    //endregion

    //region Getters for private properties
    /**
     * Gets the Character associated with the Card
     * @return (type Character) The Character associated with the Card
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * Gets the card's price increment
     * @return (type int) The card's price increment
     */
    public int getPriceIncrement() {
        return priceIncrement;
    }

    /**
     * Gets the card's total price
     * @return (type int) The card's total price
     */
    public int getPrice() {
        return getCharacter().getInitialPrice() + getPriceIncrement();
    }

    /**
     * Gets the number of times the card has been used in the current turn
     * @return (type int) the increment of price
     */
    public int getTimesUsedInCurrentTurn() {
        return timesUsedInCurrentTurn;
    }
    //endregion

    /**
     * Creates a shallow clone of the Card
     * @param dstCard (type {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard}) The card where the method copies the object
     */
    protected void copyTo(CharacterCard dstCard) {
        dstCard.character = character;
        dstCard.priceIncrement = priceIncrement;
        dstCard.timesUsedInCurrentTurn = timesUsedInCurrentTurn;
        dstCard.executor = executor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharacterCard that = (CharacterCard) o;

        if (priceIncrement != that.priceIncrement) return false;
        if (timesUsedInCurrentTurn != that.timesUsedInCurrentTurn) return false;
        if (character != that.character) return false;
        return Objects.equals(executor, that.executor);
    }

    @Override
    public int hashCode() {
        int result = character.hashCode();
        result = 31 * result + priceIncrement;
        result = 31 * result + timesUsedInCurrentTurn;
        result = 31 * result + (executor != null ? executor.hashCode() : 0);
        return result;
    }
}
