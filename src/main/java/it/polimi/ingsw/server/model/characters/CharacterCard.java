package it.polimi.ingsw.server.model.characters;
import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.server.model.TableManager;

import java.util.*;
/**
 * This Class represent the {@code CharacterCard}
 * @author Alessandro Sassi
 */
public abstract class CharacterCard {
    /**
     * Initialize {@code CharacterCard}
     */
    private Character character;
    private int priceIncrement = 0;
    private int timesUsedInCurrentTurn = 0;
    //TODO: When implementing state preservation make sure to have some way to recreate the executors (e.g. at first use if null run the constructor?)
    protected transient CharacterCardExecutor executor; //Make transient to avoid serialization

    /**
     * Constructor
     * @param character (type  {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard}) that card rapresent
     */
    public CharacterCard(Character character) {
        this.character = character;
        executor = null;
    }

    /**
     * this method is used to purchase the card, it increment the price and set the uses to 0
     */
    public void purchase() {
        if (priceIncrement == 0) {
            priceIncrement += 1;
        }
        timesUsedInCurrentTurn = 0;
    }

    /**
     * this method is used to mark as used a card with his variable
     */
    protected void markCardAsUsedInTurn() {
        timesUsedInCurrentTurn += 1;
    }

    //region Abstract methods that are implemented by concrete subclasses to provide the card functionality
    /**
     * abstract method
     */
    public abstract void setupWithTable(TableManager t);
    /**
     * abstract method
     */
    public abstract int useCard(TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException;
    /**
     * abstract method
     */
    public abstract StudentCollection getHostedStudents();
    /**
     * abstract method
     */
    public abstract CharacterCardBean beanify();
    /**
     * abstract method
     */
    public abstract void deactivate();
    //endregion

    //region Getters for private properties
    /**
     * getter
     * @return (type {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard}) that card rapresent
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * getter
     * @return (type int) the increment of price
     */
    public int getPriceIncrement() {
        return priceIncrement;
    }

    /**
     * getter
     * @return (type int) the increment of price
     */
    public int getPrice() {
        return getCharacter().getInitialPrice() + getPriceIncrement();
    }

    /**
     * getter
     * @return (type int) the increment of price
     */
    public int getTimesUsedInCurrentTurn() {
        return timesUsedInCurrentTurn;
    }
    //endregion

    /**
     * this method create a copy of card to
     * @param dstCard (type {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard})
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
