package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.TableManager;

import java.util.List;

/**
 * This Interface represents the {@code CharacterCardExecutor} closure that is used by each card to define its own behavior
 * @author Alessandro Sassi
 */
@FunctionalInterface
public interface CharacterCardExecutor {
    
    /**
     * The handler that is used by each card to define its own behavior
     * @param t The table manager used to execute the action
     * @param players The list of Players in the same lobby
     * @param currentPlayer The current Player who used the card
     * @param userInfo The card parameters
     * @return The numeric result of the card execution
     * @throws CharacterCardIncorrectParametersException If the character card parameters are incorrect
     * @throws CharacterCardNoMoreUsesAvailableException If the character card does not have any more uses available
     */
    int performAction(TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException;

}
