package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.*;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.server.model.student.StudentHost;
import it.polimi.ingsw.utils.cli.StringFormatter;

import java.util.*;
/**
 * This Class represent the {@code StudentHostingCard}, a card that can have a set of Students that the user can pick
 * @author Alessandro Sassi
 */
public class StudentHostingCard extends CharacterCard {
    
    private StudentCollection hostedStudents;
    
    /**
     * Constructs the card using the provided Character, and initializes the handler of the card action depending on it
     * @param character The Character represented by the card
     * @see CharacterCard#CharacterCard(Character)
     */
    public StudentHostingCard(Character character) {
        super(character);
        switch (character) {
            case Abbot -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (userInfo == null) throw new CharacterCardIncorrectParametersException("StudentHostingCard ERROR: CharacterCardParamSet is NULL");
                
                checkMaxUsesInTurn(character);
                int dstIslandIdx = userInfo.getTargetIslandIndex();
                Student chosenStudentType = userInfo.getSrcStudentColor();
                if (t == null || chosenStudentType == null || (dstIslandIdx < 0 || dstIslandIdx >= t.getNumberOfIslands()) || hostedStudents.getCount(chosenStudentType) < 1) throw new CharacterCardIncorrectParametersException();
                
                try {
                    moveStudents(chosenStudentType, 1, null, null, dstIslandIdx, t, StudentMovementSource.Self, StudentMovementDestination.ChosenIsland, true);
                } catch (StudentHostingCardIncorrectUpdateParametersException e) {
                    throw new CharacterCardIncorrectParametersException("StudentHostingCard ERROR: Incorrect Update Parameters - " + e.getMessage());
                }
                return 0;
            };
            case Circus -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (userInfo == null) throw new CharacterCardIncorrectParametersException("StudentHostingCard ERROR: CharacterCardParamSet is NULL");
                
                checkMaxUsesInTurn(character);
                Student studentFromCard = userInfo.getSrcStudentColor();
                Student studentFromEntrance = userInfo.getDstStudentColor();
                if (currentPlayer == null || studentFromCard == null || studentFromEntrance == null) throw new CharacterCardIncorrectParametersException();
                
                try {
                    moveStudents(studentFromCard, 1, null, currentPlayer, -1, t, StudentMovementSource.Self, StudentMovementDestination.PlayerEntrance, false);
                    moveStudents(studentFromEntrance, 1, currentPlayer, null, -1, t, StudentMovementSource.PlayerEntrance, StudentMovementDestination.Self, true);
                } catch (StudentHostingCardIncorrectUpdateParametersException e) {
                    throw new CharacterCardIncorrectParametersException("StudentHostingCard ERROR: Incorrect Update Parameters - " + e.getMessage());
                }
                return 0;
            };
            case Musician -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (userInfo == null) throw new CharacterCardIncorrectParametersException("StudentHostingCard ERROR: CharacterCardParamSet is NULL");
                
                checkMaxUsesInTurn(character);
                Student studentFromEntrance = userInfo.getSrcStudentColor();
                Student studentFromDiningRoom = userInfo.getDstStudentColor();
                if (currentPlayer == null || studentFromDiningRoom == null || studentFromEntrance == null) throw new CharacterCardIncorrectParametersException();
                
