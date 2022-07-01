package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;

import java.util.List;
/**
 * This Class represent the {@code GenericModifierCard}, a card which returns and/or memorizes a generic integer modifier
 * @author Alessandro Sassi
 */
public class GenericModifierCard extends CharacterCard {

    private Student excludedStudent;
    private int savedModifier = 0;

    /**
     * Constructor, creates the card and initializes the card handler with its behavior
     * @param character The character of the Card that is used to determine the handler actions
     */
    public GenericModifierCard(Character character) {
        super(character);
        switch (character) {
            case CheeseMan -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                // When computing the control of a Professor, if we add 1 to the "real" value of the Player the ">" check becomes ">=", as required by the card
                return getModifier(t, currentPlayer, -1, true, false, false, false, false, false);
            };
            case Ambassador -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (userInfo == null) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: userInfo is NULL");
                
                return getModifier(t, currentPlayer, userInfo.getTargetIslandIndex(), false, true, false, false, false, false);
            };
            case Magician -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (userInfo == null) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: userInfo is NULL");
    
                if (getTimesUsedInCurrentTurn() == 1) {
                    int additionalSteps = userInfo.getChosenMotherNatureAdditionalSteps();
                    if (additionalSteps < 0 || additionalSteps > 2) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: addtionalMNSteps must be between 0 and 2");
                    savedModifier = userInfo.getChosenMotherNatureAdditionalSteps();
                }
                return getModifier(t, currentPlayer, -1, false, false, false, false, false, true);
            };
            case Centaurus -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (userInfo == null) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: userInfo is NULL");
    
                return getModifier(t, currentPlayer, userInfo.getTargetIslandIndex(), false, false, false, true, false, false);
            };
            case Swordsman -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> getModifier(t, currentPlayer, -1, false, false, false, false, true, false);
            case Mushroom -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (userInfo == null) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: userInfo is NULL");
    
                if (excludedStudent == null) {
                    excludedStudent = userInfo.getSrcStudentColor();
                    return -1;
                } else {
                    int islandIdx = userInfo.getTargetIslandIndex();
                    if (islandIdx < 0 || islandIdx >= t.getNumberOfIslands()) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: Island index out of range");
                    return getModifier(t, currentPlayer, islandIdx, false, false, true, false, false, false);
                }
            };
            default -> executor = null;
        }
    }
    
    /**
     * @see CharacterCard#deactivate()
     */
    @Override
    public void deactivate() {
        excludedStudent = null;
        savedModifier = 0;
    }
    
    @Override
    protected void copyTo(CharacterCard dstCard) {
        super.copyTo(dstCard);
        ((GenericModifierCard)dstCard).excludedStudent = excludedStudent;
        ((GenericModifierCard)dstCard).savedModifier = savedModifier;
    }
    
    @Override
    public void setupWithTable(TableManager t) {}
    
    @Override
    public int useCard(TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException {
        markCardAsUsedInTurn();
        return executor.performAction(t, players, currentPlayer, userInfo);
    }
    
    @Override
    public StudentCollection getHostedStudents() {
        return new StudentCollection();
    }

    /**
     * Computes the correct modifier that needs to be returned by the card
     * @param tableManager The Tbale manager object used to get the Table data
     * @param player The current Player
     * @param targetIslandIndex The target island index
     * @param increasesProfControl Whether it increases the control of Professors
     * @param readsTargetIslandInfluence Whether the effect consists in reading the target island influence
     * @param readsModifiedMotherNatureSteps Whether the effect reads the memorized Mother Nature steps
     * @param ignoresSavedStudents Whether it should ignore the saved Students
     * @param ignoresTowers Whether it should ignore Towers
     * @throws CharacterCardIncorrectParametersException if the parameter of the effect of card are incorrect
     */
    private int getModifier(TableManager tableManager, Player player, int targetIslandIndex, boolean increasesProfControl, boolean readsTargetIslandInfluence, boolean ignoresSavedStudents, boolean ignoresTowers, boolean isConstant, boolean readsModifiedMotherNatureSteps) throws CharacterCardIncorrectParametersException {
        boolean islandIdxOutOfRange = (targetIslandIndex < 0 || targetIslandIndex >= tableManager.getNumberOfIslands());
        if (ignoresSavedStudents && (excludedStudent == null || islandIdxOutOfRange)) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: Attempting to ignore saved students, but excludedStudents has not been set or targetIslandIndex is not valid!");
        if (ignoresTowers && islandIdxOutOfRange) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: Attempting to ignore towers on island, but targetIslandIndex is not valid!");
        if (readsTargetIslandInfluence && (islandIdxOutOfRange || player == null)) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: Attempting to read influence on island, but targetIslandIndex is not valid or player is null!");

        return (boolToInt(increasesProfControl))
                + (boolToInt(ignoresSavedStudents) * (excludedStudent == null ? 0 : -1 * tableManager.getIslandAtIndex(targetIslandIndex).getNumberOfSameStudents(excludedStudent)))
                + (boolToInt(ignoresTowers) * (targetIslandIndex == -1 ? 0 : -1 * tableManager.getIslandAtIndex(targetIslandIndex).getTowerCount()))
                + (boolToInt(isConstant) * 2)
                + (boolToInt(readsModifiedMotherNatureSteps) * savedModifier)
                + (boolToInt(readsTargetIslandInfluence) * (targetIslandIndex == -1 ? 0 : tableManager.getIslandAtIndex(targetIslandIndex).getInfluence(player)));
    }
    
    /**
     * Converts a boolean to an integer
     * @param bool The boolean to convert
     * @return The converted integer
     * */
    private int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }
    
    @Override
    public CharacterCardBean beanify() {
        return new CharacterCardBean(getCharacter(), getPrice(), excludedStudent, savedModifier, -1, null);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GenericModifierCard that = (GenericModifierCard) o;

        if (savedModifier != that.savedModifier) return false;
        return excludedStudent == that.excludedStudent;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (excludedStudent != null ? excludedStudent.hashCode() : 0);
        result = 31 * result + savedModifier;
        return result;
    }
}
