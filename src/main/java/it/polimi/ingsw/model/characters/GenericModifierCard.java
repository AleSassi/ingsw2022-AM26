package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.student.*;

import java.util.List;

public class GenericModifierCard extends CharacterCard {

    private Student excludedStudent;
    private int savedModifier = 0;

    public GenericModifierCard(Character character) {
        super(character);
        switch (character) {
            case CheeseMan -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                // When computing the control of a Professor, if we add 1 to the "real" value of the Player the ">" check becomes ">=", as required by the card
                return getModifier(t, currentPlayer, -1, true, false, false, false, false, false);
            };
            case Ambassador -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> getModifier(t, currentPlayer, userInfo.getTargetIslandIndex(), false, true, false, false, false, false);
            case Magician -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (getTimesUsedInCurrentTurn() == 1) {
                    savedModifier = userInfo.getChosenMotherNatureAdditionalSteps();
                }
                return getModifier(t, currentPlayer, -1, false, false, false, false, false, true);
            };
            case Centaurus -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> getModifier(t, currentPlayer, userInfo.getTargetIslandIndex(), false, false, false, true, false, false);
            case Swordsman -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> getModifier(t, currentPlayer, -1, false, false, false, false, true, false);
            case Mushroom -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (excludedStudent == null) {
                    excludedStudent = userInfo.getSrcStudentColor();
                }
                System.out.println(excludedStudent);
                System.out.println(userInfo.getSrcStudentColor());
                System.out.println(userInfo.getTargetIslandIndex());
                if (userInfo.getTargetIslandIndex() == -1) return 0;
                return getModifier(t, currentPlayer, userInfo.getTargetIslandIndex(), false, false, true, false, false, false);
            };
            default -> executor = null;
        }
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

    private int getModifier(TableManager tableManager, Player player, int targetIslandIndex, boolean increasesProfControl, boolean readsTargetIslandInfluence, boolean ignoresSavedStudents, boolean ignoresTowers, boolean isConstant, boolean readsModifiedMotherNatureSteps) throws CharacterCardIncorrectParametersException {
        if (ignoresSavedStudents && (excludedStudent == null || targetIslandIndex == -1)) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: Attempting to ignore saved students, but excludedStudents has not been set or targetIslandIndex is not valid!");
        if (ignoresTowers && targetIslandIndex == -1) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: Attempting to ignore towers on island, but targetIslandIndex is not valid!");
        if (readsTargetIslandInfluence && (targetIslandIndex == -1 || player == null)) throw new CharacterCardIncorrectParametersException("GenericModifierCard ERROR: Attempting to read influence on island, but targetIslandIndex is not valid or player is null!");

        return (boolToInt(increasesProfControl))
                + (boolToInt(ignoresSavedStudents) * (excludedStudent == null ? 0 : -1 * tableManager.getIslandAtIndex(targetIslandIndex).getNumberOfSameStudents(excludedStudent)))
                + (boolToInt(ignoresTowers) * (targetIslandIndex == -1 ? 0 : -1 * tableManager.getIslandAtIndex(targetIslandIndex).getTowerCount()))
                + (boolToInt(isConstant) * 2)
                + (boolToInt(readsModifiedMotherNatureSteps) * savedModifier)
                + (boolToInt(readsTargetIslandInfluence) * (targetIslandIndex == -1 ? 0 : tableManager.getIslandAtIndex(targetIslandIndex).getInfluence(player)));
    }

    private int boolToInt(boolean bool) {
        return bool ? 1 : 0;
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