                try {
                    moveStudents(studentFromDiningRoom, 1, currentPlayer, currentPlayer, -1, t, StudentMovementSource.PlayerTable, StudentMovementDestination.PlayerEntrance, false);
                    moveStudents(studentFromEntrance, 1, currentPlayer, currentPlayer, -1, t, StudentMovementSource.PlayerEntrance, StudentMovementDestination.PlayerTable, true);
                } catch (StudentHostingCardIncorrectUpdateParametersException e) {
                    throw new CharacterCardIncorrectParametersException("StudentHostingCard ERROR: Incorrect Update Parameters - " + e.getMessage());
                }
                return 0;
            };
            case Queen -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (userInfo == null) throw new CharacterCardIncorrectParametersException("StudentHostingCard ERROR: CharacterCardParamSet is NULL");
                
                checkMaxUsesInTurn(character);
                Student chosenStudentType = userInfo.getSrcStudentColor();
                if (chosenStudentType == null || currentPlayer == null || hostedStudents.getCount(chosenStudentType) < 1) throw new CharacterCardIncorrectParametersException();
                
                try {
                    moveStudents(chosenStudentType, 1, null, currentPlayer, -1, t, StudentMovementSource.Self, StudentMovementDestination.PlayerTable, true);
                } catch (StudentHostingCardIncorrectUpdateParametersException e) {
                    throw new CharacterCardIncorrectParametersException("StudentHostingCard ERROR: Incorrect Update Parameters - " + e.getMessage());
                }
                return 0;
            };
            case Thief -> executor = (TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) -> {
                if (userInfo == null) throw new CharacterCardIncorrectParametersException("StudentHostingCard ERROR: CharacterCardParamSet is NULL");
                
                checkMaxUsesInTurn(character);
                Student chosenStudentType = userInfo.getSrcStudentColor();
                if (chosenStudentType == null || players == null) throw new CharacterCardIncorrectParametersException();
                
                for (Player player: players) {
                    int numberOfStudentsToRemove = Math.min(3, player.getCountAtTable(chosenStudentType));
                    try {
                        moveStudents(chosenStudentType, numberOfStudentsToRemove, player, null, -1, t, StudentMovementSource.PlayerTable, StudentMovementDestination.TableBag, false);
                    } catch (StudentHostingCardIncorrectUpdateParametersException e) {
                        throw new CharacterCardIncorrectParametersException("StudentHostingCard ERROR: Incorrect Update Parameters - " + e.getMessage());
                    }
                }
                return 0;
            };
            default -> //Other character card types
                    executor = null;
        }
    }
    
    @Override
    public void setupWithTable(TableManager t) {
        //Picks from the Bag a number of students specified by the Character enum
        int numberOfStudentsToPick = getCharacter().getHostedStudentsCount();
        try {
            hostedStudents = t.pickStudentsFromBag(numberOfStudentsToPick);
        } catch (CollectionUnderflowError e) {
            // Will never be executed, since the Bag always has the required number of Students at the beginning of a match
            e.printStackTrace();
        }
    }
    
    @Override
    public int useCard(TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException {
        markCardAsUsedInTurn();
        return executor.performAction(t, players, currentPlayer, userInfo);
    }
    
    @Override
    public StudentCollection getHostedStudents() {
        StudentCollection copyCollection = new StudentCollection();
        copyCollection.mergeWithCollection(hostedStudents);
        return copyCollection;
    }
    
    /**
     * Checks if the card has already been used a number of times higher that the maximum allowed
     * @param character The character to check the usage count against
     * @throws CharacterCardNoMoreUsesAvailableException if player is exceeding max uses of the card
     */
    private void checkMaxUsesInTurn(Character character) throws CharacterCardNoMoreUsesAvailableException {
        if (getTimesUsedInCurrentTurn() > character.getMaxNumberOfUsesInTurn()) throw new CharacterCardNoMoreUsesAvailableException();
    }
    
    /**
     * Performs the movement of a Student from a source object to a destination object, adapting to varios cases to cover for all character actions
     * @param s (type Student) The sStudent to move
     * @param numberOfStudentsToMove (type int) The number of student to move
     * @param pSource (type Player) The Player source of the movement
     * @param pDest (type Player) The destination player of movement
     * @param tableManager (type {@link it.polimi.ingsw.server.model.TableManager Table}) The table manager object, used to access table-related objects such as Islands
     * @param dstIslandIndex (type int) The index of the target island
     * @param movementSource  (type StudentMovementSource) The source of movement
     * @param movementDestination  (type StudentMovementSource) The destination of movement
     * @param autoUpdateToMatchMaxStudentCount (type boolean) Whether the method should automatically pick new Students from the bag after the movement
     * @throws StudentHostingCardIncorrectUpdateParametersException if the parameter to update card are incorrect
     */
    private void moveStudents(Student s, int numberOfStudentsToMove, Player pSource, Player pDest, int dstIslandIndex, TableManager tableManager, StudentMovementSource movementSource, StudentMovementDestination movementDestination, boolean autoUpdateToMatchMaxStudentCount) throws StudentHostingCardIncorrectUpdateParametersException {
        //region Check for Exceptions to abort the execution of the effect (i.e. transaction-like)
        if (tableManager == null) throw new StudentHostingCardIncorrectUpdateParametersException("StudentHostingCard MoveStudents(_:) ERROR: For full method safety tableManager must always be non-null");
        switch (movementSource) {
            case PlayerEntrance, PlayerTable -> {
                if (pSource == null) throw new StudentHostingCardIncorrectUpdateParametersException("StudentHostingCard MoveStudents(_:) ERROR: Expected pSource parameter to be non-null for a Movement-from-Player action");
            }
        }
        switch (movementDestination) {
            case PlayerEntrance, PlayerTable -> {
                if (pDest == null) throw new StudentHostingCardIncorrectUpdateParametersException("StudentHostingCard MoveStudents(_:) ERROR: Expected pDest parameter to be non-null for a Movement-to-Player action");
            }
            case ChosenIsland -> {
                if (dstIslandIndex == -1) throw new StudentHostingCardIncorrectUpdateParametersException("StudentHostingCard MoveStudents(_:) ERROR: Expected shDest parameter to be non-null for a Movement-to-Island action");
            }
        }
        //endregion

        try {
            // Add the student to the movement destination
            switch (movementDestination) {
                case PlayerEntrance -> {
                    StudentCollection sc = new StudentCollection();
                    sc.addStudents(s, numberOfStudentsToMove);
                    pDest.addAllStudentsToEntrance(sc);
                }
                case PlayerTable -> {
                    for (int repetition = 0; repetition < numberOfStudentsToMove; repetition++) {
                        pDest.placeStudentAtTableAndGetCoin(s, tableManager);
                    }
                }
                case Self -> hostedStudents.addStudents(s, numberOfStudentsToMove);
                case TableBag -> {
                    for (int repetition = 0; repetition < numberOfStudentsToMove; repetition++) {
                        tableManager.putStudentInBag(s);
                    }
                }
                case ChosenIsland -> tableManager.getIslandAtIndex(dstIslandIndex).placeStudents(s, numberOfStudentsToMove);
            }
            
            // Remove Student from the movement source
            switch (movementSource) {
                case PlayerEntrance -> {
                    for (int repetition = 0; repetition < numberOfStudentsToMove; repetition++) {
                        pSource.removeStudentFromEntrance(s);
                    }
                }
                case PlayerTable -> {
                    for (int repetition = 0; repetition < numberOfStudentsToMove; repetition++) {
                        pSource.removeStudentFromTable(s);
                    }
                }
                case Self -> hostedStudents.removeStudents(s, numberOfStudentsToMove);
            }

            // Invariant: The card always has a number of students equal to the one specified by the Character. Students must always be picked from the Bag in this case. We auto-update hostedStudents here in this case
            if (hostedStudents.getTotalCount() < getCharacter().getHostedStudentsCount() && autoUpdateToMatchMaxStudentCount) {
                try {
                    hostedStudents.mergeWithCollection(tableManager.pickStudentsFromBag(getCharacter().getHostedStudentsCount() - hostedStudents.getTotalCount()));
                } catch (CollectionUnderflowError e) {
                    //If the Bag is Empty we fall here and do not collect a new Student
                    e.printStackTrace();
                }
            }
        } catch (CollectionUnderflowError | TableFullException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void deactivate() {
    }
    
    @Override
    public CharacterCardBean beanify() {
        return new CharacterCardBean(getCharacter(), getPrice(), null, -1, -1, hostedStudents);
    }
    
    @Override
    protected void copyTo(CharacterCard dstCard) {
        super.copyTo(dstCard);
        ((StudentHostingCard)dstCard).hostedStudents = hostedStudents;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StudentHostingCard that = (StudentHostingCard) o;
        return hostedStudents.equals(that.hostedStudents);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + hostedStudents.hashCode();
        return result;
    }
    /**
     * This Class represent the {@code StudentMovementSource}
     */
    private enum StudentMovementSource {
        PlayerEntrance,
        PlayerTable,
        Self
    }
    
    /**
     * This Class represent the {@code StudentMovementDestination} options
     */
    private enum StudentMovementDestination {
        PlayerEntrance,
        PlayerTable,
        Self,
        TableBag,
        ChosenIsland
    }
}
