package it.polimi.ingsw.model.characters;
import it.polimi.ingsw.exceptions.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.exceptions.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.student.StudentCollection;

import java.util.*;

public abstract class CharacterCard {

    private Character character;
    private int priceIncrement = 0;
    private int timesUsedInCurrentTurn = 0;
    protected CharacterCardExecutor executor;

    public CharacterCard(Character character) {
        this.character = character;
        executor = null;
    }

    public void purchase() {
        if (priceIncrement == 0) {
            priceIncrement += 1;
        }
        timesUsedInCurrentTurn = 0;
    }

    protected void markCardAsUsedInTurn() {
        timesUsedInCurrentTurn += 1;
    }

    //region Abstract methods that are implemented by concrete subclasses to provide the card functionality
    public abstract void setupWithTable(TableManager t);
    public abstract int useCard(TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException;
    public abstract StudentCollection getHostedStudents();
    //endregion

    //region Getters for private properties
    public Character getCharacter() {
        return character;
    }

    public int getPriceIncrement() {
        return priceIncrement;
    }

    public int getPrice() {
        return getCharacter().getInitialPrice() + getPriceIncrement();
    }

    public int getTimesUsedInCurrentTurn() {
        return timesUsedInCurrentTurn;
    }
    //endregion

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
