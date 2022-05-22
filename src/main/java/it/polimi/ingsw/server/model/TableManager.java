package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.*;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.model.characters.CharacterCardExtractor;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.*;

import java.util.*;

public class TableManager {

    private List<Professor> availableProfessors;
    private List<Island> islands;
    private StudentHost studentBag;
    private List<Cloud> managedClouds;
    private List<CharacterCard> playableCharacterCards;
    private int coinReserve = 20;

    private int islandIndexWithJustRemovedStopCard = -1;

    public TableManager(int cloudTileCount, boolean allowsCharacterCards) {
        //ASSUMPTION: CloudTileCount === PlayerCount
        coinReserve = 20 - cloudTileCount;
        playableCharacterCards = new ArrayList<>();
        if (allowsCharacterCards) {
            CharacterCardExtractor cardExtractor = new CharacterCardExtractor();
            try {
                for (int repetition = 0; repetition < 3; repetition++) {
                    playableCharacterCards.add(cardExtractor.pickRandom());
                }
            } catch (UnavailableCardException e) {
                e.printStackTrace();
                //Create an empty deck
                playableCharacterCards.removeIf((card) -> true);
            }
        }
        availableProfessors = new ArrayList<>(Arrays.asList(Professor.values()));
        StudentHost initialBag = new StudentHost();
        for (Student s: Student.values()) {
            initialBag.placeStudents(s, 2);
        }
        islands = new ArrayList<>();
        //Randomize an index between 0 and 11 to get the island when Mother nature is
        int numberOfIslands = 12;
        int randomIslandIdx = new Random().nextInt(0, numberOfIslands);
        try {
            for (int islandIdx = 0; islandIdx < numberOfIslands; islandIdx++) {
                Island newIsland = new Island();
                if (islandIdx == randomIslandIdx) {
                    newIsland.setMotherNaturePresent(true);
                } else if (islandIdx != circularWrap(randomIslandIdx + (numberOfIslands / 2), numberOfIslands)) {
                    newIsland.placeStudents(initialBag.removeRandom(), 1);
                }
                islands.add(newIsland);
            }
        } catch (CollectionUnderflowError e) {
            e.printStackTrace();
        }
        // Initialize the Bag
        studentBag = new StudentHost();
        for (Student s: Student.values()) {
            studentBag.placeStudents(s, 24);
        }
        //Initialize the Cloud tiles
        managedClouds = new ArrayList<>();
        for (int cloudIdx = 0; cloudIdx < cloudTileCount; cloudIdx++) {
            managedClouds.add(new Cloud());
        }
        if (allowsCharacterCards) {
            //Set up the Character cards
            for (CharacterCard card: playableCharacterCards) {
                card.setupWithTable(this);
            }
        }
    }

    public int getNumberOfClouds() {
        return managedClouds.size();
    }

    public int getNumberOfIslands() {
        return islands.size();
    }
    
    public int getCurrentIslandIndex() {
        for (int islandIdx = 0; islandIdx < islands.size(); islandIdx++) {
            if (islands.get(islandIdx).isMotherNaturePresent()) {
                return islandIdx;
            }
        }
        //Will never be executed
        return 0;
    }

    public Island getCurrentIsland() {
        return islands.get(getCurrentIslandIndex());
    }

    public Island getIslandAtIndex(int islandIndex) {
        if (islandIndex < 0 || islandIndex > islands.size()) return null;

        return islands.get(islandIndex);
    }
    
    public boolean isProfessorAvailable(Professor professor) {
        return availableProfessors.contains(professor);
    }
    
    public boolean isBagEmpty() {
        return studentBag.isEmpty();
    }
    
    public void removeProfessor(Professor professor) {
        availableProfessors.remove(professor);
    }
    
    public int getCoinFromReserve() {
        int result = coinReserve > 0 ? 1 : 0;
        coinReserve -= 1;
        return result;
    }

    public StudentCollection pickStudentsFromBag(int count) throws CollectionUnderflowError {
        StudentCollection result = new StudentCollection();
        for (int studentIdx = 0; studentIdx < count; studentIdx++) {
            result.addStudents(studentBag.removeRandom(), 1);
        }
        return result;
    }

    public void putStudentInBag(Student s) {
        studentBag.placeStudents(s, 1);
    }

    public StudentCollection pickStudentsFromCloud(int cloudIdx) throws CollectionUnderflowError {
        StudentCollection pickedCollection = managedClouds.get(cloudIdx).extractAllStudentsAndRemove();
        if (!studentBag.isEmpty() && pickedCollection.getTotalCount() == 0) {
            throw new CollectionUnderflowError();
        }
        return pickedCollection;
    }

