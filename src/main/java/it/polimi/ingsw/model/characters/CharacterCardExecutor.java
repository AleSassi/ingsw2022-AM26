package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TableManager;

import java.util.List;

@FunctionalInterface
public interface CharacterCardExecutor {

    int performAction(TableManager t, List<Player> players, Player currentPlayer, CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException;

}
