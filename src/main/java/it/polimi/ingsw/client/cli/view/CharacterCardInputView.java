package it.polimi.ingsw.client.cli.view;

import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet;
import it.polimi.ingsw.server.controller.network.messages.NetworkMessage;
import it.polimi.ingsw.server.controller.network.messages.PlayerActionMessage;
import it.polimi.ingsw.server.exceptions.client.CharacterCardActionInvalidException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;

import java.io.InputStreamReader;
import java.util.Scanner;
/**
 * This Class represent the {@code CharacterCardInputView}
 * @author Alessandro Sassi
 */
public class CharacterCardInputView {
	/**
	 * initialize {@code CharacterCardInputView}
	 */
	private final Scanner terminalScanner;
	private final TableView tableView;
	private final PlayerStateView playerStateView;
	/**
	 * constructor, set the parameter according to
	 * @param tableView (type {@link it.polimi.ingsw.client.cli.view.TableView}) table view
	 * @param playerStateView (type {@link it.polimi.ingsw.client.cli.view.PlayerStateView}) Playerstateview
	 */
	public CharacterCardInputView(TableView tableView, PlayerStateView playerStateView) {
		this.tableView = tableView;
		this.playerStateView = playerStateView;
		this.terminalScanner = new Scanner((new InputStreamReader(System.in)));
	}
	/**
	 getter
	 @return (type terminalScanner)
	 */
	private Scanner getTerminalScanner() {
		return terminalScanner;
	}
	/**
	 this method create a thread, when the player play a  {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard} ask for the parameter(according to the played card) to apply the effect of the card
	 received the parameter from player prepare a{@link it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet Message}
	 @throws CharacterCardActionInvalidException if there are error in the parameter
	 */
	public CharacterCardNetworkParamSet run() throws CharacterCardActionInvalidException {
		Character character = tableView.getCharacterAtIndex(playerStateView.getPurchasedCharacterCard());
		boolean isCommandValid = false;
		CharacterCardNetworkParamSet paramSet = null;
		switch (character) {
			case Abbot -> {
				System.out.println(StringFormatter.formatWithColor("Enter the color of the Student you want to pick from the card:", ANSIColors.Yellow));
				String studentColor = getTerminalScanner().nextLine();
				Student chosenStudent = null;
				for (Student student: Student.values()) {
					if (student.getColor() != null && student.getColor().equals(studentColor)) {
						chosenStudent = student;
						break;
					}
				}
				if (chosenStudent != null && tableView.getStudentsInCardAtIndex(playerStateView.getPurchasedCharacterCard()).getCount(chosenStudent) > 0) {
					try {
						System.out.println(StringFormatter.formatWithColor("Enter the index of the destination island:", ANSIColors.Yellow));
						int index = Integer.parseInt(getTerminalScanner().nextLine());
						if (index >= 0 && index < tableView.getNumberOfIslands()) {
							isCommandValid = true;
							paramSet = new CharacterCardNetworkParamSet(chosenStudent, null, false, -1, -1, index, null);
						}
					} catch (NumberFormatException ignored) {
					}
				}
			}
			case CheeseMan -> {
				System.out.println(StringFormatter.formatWithColor("The CheeseMan card cannot be played, since its effect is passive", ANSIColors.Yellow));
				isCommandValid = true;
			}
			case Ambassador -> {
				try {
					System.out.println(StringFormatter.formatWithColor("Enter the index of the target island:", ANSIColors.Yellow));
					int index = Integer.parseInt(getTerminalScanner().nextLine());
					if (index >= 0 && index < tableView.getNumberOfIslands()) {
						isCommandValid = true;
						paramSet = new CharacterCardNetworkParamSet(null, null, false, -1, index, index, null);
					}
				} catch (NumberFormatException ignored) {
				}
			}
			case Magician -> {
				try {
					System.out.println(StringFormatter.formatWithColor("Enter the number of additional Mother Nature steps [0-2]:", ANSIColors.Yellow));
					int additionalSteps = Integer.parseInt(getTerminalScanner().nextLine());
					if (additionalSteps >= 0 && additionalSteps <= 2) {
						isCommandValid = true;
						paramSet = new CharacterCardNetworkParamSet(null, null, false, additionalSteps, -1, additionalSteps, null);
					}
				} catch (NumberFormatException ignored) {
				}
			}
			case Herbalist -> {
				try {
					System.out.println(StringFormatter.formatWithColor("Enter the index of the destination island:", ANSIColors.Yellow));
					int index = Integer.parseInt(getTerminalScanner().nextLine());
					if (index >= 0 && index < tableView.getNumberOfIslands()) {
						isCommandValid = true;
						paramSet = new CharacterCardNetworkParamSet(null, null, false, -1, index, index, CharacterCardParamSet.StopCardMovementMode.ToIsland);
					}
				} catch (NumberFormatException ignored) {
				}
			}
			case Centaurus -> {
				System.out.println(StringFormatter.formatWithColor("The Centaurus card cannot be played, since its effect is passive", ANSIColors.Yellow));
				isCommandValid = true;
			}
			case Circus -> {
				Student studentFromCard = askPickStudentColor("Enter the color of the Student you want to pick from the card:");
				if (studentFromCard != null) {
					Student studentFromEntrance = askPickStudentColor("Enter the color of the Student you want to pick from the entrance space:");
					if (studentFromEntrance != null) {
						// Check that the Student from the Card is valid, as well as the Student from the Entrance space
						if (playerStateView.getEntrance().getCount(studentFromEntrance) > 0 && tableView.getStudentsInCardAtIndex(playerStateView.getPurchasedCharacterCard()).getCount(studentFromCard) > 0) {
							isCommandValid = true;
							paramSet = new CharacterCardNetworkParamSet(studentFromCard, studentFromEntrance, false, -1, -1, -1, null);
						}
					}
				}
			}
			case Swordsman -> {
				System.out.println(StringFormatter.formatWithColor("The Swordsman card cannot be played, since its effect is passive", ANSIColors.Yellow));
				isCommandValid = true;
			}
			case Mushroom -> {
				Student excludedStudent = askPickStudentColor("Enter the color of the Student you want to exclude from the Influence count:");
				if (excludedStudent != null) {
					isCommandValid = true;
					paramSet = new CharacterCardNetworkParamSet(excludedStudent, null, false, -1, -1, -1, null);
				}
			}
			case Musician -> {
				Student studentFromEntrance = askPickStudentColor("Enter the color of the Student you want to pick from the entrance space:");
				if (studentFromEntrance != null) {
					Student studentFromTable = askPickStudentColor("Enter the color of the Student you want to pick from the table space:");
					if (studentFromTable != null) {
						// Check that the Student from the Card is valid, as well as the Student from the Entrance space
						if (playerStateView.getEntrance().getCount(studentFromEntrance) > 0 && playerStateView.getTable().getCount(studentFromTable) > 0) {
							isCommandValid = true;
							paramSet = new CharacterCardNetworkParamSet(studentFromEntrance, studentFromTable, false, -1, -1, -1, null);
						}
					}
				}
			}
			case Queen -> {
				Student chosenStudent = askPickStudentColor("Enter the color of the Student you want to pick from the Card:");
				if (chosenStudent != null && tableView.getStudentsInCardAtIndex(playerStateView.getPurchasedCharacterCard()).getCount(chosenStudent) > 0) {
					isCommandValid = true;
					paramSet = new CharacterCardNetworkParamSet(chosenStudent, null, false, -1, -1, -1, null);
				}
			}
			case Thief -> {
				Student chosenStudent = askPickStudentColor("Enter the color of the Student you want to \"steal\":");
				if (chosenStudent != null) {
					isCommandValid = true;
					paramSet = new CharacterCardNetworkParamSet(chosenStudent, null, false, -1, -1, -1, null);
				}
			}
		}
		if (isCommandValid) {
			return paramSet;
		} else {
			throw new CharacterCardActionInvalidException();
		}
	}
	/**
	 this method ask what type of {@link it.polimi.ingsw.server.model.student.Student Student} want to use on effect of card
	 @param descriptiveMessage(type string) coint the request for the player according type of card
	 @return (type Student) type of student player choose
	 */
	private Student askPickStudentColor(String descriptiveMessage) {
		System.out.println(StringFormatter.formatWithColor(descriptiveMessage, ANSIColors.Yellow));
		String studentColor = getTerminalScanner().nextLine();
		Student chosenStudent = null;
		for (Student student: Student.values()) {
			if (student.getColor() != null && student.getColor().equals(studentColor)) {
				chosenStudent = student;
				break;
			}
		}
		return chosenStudent;
	}

	/**
	 * method called whan arrive a {@link it.polimi.ingsw.notifications.Notification Notification}with name NetworkTimeoutNotification
	 *the method endgame
	 * @param notification (@code Notification) that contain the information of event
	 */
	protected void didReceiveNetworkTimeoutNotification(Notification notification) {
	}
}
