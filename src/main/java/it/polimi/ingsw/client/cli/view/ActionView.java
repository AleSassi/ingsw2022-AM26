package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.server.exceptions.client.CharacterCardActionInvalidException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
import it.polimi.ingsw.utils.cli.client.ClientActionCommand;

import java.util.Arrays;
/**
 * This Class represent the {@code TerminalView}
 * @author Alessandro Sassi
 */
public class ActionView extends TerminalView {
	
	private TableView tableView;
	private PlayerStateView playerStateView;
	private boolean isPlayerActive = false;
	private MatchPhase phase;
	private final MatchVariant variant;
	
	private boolean shouldEndMatch = false;
	/**constructor
	 * set variant of match
	 * @param variant (type {@link it.polimi.ingsw.server.model.match.MatchVariant}) type of match
	 */
	public ActionView(MatchVariant variant) {
		this.variant = variant;
	}

	/**
	 * create a thread and add observers(one for each type of {@link it.polimi.ingsw.notifications.Notification Notification} we need) on  {@link it.polimi.ingsw.notifications.NotificationCenter Center} type of match
	 * for every (@Code Nofication) that arrive call a differrent method of class according to the name of (@Code Nofication)
	 */
	@Override
	public void run() {
		NotificationCenter.shared().addObserver(this, this::didReceiveActionResponse, NotificationName.ClientDidReceivePlayerActionResponse, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveActivePlayer, NotificationName.ClientDidReceiveActivePlayerMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveMatchPhase, NotificationName.ClientDidReceiveMatchStateMessage, null);
		NotificationCenter.shared().addObserver(this, (notification) -> {
			MatchTerminationMessage message = (MatchTerminationMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
			System.out.println(StringFormatter.formatWithColor("The server ended the match. Reason: \"" + message.getTerminationReason() + "\"", ANSIColors.Red));
			endMatch();
		}, NotificationName.ClientDidReceiveMatchTerminationMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveVictory, NotificationName.ClientDidReceiveVictoryMessage, null);
		
		tableView = new TableView();
		tableView.run();
		playerStateView = new PlayerStateView(this.variant);
		playerStateView.run();
		parseInputCommands();
	}
	/**
	 This method parse the action from the command line, check parameter and type of action, if are correct send a{@link it.polimi.ingsw.server.controller.network.messages.PlayerActionMessage Message} that contain the infomation of action
	 */
	private void parseInputCommands() {
		while (!shouldEndMatch) {
			String command = getTerminalScanner().nextLine();
			if (!shouldEndMatch) {
				String[] args = command.split("\\s+");
				if (isPlayerActive && args.length > 0) {
					ClientActionCommand actionCommand = null;
					for (ClientActionCommand clientActionCommand: ClientActionCommand.values()) {
						if (clientActionCommand.getRawValue().equals(args[0])) {
							actionCommand = clientActionCommand;
							break;
						}
					}
					if (actionCommand == null || !actionCommand.isValidForPhaseAndVariant(phase, variant)) {
						System.out.println(StringFormatter.formatWithColor("ERROR: Incorrect command. These are the commands you can use:", ANSIColors.Red));
						printAvailableCommands();
					} else {
						// Decode the command
						boolean successDecoding = false;
						PlayerActionMessage actionMessage = null;
						switch (actionCommand) {
							case ChooseAssistant -> {
								//Picks and plays an Assistant card. Must be followed by the index of the card to play
								if (args.length > 1) {
									try {
										int index = Integer.parseInt(args[1]);
										if (index >= 0 && index < playerStateView.getNumberOfCards()) {
											successDecoding = true;
											actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPlayAssistantCard, index, null, false, -1, -1, -1, -1, null);
										}
									} catch (NumberFormatException ignored) {
									}
								}
							}
							case MoveStudentToIsland -> {
								//Picks a Student from your Entrance space and moves it to an Island. Must be followed by (in order) the COLOR of the Student to move and the INDEX of the target island
								if (args.length > 2) {
									String studentColor = args[1];
									Student chosenStudent = null;
									for (Student student: Student.values()) {
										if (student.getColor() != null && student.getColor().equals(studentColor)) {
											chosenStudent = student;
											break;
										}
									}
									if (chosenStudent != null) {
										try {
											int index = Integer.parseInt(args[2]);
											if (index >= 0 && index < tableView.getNumberOfIslands()) {
												successDecoding = true;
												actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidMoveStudent, -1, chosenStudent, true, index, -1, -1, -1, null);
											}
										} catch (NumberFormatException ignored) {
										}
									}
								}
							}
							case MoveStudentToRoom -> {
								//Picks a Student from your Entrance space and moves it to the Dining Room. Must be followed by the COLOR of the Student to move
								if (args.length > 1) {
									String studentColor = args[1];
									Student chosenStudent = null;
									for (Student student: Student.values()) {
										if (student.getColor() != null && student.getColor().equals(studentColor)) {
											chosenStudent = student;
											break;
										}
									}
									if (chosenStudent != null) {
										successDecoding = true;
										actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidMoveStudent, -1, chosenStudent, false, -1, -1, -1, -1, null);
									}
								}
							}
							case MoveMotherNature -> {
								//Moves Mother Nature. Followed by the number of steps
								if (args.length > 1) {
									try {
										int mnSteps = Integer.parseInt(args[1]);
										if (mnSteps > 0 && mnSteps <= playerStateView.getMaxMNSteps()) {
											successDecoding = true;
											actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidMoveMNBySteps, -1, null, false, -1, mnSteps, -1, -1, null);
										}
									} catch (NumberFormatException ignored) {
									}
								}
							}
							case PickCloud -> {
								//Picks all Students from a Cloud card. Followed by the Cloud card index
								if (args.length > 1) {
									try {
										int cloudIdx = Integer.parseInt(args[1]);
										if (cloudIdx >= 0 && cloudIdx < tableView.getNumberOfClouds()) {
											successDecoding = true;
											actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidChooseCloudIsland, -1, null, false, -1, -1, cloudIdx, -1, null);
										}
									} catch (NumberFormatException ignored) {
									}
								}
							}
							case PurchaseCharacterCard -> {
								//Purchases a Character card. Followed by the Index of the Character card to play
								if (args.length > 1) {
									try {
										int cardIdx = Integer.parseInt(args[1]);
										if (cardIdx >= 0 && cardIdx < tableView.getNumberOfCards()) {
											successDecoding = true;
											actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPurchaseCharacterCard, -1, null, false, -1, -1, -1, cardIdx, null);
										}
									} catch (NumberFormatException ignored) {
									}
								}
							}
							case PlayCharacterCard -> {
								//Plays the purchased character card
								if (playerStateView.getPurchasedCharacterCard() != null) {
									// Now we must get the correct input for each card type
									CharacterCardInputView inputView = new CharacterCardInputView(tableView, playerStateView);
									try {
										CharacterCardNetworkParamSet paramSet = inputView.run();
										if (paramSet == null) {
											printCaret();
											continue;
										} else {
											successDecoding = true;
											actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPlayCharacterCard, -1, null, false, -1, -1, -1, playerStateView.getPurchasedCharacterCard(), paramSet);
										}
									} catch (CharacterCardActionInvalidException ignored) {
									}
								}
							}
							default -> System.out.println(StringFormatter.formatWithColor("ERROR: Unrecognized command", ANSIColors.Red));
						}
						if (successDecoding && actionMessage != null) {
							GameClient.shared().sendMessage(actionMessage);
							System.out.println(StringFormatter.formatWithColor("Command sent. Awaiting response...", ANSIColors.Pink));
						} else {
							System.out.println(StringFormatter.formatWithColor("Invalid command or command parameters", ANSIColors.Red));
							printAvailableCommands();
						}
					}
				}
			}
		}
	}
	/**
	 * method called whan arrive a {@link it.polimi.ingsw.notifications.Notification Notification}with name ActionResponse
	 *the method print  the outcome of action recived
	 * @param notification (@code Notification) that contain the information of event
	 */
	private void didReceiveActionResponse(Notification notification) {
		PlayerActionResponse response = (PlayerActionResponse) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		if (response.isActionSuccess()) {
			if (response.getActionType() == PlayerActionMessage.ActionType.DidPlayCharacterCard && !response.getDescriptiveErrorMessage().equals("")) {
				System.out.println(StringFormatter.formatWithColor("Action of type " + response.getActionType().toString() + " succeeded. Returned: " + response.getDescriptiveErrorMessage(), ANSIColors.Green));
				printCaret();
			} else {
				System.out.println(StringFormatter.formatWithColor("Action of type " + response.getActionType().toString() + " succeeded. Printing the table and player data...", ANSIColors.Green));
			}
		} else {
			System.out.println(StringFormatter.formatWithColor("The command of type " + response.getActionType().toString() + " did not succeed. Reason: " + response.getDescriptiveErrorMessage(), ANSIColors.Red));
			printCaret();
		}
	}
	/**
	 * method called when arrive a {@link it.polimi.ingsw.notifications.Notification Notification}with name ActivePlayer
	 *the method print  the name of active player
	 * @param notification (@code Notification) that contain the information of event
	 */
	private void didReceiveActivePlayer(Notification notification) {
		ActivePlayerMessage message = (ActivePlayerMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		if (!message.getActiveNickname().equals(Client.getNickname())) {
			System.out.println(StringFormatter.formatWithColor("Player \"" + message.getActiveNickname() + "\" is performing their turn. Please wait for your turn.", ANSIColors.Yellow));
		}
		isPlayerActive = message.getActiveNickname().equals(Client.getNickname());
	}
	/**
	 * method called whan arrive a {@link it.polimi.ingsw.notifications.Notification Notification}with name MatchPhase
	 *the method print tha name of phase we are playing
	 * @param notification (@code Notification) that contain the information of event
	 */
	private void didReceiveMatchPhase(Notification notification) {
		this.phase = ((MatchStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue())).getCurrentMatchPhase();
		printAvailableCommands();
	}
	/**
	 * method called whan arrive a {@link it.polimi.ingsw.notifications.Notification Notification}with name Victory
	 *the method print tha name of winner
	 * @param notification (@code Notification) that contain the information of event
	 */
	private void didReceiveVictory(Notification notification) {
		VictoryMessage victoryMessage = (VictoryMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		if (!Arrays.stream(victoryMessage.getWinners()).filter((winner) -> winner.equals(Client.getNickname())).toList().isEmpty()) {
			System.out.println(StringFormatter.formatWithColor("You won!", ANSIColors.Green));
		} else {
			System.out.println(StringFormatter.formatWithColor("You lose", ANSIColors.Red));
		}
		endMatch();
	}

	/**
	 * method called when arrive a {@link it.polimi.ingsw.notifications.Notification Notification}with name NetworkTimeoutNotification
	 *the method call the endgame method
	 * @param notification (@code Notification) that contain the information of event
	 */
	@Override
	protected void didReceiveNetworkTimeoutNotification(Notification notification) {
		System.out.println(StringFormatter.formatWithColor("The Client encountered an error. Reason: Timeout. The network connection with the Server might have been interrupted, or the Server might be too busy to respond", ANSIColors.Red));
		endMatch();
	}
	/**
	 *the method close the client
	 */
	private void endMatch() {
		GameClient.shared().terminate();
		shouldEndMatch = true;
		System.exit(0);
	}
	/**
	 *the method print on command line  the avaibles command that player can perform
	 */
	private void printAvailableCommands() {
		if (isPlayerActive) {
			System.out.println(StringFormatter.formatWithColor("It's your turn! These are the commands you can use to play your turn:", ANSIColors.Green));
			for (ClientActionCommand actionCommand: ClientActionCommand.values()) {
				if (actionCommand.isValidForPhaseAndVariant(phase, variant)) {
					boolean canPurchaseCard = playerStateView.getPurchasedCharacterCard() == null;
					if (actionCommand == ClientActionCommand.PurchaseCharacterCard && canPurchaseCard) {
						actionCommand.printHelp(false);
					} else if (actionCommand == ClientActionCommand.PlayCharacterCard && !canPurchaseCard) {
						actionCommand.printHelp(false);
					} else if (actionCommand != ClientActionCommand.PlayCharacterCard && actionCommand != ClientActionCommand.PurchaseCharacterCard) {
						actionCommand.printHelp(actionCommand == ClientActionCommand.MoveMotherNature);
						if (actionCommand == ClientActionCommand.MoveMotherNature) {
							System.out.println(StringFormatter.formatWithColor(" (between 1 and " + playerStateView.getMaxMNSteps() + ")", ANSIColors.Green));
						}
					}
				}
			}
			printCaret();
		}
	}
	/**
	 *the method print on command line the Caret
	 */
	private void printCaret() {
		System.out.print(Client.getNickname() + " > ");
	}
}
