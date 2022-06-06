package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.ActivePlayerMessage;
import it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.ui.GUIUtils;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SchoolBoardPane extends RescalableAnchorPane {
	
	private final String ownerNickname;
	private final boolean isPrimary;
	private StudentDropTarget[] allowedDropDestinationsForDrag, allowedStudentDestinationsForPhase;
	
	private final GridPane entranceGrid;
	private final GridPane diningGrid;
	private final GridPane professors;
	private final GridPane towersGrid;
	
	public SchoolBoardPane(boolean isPrimary, String ownerNickname) {
		super();
		this.allowedDropDestinationsForDrag = new StudentDropTarget[0];
		this.allowedStudentDestinationsForPhase = new StudentDropTarget[0];
		this.ownerNickname = ownerNickname;
		this.isPrimary = isPrimary;
		//Set the Background Image
		setStyle("-fx-background-image: url(" + GUIUtils.getURI("images/Plancia_DEF2.png") + ");\n-fx-background-size: 100% 100%");
		//Create the grid that handles the Students
		entranceGrid = new GridPane();
		setupMouseClickAfterStudentStartMoving(entranceGrid, StudentDropTarget.ToEntrance);
		setDisabled(!isPrimary);
		//Create the grid for the dining room
		diningGrid = new GridPane();
		setupMouseClickAfterStudentStartMoving(diningGrid, StudentDropTarget.ToDiningRoom);
		//Create the V-Stack for the Professors
		professors = new GridPane();
		//Create the grid for the Towers
		towersGrid = new GridPane();
		//Rescale the pane
		rescale(1);
		getChildren().addAll(entranceGrid, diningGrid, professors, towersGrid);
		//Register for auto-update notifications
		NotificationCenter.shared().addObserver(this::didReceivePlayerStatusNotification, NotificationName.ClientDidReceivePlayerStateMessage, null);
		NotificationCenter.shared().addObserver(this::didReceiveActivePlayerNotification, NotificationName.ClientDidReceiveActivePlayerMessage, null);
		NotificationCenter.shared().addObserver(this::didReceiveStartStudentMoveNotification, NotificationName.JavaFXDidStartMovingStudent, null);
		NotificationCenter.shared().addObserver(this::didReceiveEndStudentMoveNotification, NotificationName.JavaFXDidEndMovingStudent, null);
	}
	
	public void rescale(double scale) {
		double multiplier = (isPrimary ? 0.75 : 0.35) * scale;
		//Define the pane size to init
		setPrefSize(838 * multiplier, 363.5 * multiplier);
		//Create the grid that handles the Students
		entranceGrid.setPrefSize(88 * multiplier, 277 * multiplier);
		entranceGrid.setLayoutX(27 * multiplier);
		entranceGrid.setLayoutY(44 * multiplier);
		entranceGrid.setHgap(12 * multiplier);
		entranceGrid.setVgap(22 * multiplier);
		entranceGrid.getRowConstraints().removeAll(entranceGrid.getRowConstraints());
		entranceGrid.getColumnConstraints().removeAll(entranceGrid.getColumnConstraints());
		for (int i = 0; i < 4; i++) {
			RowConstraints row = new RowConstraints(38 * multiplier);
			entranceGrid.getRowConstraints().add(row);
		}
		for (int i = 0; i < 2; i++) {
			ColumnConstraints col = new ColumnConstraints(38 * multiplier);
			entranceGrid.getColumnConstraints().add(col);
		}
		//Create the grid for the dining room
		diningGrid.setMinWidth(396 * multiplier);
		diningGrid.setMinHeight(275 * multiplier);
		diningGrid.setLayoutX(155 * multiplier);
		diningGrid.setLayoutY(45 * multiplier);
		diningGrid.setVgap(22 * multiplier);
		diningGrid.setHgap(2 * multiplier);
		diningGrid.getRowConstraints().removeAll(diningGrid.getRowConstraints());
		diningGrid.getColumnConstraints().removeAll(diningGrid.getColumnConstraints());
		for (int i = 0; i < 5; i++) {
			RowConstraints row = new RowConstraints(38 * multiplier);
			diningGrid.getRowConstraints().add(row);
		}
		for (int i = 0; i < 2; i++) {
			ColumnConstraints col = new ColumnConstraints(38 * multiplier);
			diningGrid.getColumnConstraints().add(col);
		}
		//Create the V-Stack for the Professors
		professors.setMinWidth(37 * multiplier);
		professors.setMinHeight(285 * multiplier);
		professors.setLayoutX(592 * multiplier);
		professors.setLayoutY(37 * multiplier);
		professors.setVgap(17 * multiplier);
		professors.getRowConstraints().removeAll(professors.getRowConstraints());
		professors.getColumnConstraints().removeAll(professors.getColumnConstraints());
		for (int i = 0; i < 5; i++) {
			RowConstraints row = new RowConstraints(42 * multiplier);
			professors.getRowConstraints().add(row);
		}
		professors.getColumnConstraints().add(new ColumnConstraints(37 * multiplier));
		//Create the grid for the Towers
		towersGrid.setMinWidth(110 * multiplier);
		towersGrid.setMinHeight(226 * multiplier);
		towersGrid.setLayoutX(680 * multiplier);
		towersGrid.setLayoutY(69 * multiplier);
		towersGrid.setHgap(18 * multiplier);
		towersGrid.setVgap(14 * multiplier);
		towersGrid.getRowConstraints().removeAll(towersGrid.getRowConstraints());
		towersGrid.getColumnConstraints().removeAll(towersGrid.getColumnConstraints());
		for (int i = 0; i < 4; i++) {
			RowConstraints row = new RowConstraints(46 * multiplier);
			towersGrid.getRowConstraints().add(row);
		}
		for (int i = 0; i < 2; i++) {
			ColumnConstraints col = new ColumnConstraints(46 * multiplier);
			towersGrid.getColumnConstraints().add(col);
		}
	}
	
	protected String getOwnerNickname() {
		return ownerNickname;
	}
	
	public void setAllowedStudentDestinationsForPhase(StudentDropTarget[] allowedStudentDestinationsForPhase) {
		this.allowedStudentDestinationsForPhase = allowedStudentDestinationsForPhase;
	}
	
	private void didReceiveActivePlayerNotification(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof ActivePlayerMessage message) {
			if (isPrimary) {
				// Disable if the Player is not active anymore
				setDisabled(!(message.getActiveNickname().equals(ownerNickname)));
			}
		}
	}
	
	protected void didReceivePlayerStatusNotification(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof PlayerStateMessage message && message.getNickname().equals(ownerNickname)) {
			displayEntranceFromMessage(message);
			displayDiningRoomFromMessage(message);
			displayProfessorsFromMessage(message);
			displayTowersFromMessage(message);
		}
	}
	
	private void displayEntranceFromMessage(PlayerStateMessage message) {
		Platform.runLater(() -> entranceGrid.getChildren().removeAll(entranceGrid.getChildren()));
		int row = 0, col = 1;
		for (Student student: Student.values()) {
			for (int i = 0; i < message.getBoard().getEntrance().getCount(student); i++) {
				//Place a Pawn at (row, col)
				AnchorPane studentButton = GUIUtils.createStudentButton(student, new StudentDropTarget[]{StudentDropTarget.ToDiningRoom, StudentDropTarget.ToIsland, StudentDropTarget.ToCharacterCard});
				int finalRow = row;
				int finalCol = col;
				//System.out.println("Adding student at (" + finalRow + ", " + finalCol + ")");
				Platform.runLater(() -> {
					GridPane.setRowIndex(studentButton, finalRow);
					GridPane.setColumnIndex(studentButton, finalCol);
					entranceGrid.getChildren().add(studentButton);
				});
				col = (col + 1) % 2;
				row = col == 1 ? row + 1 : row;
			}
		}
	}
	
	private void displayDiningRoomFromMessage(PlayerStateMessage message) {
		Platform.runLater(() -> diningGrid.getChildren().removeAll(diningGrid.getChildren()));
		int row = 0, col = 0;
		for (Student student: Student.values()) {
			for (int i = 0; i < message.getBoard().getDiningRoom().getCount(student); i++) {
				//Place a Pawn at (row, col)
				AnchorPane studentButton = GUIUtils.createStudentButton(student, new StudentDropTarget[0]);
				int finalRow = row;
				int finalCol = col;
				Platform.runLater(() -> {
					GridPane.setRowIndex(studentButton, finalRow);
					GridPane.setColumnIndex(studentButton, finalCol);
					diningGrid.getChildren().add(studentButton);
				});
				col += 1;
			}
			col = 0;
			row += 1;
		}
	}
	
	private void displayProfessorsFromMessage(PlayerStateMessage message) {
		Platform.runLater(() -> professors.getChildren().removeAll(professors.getChildren()));
		int row = 0;
		for (Student professor: Student.values()) {
			if (message.getBoard().getControlledProfessors().contains(professor.getAssociatedProfessor())) {
				AnchorPane professorView = GUIUtils.createProfessorButton(professor);
				int finalRow = row;
				Platform.runLater(() -> {
					GridPane.setRowIndex(professorView, finalRow);
					GridPane.setColumnIndex(professorView, 0);
					professors.getChildren().add(professorView);
				});
			}
			row += 1;
		}
	}
	
	private void displayTowersFromMessage(PlayerStateMessage message) {
		Platform.runLater(() -> towersGrid.getChildren().removeAll(towersGrid.getChildren()));
		for (int i = 0; i < message.getBoard().getAvailableTowerCount(); i++) {
			int row = i / 2;
			int col = i % 2;
			AnchorPane towerView = GUIUtils.createTowerButton(message.getBoard().getTowerType());
			Platform.runLater(() -> {
				GridPane.setRowIndex(towerView, row);
				GridPane.setColumnIndex(towerView, col);
				towersGrid.getChildren().add(towerView);
			});
		}
	}
	
	private void setupMouseClickAfterStudentStartMoving(Pane targetPane, StudentDropTarget dropTarget) {
		if (isPrimary) {
			targetPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				for (StudentDropTarget allowedDropTarget: allowedDropDestinationsForDrag) {
					if (allowedDropTarget == dropTarget) {
						//Action registered successfully - post notification
						HashMap<String, Object> userInfo = new HashMap<>();
						userInfo.put("dropTarget", dropTarget);
						NotificationCenter.shared().post(NotificationName.JavaFXDidEndMovingStudent, null, userInfo);
						//End the operation
						entranceGrid.setStyle("");
						diningGrid.setStyle("");
						break;
					}
				}
				event.consume();
			});
		}
	}
	
	private void didReceiveStartStudentMoveNotification(Notification notification) {
		if (isPrimary) {
			List<StudentDropTarget> allowableDefaultMovements = Arrays.stream(allowedStudentDestinationsForPhase).toList();
			List<StudentDropTarget> dropDestinationsForDrag = Arrays.stream(((StudentDropTarget[]) notification.getUserInfo().get(NotificationKeys.StudentDropTargets.getRawValue()))).toList();
			this.allowedDropDestinationsForDrag = dropDestinationsForDrag.stream().filter(allowableDefaultMovements::contains).toList().toArray(new StudentDropTarget[0]);
			//Highlight areas that can be a target for the operation
			for (StudentDropTarget dropTarget: allowedDropDestinationsForDrag) {
				switch (dropTarget) {
					case ToEntrance -> {
						entranceGrid.setStyle(entranceGrid.getStyle() + ";\n-fx-background-color: rgba(80,255,80,0.4)");
					}
					case ToDiningRoom -> {
						diningGrid.setStyle(diningGrid.getStyle() + ";\n-fx-background-color: rgba(80,255,80,0.4)");
					}
				}
			}
		}
	}
	
	private void didReceiveEndStudentMoveNotification(Notification notification) {
		this.allowedDropDestinationsForDrag = new StudentDropTarget[0]; //To reset to the initial default state
	}
}
