package it.polimi.ingsw.server.controller;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.server.controller.network.GameServer;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.model.match.GameLobby;
import it.polimi.ingsw.server.model.match.GameLobbyState;
import it.polimi.ingsw.server.model.match.MatchManager;
import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.exceptions.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GameController {
	
	private final GameServer server;
	private GameLobby lobby;
	private MatchManager activeMatchManager;
	
	public GameController(@NotNull GameServer server) {
		this.server = server;
		
		NotificationCenter.shared().addObserver(this::didReceiveLoginMessage, NotificationName.ServerDidReceiveLoginMessage, this);
		NotificationCenter.shared().addObserver(this::didReceivePlayerActionMessage, NotificationName.ServerDidReceivePlayerActionMessage, this);
		NotificationCenter.shared().addObserver(this::didReceiveTerminationMessage, NotificationName.ServerDidTerminateMatch, this);
	}
	
	public boolean containsPlayerWithNickname(String nickname) {
		if (lobby == null) return false;
		return Arrays.stream(lobby.getPlayerNicknames()).toList().contains(nickname);
	}
	
	private void didReceiveLoginMessage(Notification notification) {
		// Before continuing, check that the notification contains the desired message (LoginMessage)
		if (notification.getUserInfo() != null &&
				notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) != null &&
				notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof LoginMessage loginMessage) {
			// Create the Lobby if needed
			if (lobby == null) {
				lobby = new GameLobby(loginMessage.getDesiredNumberOfPlayers(), loginMessage.getMatchVariant());
			}
			// Add the Player to the lobby
			LoginResponse responseMessage;
			boolean success = false;
			try {
				lobby.addPlayer(loginMessage.getNickname(), loginMessage.getChosenWizard());
				responseMessage = new LoginResponse(loginMessage.getNickname(), true, lobby.getMaxPlayerCount() - lobby.getNumberOfPlayers(), null);
				success = true;
			} catch (NicknameNotUniqueException e) {
				responseMessage = new LoginResponse(loginMessage.getNickname(), false, Integer.MAX_VALUE, "The nickname you entered is not unique. Please choose another nickname");
			} catch (LobbyFullException e) {
				//TODO: In case of multiple matches, the Server should first scan the controllers to find an available one before sending the Notification
				responseMessage = new LoginResponse(loginMessage.getNickname(), false, Integer.MAX_VALUE, "The lobby you entered in is already full");
			} catch (WizardAlreadyChosenException e) {
				responseMessage = new LoginResponse(loginMessage.getNickname(), false, Integer.MAX_VALUE, "The Wizard you have chosen has already been taken by another player in the same lobby");
			}
			//TODO: We assume that Login messages set the VirtualClient's nickname BEFORE calling the Server methods and invoking any notification
			server.sendMessage(responseMessage, loginMessage.getNickname());
			
			if (success) {
				for (String nickname: lobby.getPlayerNicknames()) {
					if (!nickname.equals(loginMessage.getNickname())) {
						server.sendMessage(responseMessage, nickname);
					}
				}
			}
			if (success && lobby.getCurrentState() == GameLobbyState.Full) {
				startMatch();
			}
		}
	}
	
	private void didReceivePlayerActionMessage(Notification notification) {
		// Before continuing, check that the notification contains the desired message (LoginMessage)
		if (notification.getUserInfo() != null &&
				notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) != null &&
				notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof PlayerActionMessage actionMessage) {
			// Check that the Action is valid for the current Phase
			if (lobby == null || activeMatchManager == null) {
				server.sendMessage(new PlayerActionResponse(actionMessage.getNickname(), actionMessage.getPlayerActionType(), false, "You are trying to run an Action without logging in first, or without waiting for the Match to start"), actionMessage.getNickname());
			} else {
				// The message comes in order (after a Login + Match Started). Check if the move is valid
				MatchPhase currentPhase = activeMatchManager.getMatchPhase();
				if (!actionMessage.getNickname().equals(activeMatchManager.getCurrentPlayer().getNickname())) {
					server.sendMessage(new PlayerActionResponse(actionMessage.getNickname(), actionMessage.getPlayerActionType(), false, "Invalid move: you are not the current Player"), actionMessage.getNickname());
				} else if (!actionMessage.getPlayerActionType().isValidForMatchPhase(currentPhase)) {
					server.sendMessage(new PlayerActionResponse(actionMessage.getNickname(), actionMessage.getPlayerActionType(), false, "Invalid move: the action is not valid for the current Match Phase"), actionMessage.getNickname());
				} else {
					// Perform the action
					String errorMessage = null;
					String additionalMessage = "";
					if (actionMessage.getPlayerActionType() == PlayerActionMessage.ActionType.DidPurchaseCharacterCard) {
						boolean success = activeMatchManager.purchaseCharacterCards(actionMessage.getChosenCharacterIndex());
						if (!success) {
							errorMessage = "Not enough Coins to purchase the Card";
						}
					} else if (actionMessage.getPlayerActionType() == PlayerActionMessage.ActionType.DidPlayCharacterCard) {
						try {
							additionalMessage += activeMatchManager.useCharacterCard(actionMessage.getCharacterCardParameters());
						} catch (CharacterCardIncorrectParametersException e) {
							errorMessage = "Invalid move: the Character Card parameters are not valid";
						} catch (CharacterCardNoMoreUsesAvailableException e) {
							errorMessage = "Invalid move: the Character Card cannot be used anymore (reached max use limit)";
						} catch (CharacterCardNotPurchasedException e) {
							errorMessage = "Invalid move: you have not purchased the Character card";
						}
					} else {
						// A normal action
						try {
							activeMatchManager.runAction(actionMessage.getAssistantIndex(), actionMessage.getMovedStudent(), actionMessage.getDestinationIslandIndex(), actionMessage.isMovesToIsland(), actionMessage.getChosenMNBaseSteps(), actionMessage.getChosenCloudTileIndex());
						} catch (StudentMovementInvalidException e) {
							errorMessage = "Invalid move: the movement of the selected Student is not valid";
						} catch (AssistantCardNotPlayableException e) {
							errorMessage = "Invalid move: the Assistant Card you chose cannot be played, because other opponents have already played it before you";
						} catch (CloudPickInvalidException e) {
							errorMessage = "Invalid move: the Cloud you chose is empty. This is not allowed, unless the Bag is also empty";
						}
					}
					if (errorMessage == null) {
						// If nothing failed we send a "Success" message
						server.sendMessage(new PlayerActionResponse(actionMessage.getNickname(), actionMessage.getPlayerActionType(), true, additionalMessage), actionMessage.getNickname());
						// and then the updated states
						sendMatchDataToClients();
					} else {
						// Send the error message
						server.sendMessage(new PlayerActionResponse(actionMessage.getNickname(), actionMessage.getPlayerActionType(), false, errorMessage), actionMessage.getNickname());
					}
				}
			}
		}
	}
	
	private void didReceiveTerminationMessage(Notification notification) {
		// Terminate the game due to disconnections
		//TODO: Find a way to archive the Match here for future reconnections
		lobby = null;
		activeMatchManager = null;
	}
	
	private void startMatch() {
		try {
			activeMatchManager = lobby.startGame();
			sendMatchDataToClients();
		} catch (IncorrectConstructorParametersException | InvalidPlayerCountException e) {
			// Should never happen, but in case it does we send an error to all clients
			MatchTerminationMessage terminationMessage = new MatchTerminationMessage("The match has been terminated because the Players were of invalid number, or because the chosen match parameters were incorrect", true);
			for (String nickname: lobby.getPlayerNicknames()) {
				server.sendMessage(terminationMessage, nickname);
			}
			// Terminate the match by destroying the lobby
			lobby = null;
			activeMatchManager = null;
		}
	}
	
	private void sendMatchDataToClients() {
		for (String nickname: lobby.getPlayerNicknames()) {
			// First we send the Table description
			server.sendMessage(activeMatchManager.generateTableStateMessage(), nickname);
			// Then we get the Player description
			server.sendMessage(activeMatchManager.generatePlayerStateMessage(nickname), nickname);
			// Then we send the active Player nickname
			server.sendMessage(new ActivePlayerMessage(activeMatchManager.getCurrentPlayer()), nickname);
			// Finally, send the current Match phase
			server.sendMessage(new MatchStateMessage(activeMatchManager.getMatchPhase()), nickname);
		}
	}
	
}