    public void placeStudentOnCloud(Student s, int cloudIdx, int count) throws IndexOutOfBoundsException {
        if (cloudIdx < 0 || cloudIdx > managedClouds.size()) throw new IndexOutOfBoundsException();

        managedClouds.get(cloudIdx).placeStudents(s, count);
    }

    public void placeStudentOnIsland(Student s, int islandIdx) throws IndexOutOfBoundsException {
        if (islandIdx < 0 || islandIdx > islands.size()) throw new IndexOutOfBoundsException();

        islands.get(islandIdx).placeStudents(s, 1);
    }

    public void moveMotherNature(int steps) {
        int currentIslandIdx = getCurrentIslandIndex();
        int newIslandIdx = circularWrap(currentIslandIdx + steps, islands.size());
        islands.get(currentIslandIdx).setMotherNaturePresent(false);
        islands.get(newIslandIdx).setMotherNaturePresent(true);
        if (islands.get(newIslandIdx).itHasStopCard()) {
            islandIndexWithJustRemovedStopCard = newIslandIdx;
            // We need to find the StopCardActivatorCard to give the card back
            for (CharacterCard characterCard: playableCharacterCards) {
                try {
                    characterCard.useCard(this, null, null, new CharacterCardParamSet(null, null, null, null, false, -1, newIslandIdx, newIslandIdx, CharacterCardParamSet.StopCardMovementMode.ToCard));
                } catch (Exception e) {
                    //We do nothing since it means that the card is not a StopCardActivatorCard
                }
            }
        } else {
            islandIndexWithJustRemovedStopCard = -1;
        }
    }
    public Cloud getCloud(int idx) {
        return managedClouds.get(idx);
    }

    public CharacterCard getCardAtIndex(int cardIndex) throws IndexOutOfBoundsException {
        return playableCharacterCards.get(cardIndex);
    }

    public int getInfluenceOnCurrentIsland(Player p) throws IslandSkippedInfluenceForStopCardException {
        if (getCurrentIslandIndex() == islandIndexWithJustRemovedStopCard) throw new IslandSkippedInfluenceForStopCardException();

        int computedInfluence = getCurrentIsland().getInfluence(p);
        CharacterCard playedCharacterCard = p.getActiveCharacterCard();
        //Apply the Modifier
        if (playedCharacterCard != null && playedCharacterCard.getCharacter().getChangesInfluence()) {
            try {
                computedInfluence += playedCharacterCard.useCard(this, null, p, new CharacterCardParamSet(null, null, null, null, false, -1, getCurrentIslandIndex(), getCurrentIslandIndex(), null));
            } catch (Exception e) {
                // If we have an exception it means that the card was not an Influence modifier card.
                e.printStackTrace();
            }
        }
        return computedInfluence;
    }

    /**
     * Changes control of the current island from the player that previously controlled it to the new Player
     *
     * This method performs both the control change (tower placement on the Island) and Tower reassignment to the Player that owns that Tower color. This means that if the match is a Team match, you will need to pass the Player that owns the Towers (and not the one that is effectively controlling the Island).
     * @param from The Player that previously owned the Island. You can pass <code>null</code>, but only if the Island did not have a Tower before
     * @param to The Player that will gain control of the Island (or in a Team match, the Player of the team that owns the Towers). This parameter cannot be <code>null</code>.
     * @throws IslandSkippedControlAssignmentForStopCardException If the Island had a Stop Card on it before the start of this turn, thus the control change will not occur.
     * @throws IllegalArgumentException If the parameters are incorrect (e.g.: if <code>from</code> is <code>null</code> but the Island already has an Island, or if <code>from</code> is not the Player that previously controlled the Island).
     x*/
    public void changeControlOfCurrentIsland(Player from, Player to) throws IslandSkippedControlAssignmentForStopCardException, IllegalArgumentException {
        if (getCurrentIslandIndex() == islandIndexWithJustRemovedStopCard) throw new IslandSkippedControlAssignmentForStopCardException();
        Island currentIsland = getCurrentIsland();
        if (!((from == null && currentIsland.getTowerCount() == 0) || (from != null && currentIsland.getTowerCount() > 0 && from.getTowerType() == currentIsland.getActiveTowerType()))) throw new IllegalArgumentException();
        if (to == null) throw new IllegalArgumentException();

        currentIsland.setTower(to.getTowerType());
        try {
            for (int towerIdx = 0; towerIdx < currentIsland.getTowerCount(); towerIdx++) {
                to.pickAndRemoveTower();
                if (from != null) {
                    from.gainTower();
                }
            }
            unifyCurrentIslandWithAdjacentIfPossible();
        } catch (InsufficientTowersException e) {
            // Notify match end by the Player who would have owned the Island
            to.notifyVictory();
        } catch (TooManyTowersException e) {
            e.printStackTrace();
        }
    }

