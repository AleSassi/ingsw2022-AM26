package it.polimi.ingsw.client.ui.viewcontrollers;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.client.ui.*;
import it.polimi.ingsw.client.ui.assistants.AssistantCardPickerView;
import it.polimi.ingsw.client.ui.characters.CharacterCardContainer;
import it.polimi.ingsw.client.ui.islands.IslandContainer;
import it.polimi.ingsw.client.ui.rescale.JavaFXRescalable;
import it.polimi.ingsw.client.ui.rescale.RescaleUtils;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainBoardController extends CleanableController implements JavaFXRescalable {
	
	@FXML
	private AnchorPane mainPane;
	
	private MatchPhase activeMatchPhase;
	private List<SchoolBoardContainer> schoolBoardContainers;
	private Student movingStudentColor;
	private String currentlyActivePlayerNickname;
	private Pane faderPane;
	private Label waitTurnLabel;
	private PlayerStateMessage stateMessage;
	private IslandContainer islandContainer;
	private CloudsContainer cloudsContainer;
	private CharacterCardContainer characterCardContainer;
	
	public void load() {
		schoolBoardContainers = new ArrayList<>();
		NotificationCenter.shared().addObserver(this, this::didReceiveWindowDidResizeNotification, NotificationName.JavaFXWindowDidResize, null);
		NotificationCenter.shared().addObserver(this, this::didReceivePlayerStatusNotification, NotificationName.ClientDidReceivePlayerStateMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveActivePlayerMessage, NotificationName.ClientDidReceiveActivePlayerMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveTableStateMessage, NotificationName.ClientDidReceiveTableStateMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveMatchStateMessage, NotificationName.ClientDidReceiveMatchStateMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceivePlayerActionResponse, NotificationName.ClientDidReceivePlayerActionResponse, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveVictoryNotification, NotificationName.ClientDidReceiveVictoryMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveStudentMovementStart, NotificationName.JavaFXDidStartMovingStudent, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveStudentMovementEnd, NotificationName.JavaFXDidEndMovingStudent, null);
		NotificationCenter.shared().addObserver(this, this::didReceivePlayedCard, NotificationName.JavaFXPlayedCharacterCard, null);
	}
	
	private void didReceivePlayedCard(Notification notification) {
		movingStudentColor = null; //To interrupt pending actions
	}
	
	public void showModalPopup(ModalPopup popup) {
		Platform.runLater(() -> {
			showFaderPane();
			mainPane.getChildren().add(popup);
		});
	}
	
	public void dismissModalPopup(ModalPopup popup) {
		Platform.runLater(() -> {
			mainPane.getChildren().remove(faderPane);
			mainPane.getChildren().remove(popup);
			faderPane = null;
		});
	}
	
	protected void didReceivePlayerStatusNotification(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof PlayerStateMessage message) {
			if (message.getNickname().equals(Client.getNickname())) {
				stateMessage = message;
			}
			if (!schoolBoardContainers.stream().map(SchoolBoardContainer::getOwnerNickname).toList().contains(message.getNickname())) {
				//Create the new container
				SchoolBoardContainer newContainer = new SchoolBoardContainer(message.getNickname().equals(Client.getNickname()), message.getNickname(), characterCardContainer != null ? message.getAvailableCoins() : null);
				Platform.runLater(() -> mainPane.getChildren().add(newContainer));
				if (newContainer.getOwnerNickname().equals(Client.getNickname())) {
					schoolBoardContainers.add(newContainer);
				} else {
					schoolBoardContainers.add(0, newContainer);
				}
				rescale(1);
				//Forward the notification event to the new container
				newContainer.forwardInitialPlayerStatusNotification(notification);
			}
		}
	}
	
	private void didReceiveWindowDidResizeNotification(Notification notification) {
		Double scaleValue = RescaleUtils.rescaleAfterNotification(notification);
		if (scaleValue != null) {
			rescale(scaleValue);
		}
	}
	
	protected void didReceiveTableStateMessage(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof TableStateMessage message) {
			if (characterCardContainer == null && !(message.getPlayableCharacterCards() == null || message.getPlayableCharacterCards().isEmpty())) {
				characterCardContainer = new CharacterCardContainer(message);
				characterCardContainer.setParentController(this);
				Platform.runLater(() -> mainPane.getChildren().add(characterCardContainer));
			}
			if (islandContainer == null && cloudsContainer == null) {
				islandContainer = new IslandContainer(notification);
				cloudsContainer = new CloudsContainer(notification);
				Platform.runLater(() -> mainPane.getChildren().addAll(islandContainer, cloudsContainer));
				
				AnchorPane.setRightAnchor(islandContainer, 0.0);
				AnchorPane.setTopAnchor(islandContainer, 0.0);
			}
		}
	}
	
	protected void didReceiveActivePlayerMessage(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof ActivePlayerMessage message) {
			currentlyActivePlayerNickname = message.getActiveNickname();
			schoolBoardContainers.get(schoolBoardContainers.size() - 1).setActive(currentlyActivePlayerNickname.equals(Client.getNickname()));
			setDisplaysWaitForTurnFader(!currentlyActivePlayerNickname.equals(Client.getNickname()));
		}
	}
	
	private void setDisplaysWaitForTurnFader(boolean displaysWaitForTurnFader) {
		Platform.runLater(() -> {
			if (displaysWaitForTurnFader && faderPane == null) {
				showFaderPane();
				waitTurnLabel = new Label(currentlyActivePlayerNickname + " is currently playing their turn. Please wait until it is your turn to play.");
				waitTurnLabel.setFont(new Font("Avenir", 30));
				waitTurnLabel.setTextFill(new Color(1, 1, 1, 1));
				waitTurnLabel.setContentDisplay(ContentDisplay.CENTER);
				waitTurnLabel.setAlignment(Pos.CENTER);
				waitTurnLabel.setWrapText(true);
				AnchorPane.setTopAnchor(waitTurnLabel, 20.0);
				AnchorPane.setBottomAnchor(waitTurnLabel, 20.0);
				AnchorPane.setLeftAnchor(waitTurnLabel, 20.0);
				AnchorPane.setRightAnchor(waitTurnLabel, 20.0);
				mainPane.getChildren().add(waitTurnLabel);
			} else if (faderPane != null) {
				mainPane.getChildren().remove(faderPane);
				mainPane.getChildren().remove(waitTurnLabel);
				faderPane = null;
				waitTurnLabel = null;
			}
		});
	}
	
	protected void didReceiveMatchStateMessage(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof MatchStateMessage message && currentlyActivePlayerNickname.equals(Client.getNickname())) {
			prepareForMatchPhase(message.getCurrentMatchPhase());
		}
	}
	
	private void prepareForMatchPhase(MatchPhase matchPhase) {
		// Update the list of allowed moves
		this.activeMatchPhase = matchPhase;
		System.out.println(activeMatchPhase);
		switch (matchPhase) {
			case PlanPhaseStepTwo -> {
				// Allow only assistant card choice
				schoolBoardContainers.get(schoolBoardContainers.size() - 1).setAllowedStudentMovements(new StudentDropTarget[0]);
				islandContainer.setAllowedStudentMovements(new StudentDropTarget[0]);
				cloudsContainer.setActivateCloudPick(false);
				showAssistantCardModalWindow();
			}
			case ActionPhaseStepOne -> {
				// Allow student movement from entrance to everywhere, and allow character card purchase and play (if applicable)
				schoolBoardContainers.get(schoolBoardContainers.size() - 1).setAllowedStudentMovements(StudentDropTarget.all());
				islandContainer.setAllowedStudentMovements(StudentDropTarget.all());
				cloudsContainer.setActivateCloudPick(false);
				if (characterCardContainer != null) {
					characterCardContainer.setDisable(false);
				}
			}
			case ActionPhaseStepTwo -> {
				// Disable everything, present a popup to choose the number of steps MN must move by
				schoolBoardContainers.get(schoolBoardContainers.size() - 1).setAllowedStudentMovements(new StudentDropTarget[0]);
				islandContainer.setAllowedStudentMovements(new StudentDropTarget[0]);
				cloudsContainer.setActivateCloudPick(false);
				showMotherNatureMovementAlert();
				if (characterCardContainer != null) {
					characterCardContainer.setDisable(true);
				}
			}
			case ActionPhaseStepThree -> {
				// Disable everything except the Cloud tiles, when clicking on a Cloud tile send the event
				schoolBoardContainers.get(schoolBoardContainers.size() - 1).setAllowedStudentMovements(new StudentDropTarget[0]);
				islandContainer.setAllowedStudentMovements(new StudentDropTarget[0]);
				cloudsContainer.setActivateCloudPick(true);
				if (characterCardContainer != null) {
					characterCardContainer.setDisable(true);
				}
			}
		}
	}
	
	private void showAssistantCardModalWindow() {
		AssistantCardPickerView cardPickerView = new AssistantCardPickerView(stateMessage.getAvailableCardsDeck());
		cardPickerView.setSelectionHandler(pickedAssistant -> {
			int index = 0;
			for (AssistantCard assistantCard : stateMessage.getAvailableCardsDeck()) {
				if (assistantCard == pickedAssistant) {
					break;
				}
				index += 1;
			}
			PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPlayAssistantCard, index, null, false, -1, -1, -1, -1, null);
			GameClient.shared().sendMessage(actionMessage);
			dismissModalPopup(cardPickerView);
		});
		showModalPopup(cardPickerView);
	}
	
	private void showFaderPane() {
		faderPane = new FaderPane();
		mainPane.getChildren().add(faderPane);
	}
	
	private void showMotherNatureMovementAlert() {
		Platform.runLater(() -> {
			TextInputDialog td = new TextInputDialog("Mother Nature Steps");
			td.setHeaderText("Enter the number of steps Mother Nature must move by (between 0 and 2)");
			td.showAndWait().ifPresentOrElse((text) -> {
				try {
					showFaderPane();
					int number = Integer.parseInt(text);
					if (number < 0) {
						number = 0;
					} else if (number > 2) {
						number = 2;
					}
					//Send the player action
					PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidMoveMNBySteps, -1, null, false, -1, number, -1, -1, null);
					GameClient.shared().sendMessage(actionMessage);
				} catch (NumberFormatException e) {
					this.showMotherNatureMovementAlert();
				}
			}, this::showMotherNatureMovementAlert);
		});
	}
	
	private void didReceiveStudentMovementStart(Notification notification) {
		//Cache the parameters so that at the student movement end we can validate it and send a message to the server
		movingStudentColor = (Student) notification.getUserInfo().get(NotificationKeys.ClickedStudentColor.getRawValue());
	}
	
	private void didReceiveStudentMovementEnd(Notification notification) {
		//Use the cached parameter to perform the student movement (to island - to dining room)
		StudentDropTarget dropTarget = (StudentDropTarget) notification.getUserInfo().get("dropTarget");
		switch (dropTarget) {
			case ToDiningRoom -> {
				PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidMoveStudent, -1, movingStudentColor, false, -1, -1, -1, -1, null);
				GameClient.shared().sendMessage(actionMessage);
			}
			case ToIsland -> {
				int islandIndex = (int) notification.getUserInfo().get("targetIslandIndex");
				PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidMoveStudent, -1, movingStudentColor, true, islandIndex, -1, -1, -1, null);
				GameClient.shared().sendMessage(actionMessage);
			}
		}
	}
	
	private void didReceivePlayerActionResponse(Notification notification) {
		PlayerActionResponse response = (PlayerActionResponse) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		if (!response.isActionSuccess() && response.getNickname().equals(Client.getNickname())) {
			//Restore the UI for the current match phase
			prepareForMatchPhase(activeMatchPhase);
			//Alert the user that the action was cancelled
			Platform.runLater(() -> {
				Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Invalid move", ButtonType.CLOSE);
				errorAlert.setContentText(response.getDescriptiveErrorMessage());
				errorAlert.show();
			});
		}
	}
	
	private void didReceiveVictoryNotification(Notification notification) {
		if (notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof VictoryMessage message) {
			Platform.runLater(() -> {
				try {
					EndgameController endgameController = GUI.setRoot("scenes/win").getController();
					endgameController.endGame(message.getWinners());
					cleanupAfterTermination();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	protected void cleanupAfterTermination() {
		for (SchoolBoardContainer schoolBoardContainer: schoolBoardContainers) {
			schoolBoardContainer.cleanupBeforeDisappear();
		}
		schoolBoardContainers = null;
		if (islandContainer != null) {
			islandContainer.cleanupBeforeDisappear();
			islandContainer = null;
		}
		if (cloudsContainer != null) {
			cloudsContainer.cleanupBeforeDisappear();
			cloudsContainer = null;
		}
		if (characterCardContainer != null) {
			characterCardContainer.cleanupBeforeDisappear();
			characterCardContainer = null;
		}
	}
	
	@Override
	public void rescale(double scale) {
		for (int i = 0; i < schoolBoardContainers.size(); i++) {
			if (i == 0) {
				schoolBoardContainers.get(0).relocate(0, 0);
			} else {
				schoolBoardContainers.get(i).relocate(0, schoolBoardContainers.get(i - 1).getLayoutY() + schoolBoardContainers.get(i - 1).getPrefHeight() + 10);
			}
		}
		cloudsContainer.setLayoutX(GUI.getWindowWidth() - 500 * scale + 70 * scale);
		cloudsContainer.setLayoutY(250 * scale - 70 * scale * 0.5);
	}
}
