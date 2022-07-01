package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.TableManager;

import java.util.List;

@FunctionalInterface
/**
 * This Interface represent the {@code CharacterCardExecutor}
 * @author Alessandro Sassi
 */
public interface CharacterCardExecutor {
    /**
     abstract method
     */
    int performAction(TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException;

}