    private void unifyCurrentIslandWithAdjacentIfPossible() {
        // The adjacent islands are the ones at index +1 and -1
        int currentIslandIndex = getCurrentIslandIndex();
        int nextIslandIndex = circularWrap(currentIslandIndex + 1, islands.size());
        int prevIslandIndex = circularWrap(currentIslandIndex - 1, islands.size());
        Island adjacentLeft = islands.get(nextIslandIndex);
        Island adjacentRight = islands.get(prevIslandIndex);
        Island currentIsland = getCurrentIsland();

        if (adjacentLeft.isUnifiableWith(currentIsland)) {
            //Merge!
            currentIsland.acquireIsland(adjacentLeft);
            islands.remove(adjacentLeft);
        }
        if (adjacentRight.isUnifiableWith(currentIsland)) {
            //Merge!
            currentIsland.acquireIsland(adjacentRight);
            islands.remove(adjacentRight);
        }

        if (islands.size() == 3) {
            notifyMatchEnd();
        }
    }

    private void notifyMatchEnd() {
        //This will notify the MatchManager that somebody has won the Match. The Match manager will then get the winning players based on which Players have the Tower indicated by the notification, and resolve parity if needed
        //Send the notification
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(NotificationKeys.WinnerTowerType.getRawValue(), getWinningTowers());
        NotificationCenter.shared().post(NotificationName.PlayerVictory, this, userInfo);
    }
    
    public List<Tower> getWinningTowers() {
        int[] towerCounts = new int[Tower.values().length];
        for (Island island: islands) {
            if (island.getActiveTowerType() != null) {
                towerCounts[island.getActiveTowerType().index()] += island.getTowerCount();
            }
        }
        //Find the max value
        int currentMax = 0;
        for (int towerCount: towerCounts) {
            currentMax = Math.max(currentMax, towerCount);
        }
        //Find the list of Towers (in case of parity) to send to the MatchManager - parity will be resolved by the Manager (which knows the Players in the Match)
        List<Tower> candidateWinners = new ArrayList<>();
        for (int index = 0; index < towerCounts.length; index++) {
            if (towerCounts[index] == currentMax) {
                candidateWinners.add(Tower.values()[index]);
            }
        }
        return candidateWinners;
    }

    public boolean checkAndNotifyMatchEnd() {
        if (studentBag.isEmpty()) {
            notifyMatchEnd();
            return true;
        }
        return false;
    }
    
    public TableStateMessage getStateMessage() {
        return new TableStateMessage(availableProfessors, islands, studentBag, managedClouds, playableCharacterCards.stream().map(CharacterCard::beanify).toList());
    }

    /**
     * Adjusts a range as circular and 'wraps around' the value to become in bounds
     * @param left: The current number, or the 'numerator'
     * @param right: The upper bound maximum range number, or the 'denominator'
     * @return The adjusted value according to the denominator
     */
    public static int circularWrap(int left, int right) {
        return ((left % right) + right) % right;
    }

    public List<CharacterCard> getPlayableCharacterCards() {
        return playableCharacterCards;
    }
    
    public void copyTo(TableManager copyDst) {
        copyDst.playableCharacterCards = playableCharacterCards;
        copyDst.managedClouds = managedClouds;
        copyDst.islands = islands;
        copyDst.studentBag = studentBag;
        copyDst.islandIndexWithJustRemovedStopCard = islandIndexWithJustRemovedStopCard;
        copyDst.availableProfessors = availableProfessors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableManager that = (TableManager) o;

        if (islandIndexWithJustRemovedStopCard != that.islandIndexWithJustRemovedStopCard) return false;
        if (!availableProfessors.equals(that.availableProfessors)) return false;
        if (!islands.equals(that.islands)) return false;
        if (!studentBag.equals(that.studentBag)) return false;
        if (!managedClouds.equals(that.managedClouds)) return false;
        return playableCharacterCards.equals(that.playableCharacterCards);
    }

    @Override
    public int hashCode() {
        int result = availableProfessors.hashCode();
        result = 31 * result + islands.hashCode();
        result = 31 * result + studentBag.hashCode();
        result = 31 * result + managedClouds.hashCode();
        result = 31 * result + playableCharacterCards.hashCode();
        result = 31 * result + islandIndexWithJustRemovedStopCard;
        return result;
    }
}
