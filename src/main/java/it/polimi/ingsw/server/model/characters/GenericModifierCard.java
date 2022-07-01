package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;

import java.util.List;
/**
 * This Class represent the {@code GenericModifierCard}
 * @author Alessandro Sassi
 */
public class GenericModifierCard extends CharacterCard {

    /**
     * initialize {@code GenericModifierCard}
     */
    private Student excludedStudent;
    private int savedModifier = 0;

    /**
     * constructor, set the parameter of the card according to
     * @param character (type {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard}) character of the card
     * process it,check parameterif are correct, define the executor parameter, and
     * @return getModifier that contain the parameter of the effect of the activated card otherwise
     * @throws CharacterCardIncorrectParametersException
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
    reset variable
     */
    @Override
    public void deactivate() {
        excludedStudent = null;
        savedModifier = 0;
    }
    /**
     * this method create a copy of card to
     * @param dstCard (type {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard})
     */
    @Override
    protected void copyTo(CharacterCard dstCard) {
        super.copyTo(dstCard);
        ((GenericModifierCard)dstCard).excludedStudent = excludedStudent;
        ((GenericModifierCard)dstCard).savedModifier = savedModifier;
    }
    /**
     abstract method
     */
    @Override
    public void setupWithTable(TableManager t) {}
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
        markCardAsUsedInTurn();
        return executor.performAction(t, players, currentPlayer, userInfo);
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
     * this method is used  to activate the effect of card, setting the correct variable
     * @param tableManager (type {@link it.polimi.ingsw.server.model.TableManager Table}) link to access the tablemanager
     * @param player      (type Player){@link it.polimi.ingsw.server.model.Player Player} list of the player
     * @param targetIslandIndex   (type int)  index of the target island{@link it.polimi.ingsw.server.model.student.Island Island}
     * @param increasesProfControl   (type Bool)true if the effect increase prof control{@link it.polimi.ingsw.server.model.Professor prof}
     *  @param readsTargetIslandInfluence  (type Bool)  if the effect use the index of a target island{@link it.polimi.ingsw.server.model.student.Island Island}
     *  @param readsModifiedMotherNatureSteps (type Bool)if the effect modified MNS
     * @param ignoresSavedStudents (type Bool)if ignore the saved student{@link it.polimi.ingsw.server.model.student.Student Student}
     *  @param ignoresTowers (type Bool)if ignore the tower{@link it.polimi.ingsw.server.model.Tower Tower}
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
     * create a bool from int
     * @param bool (type int)
     * @return (type bool)
     * */
    private int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }
    
    @Override
    /**
     * create a CharacterCardBean from the parameter of this class
     * @return (type CharacterCardBean) {@link it.polimi.ingsw.server.model.characters.CharacterCardBean CharacterCardBean}) that card rapresent
     */
    public CharacterCardBean beanify() {
        return new CharacterCardBean(getCharacter(), getPrice(), excludedStudent, savedModifier, -1, null);
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
