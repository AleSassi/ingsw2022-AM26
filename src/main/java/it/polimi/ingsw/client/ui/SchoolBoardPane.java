package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.ActivePlayerMessage;
import it.polimi.ingsw.server.controller.network.messages.MatchStateMessage;
import it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.ui.GUIUtils;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Class {@code SchoolBoardPane} represent the SchoolBoard
 */
public class SchoolBoardPane extends RescalableAnchorPane {

    private final String ownerNickname;
    private final boolean isPrimary;
    private StudentDropTarget[] allowedDropDestinationsForDrag, allowedStudentDestinationsForPhase;
    private CardParameterMode parameterMode = CardParameterMode.Disabled;
    private Character activeCharacter;
    private Student pickedSrcStudentForSwap;

    private final HighlightableGridPane entranceGrid;
    private final HighlightableGridPane diningGrid;
    private final GridPane professors;
    private final GridPane towersGrid;
    
    private final StudentDropTarget[] defaultDropDestinationsForEntranceStudents = new StudentDropTarget[]{StudentDropTarget.ToDiningRoom, StudentDropTarget.ToIsland, StudentDropTarget.ToCharacterCard};
    private final StudentDropTarget[] defaultDropDestinationsForDiningStudents = null;

    /**
     * Constructor creates the SchoolBoard AnchorPane
     * @param isPrimary (type boolean) true if this {@code SchoolBoardPane} is primary
     * @param ownerNickname (type String) owner's nickname
     */
    public SchoolBoardPane(boolean isPrimary, String ownerNickname) {
        super();
        this.allowedDropDestinationsForDrag = new StudentDropTarget[0];
        this.allowedStudentDestinationsForPhase = new StudentDropTarget[0];
        this.ownerNickname = ownerNickname;
        this.isPrimary = isPrimary;
        //Set the Background Image
        setStyle("-fx-background-image: url(" + GUIUtils.getURI("images/Plancia_DEF2.png") + ");\n-fx-background-size: 100% 100%");
        //Create the grid that handles the Students
        entranceGrid = new HighlightableGridPane();
        setupMouseClickAfterStudentStartMoving(entranceGrid, StudentDropTarget.ToEntrance);
        setDisabled(!isPrimary);
        //Create the grid for the dining room
        diningGrid = new HighlightableGridPane();
        setupMouseClickAfterStudentStartMoving(diningGrid, StudentDropTarget.ToDiningRoom);
        //Create the V-Stack for the Professors
        professors = new GridPane();
        //Create the grid for the Towers
        towersGrid = new GridPane();
        //Rescale the pane
        double scalingValue = isPrimary ? 0.65 : 0.35;
        setScalingValue(scalingValue);
        getChildren().addAll(entranceGrid, diningGrid, professors, towersGrid);
        //Register for auto-update notifications
        NotificationCenter.shared().addObserver(this, this::didReceivePlayerStatusNotification, NotificationName.ClientDidReceivePlayerStateMessage, null);
        NotificationCenter.shared().addObserver(this, this::didReceiveMatchStateNotification, NotificationName.ClientDidReceiveMatchStateMessage, null);
        NotificationCenter.shared().addObserver(this, this::didReceiveActivePlayerNotification, NotificationName.ClientDidReceiveActivePlayerMessage, null);
        NotificationCenter.shared().addObserver(this, this::didReceiveStartStudentMoveNotification, NotificationName.JavaFXDidStartMovingStudent, null);
        NotificationCenter.shared().addObserver(this, this::didReceiveEndStudentMoveNotification, NotificationName.JavaFXDidEndMovingStudent, null);
        NotificationCenter.shared().addObserver(this, this::didReceiveCharacterCardPlayedNotification, NotificationName.JavaFXDidPlayCharacterCard, null);
    }
    
    @Override
    public double getUnscaledWidth() {
        return 838;
    }
    
    @Override
    public double getUnscaledHeight() {
        return 363.5;
    }
    
    @Override
    public void rescale(double scale) {
        //Define the pane size to init
        setPrefSize(getUnscaledWidth() * scale, getUnscaledHeight() * scale);
        //Create the grid that handles the Students
        rescaleGridPane(entranceGrid, 88, 277, 27, 44, 22, 12, 38, 38, 4, 2, scale);
        //Create the grid for the dining room
        rescaleGridPane(diningGrid, 396, 275, 155, 45, 22, 2, 38, 38, 5, 10, scale);
        //Create the V-Stack for the Professors
        rescaleGridPane(professors, 37, 255, 592, 37, 17, 0, 37, 42, 5, 1, scale);
        //Create the grid for the Towers
        rescaleGridPane(towersGrid, 100, 226, 680, 69, 18, 14, 46, 46, 4, 2, scale);
    }
    
    /**
     * Rescales a grid pane
     * @param gridPane The grid pane to scale
     * @param width The unscaled width
     * @param height The unscaled height
     * @param x The unscaled x coordinate
     * @param y The unscaled y coordinate
     * @param vGap The unscaled vGap
     * @param hGap The unscaled hGap
     * @param colWidth The unscaled column width
     * @param rowHeight The unscaled row height
     * @param numberOfRows The number of rows
     * @param numberOfColumns The number of columns
     * @param scale The scale factor
     */
    private void rescaleGridPane(GridPane gridPane, double width, double height, double x, double y, double vGap, double hGap, double colWidth, double rowHeight, int numberOfRows, int numberOfColumns, double scale) {
        gridPane.setPrefSize(width * scale, height * scale);
        gridPane.setMinWidth(width * scale);
        gridPane.setMinHeight(height * scale);
        gridPane.setLayoutX(x * scale);
        gridPane.setLayoutY(y * scale);
        gridPane.setHgap(hGap * scale);
        gridPane.setVgap(vGap * scale);
        gridPane.getRowConstraints().removeAll(gridPane.getRowConstraints());
        gridPane.getColumnConstraints().removeAll(gridPane.getColumnConstraints());
        for (int i = 0; i < numberOfRows; i++) {
            RowConstraints row = new RowConstraints(rowHeight * scale);
            gridPane.getRowConstraints().add(row);
        }
        for (int i = 0; i < numberOfColumns; i++) {
            ColumnConstraints col = new ColumnConstraints(colWidth * scale);
            gridPane.getColumnConstraints().add(col);
        }
    }

    /**
     * Gets the owner of the {@link it.polimi.ingsw.client.ui.SchoolBoardPane}
     * @return (type String) return the owner of the {@link it.polimi.ingsw.client.ui.SchoolBoardPane}
     */
    protected String getOwnerNickname() {
        return ownerNickname;
    }
    
    /**
     * Sets the allowed {@link it.polimi.ingsw.utils.ui.StudentDropTarget StudentDropTargets} depending on the {@link it.polimi.ingsw.server.model.match.MatchPhase MatchPhase}
     * @param allowedStudentDestinationsForPhase (type StudentDropTarget[]) The allowed drop targets
     */
    public void setAllowedStudentDestinationsForPhase(StudentDropTarget[] allowedStudentDestinationsForPhase) {
        this.allowedStudentDestinationsForPhase = allowedStudentDestinationsForPhase;
    }

    /**
     * {@code ActivePlayer} callback, used to enable/disable the pane
     * @param notification (type Notification) The Active Player notification
     */
    private void didReceiveActivePlayerNotification(Notification notification) {
        if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof ActivePlayerMessage message) {
            // Disable if the Player is not active anymore
            setDisabled(!(message.getActiveNickname().equals(ownerNickname) && ownerNickname.equals(Client.getNickname())));
            setDisable(!(message.getActiveNickname().equals(ownerNickname) && ownerNickname.equals(Client.getNickname())));
        }
    }
    
    /**
     * The Match State notification, used to set the correct highlighting depending on the phase
     * @param notification The Match Phase notification
     */
    private void didReceiveMatchStateNotification(Notification notification) {
        if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof MatchStateMessage message && !isDisabled()) {
            if (message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne) {
                Platform.runLater(() -> {
                    entranceGrid.highlight(true);
                    diningGrid.highlight(false);
                });
            }
        }
    }

    /**
     * {@code PlayerStatus} callback when the client receives the Player state, used to update the board
     * @param notification (type Notification) The Player Status notification
     */
    protected void didReceivePlayerStatusNotification(Notification notification) {
        if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof PlayerStateMessage message && message.getNickname().equals(ownerNickname)) {
            displayEntranceFromMessage(message);
            displayDiningRoomFromMessage(message);
            displayProfessorsFromMessage(message);
            displayTowersFromMessage(message);
        }
    }

    /**
     * Displays the Entrance from the {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage PlayerStateMessage} and updates it with new data if already present
     * @param message (type PlayerStateMessage) The player state message
     */
    private void displayEntranceFromMessage(PlayerStateMessage message) {
        Platform.runLater(() -> entranceGrid.getChildren().removeAll(entranceGrid.getChildren()));
        int row = 0, col = 1;
        for (Student student : Student.values()) {
            for (int i = 0; i < message.getBoard().getEntrance().getCount(student); i++) {
                //Place a Pawn at (row, col)
                setupStudentInGrid(row, col, student, defaultDropDestinationsForEntranceStudents, entranceGrid);
                col = (col + 1) % 2;
                row = col == 1 ? row + 1 : row;
            }
        }
        entranceGrid.setDisable(false);
    }

    /**
     * Displays the DiningRoom from the {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage PlayerStateMessage} and updates it with new data if already present
     * @param message (type PlayerStateMessage) The player state message
     */
    private void displayDiningRoomFromMessage(PlayerStateMessage message) {
        Platform.runLater(() -> diningGrid.getChildren().removeAll(diningGrid.getChildren()));
        int row = 0, col = 0;
        for (Student student : Student.values()) {
            for (int i = 0; i < message.getBoard().getDiningRoom().getCount(student); i++) {
                //Place a Pawn at (row, col)
                setupStudentInGrid(row, col, student, defaultDropDestinationsForDiningStudents, diningGrid);
                col += 1;
            }
            col = 0;
            row += 1;
        }
        diningGrid.setDisable(false);
    }

    /**
     * Sets the {@link it.polimi.ingsw.server.model.student.Student Student} in the Grid
     * @param row (type int) target {@code row}
     * @param col(type int) target {@code column}
     * @param student (type Student) {@code Student} type to place
     * @param defaultDropDestinationsForDiningStudents (type StudentDropTarget[]) default {@link it.polimi.ingsw.utils.ui.StudentDropTarget StudentDropTarget} allowed as destinations
     * @param diningGrid (type gridPane) The target grid pane where to add the Student
     */
    private void setupStudentInGrid(int row, int col, Student student, StudentDropTarget[] defaultDropDestinationsForDiningStudents, GridPane diningGrid) {
        AnchorPane studentButton = GUIUtils.createStudentButton(student, defaultDropDestinationsForDiningStudents);
        Platform.runLater(() -> {
            GridPane.setRowIndex(studentButton, row);
            GridPane.setColumnIndex(studentButton, col);
            diningGrid.getChildren().add(studentButton);
        });
    }

    /**
     * Displays the {@link it.polimi.ingsw.server.model.Professor Professors} from the {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage PlayerStateMessage}
     * @param message (type PlayerStateMessage) The player state message to get data from
     */
    private void displayProfessorsFromMessage(PlayerStateMessage message) {
        Platform.runLater(() -> professors.getChildren().removeAll(professors.getChildren()));
        int row = 0;
        for (Student professor : Student.values()) {
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

    /**
     * Displays the {@link it.polimi.ingsw.server.model.Tower Tower}s in the grid from the {@link it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage PlayerStateMessage}
     * @param message (type PlayerStateMessage) The player state message to get data from
     */
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

    /**
     * Sets up a Pane so that after clicking on it the Student will be moved there
     * @param targetPane (type Pane) The Pane where it needs to add the click handler
     * @param dropTarget (type StudentDropTarget) The drop target linked to the target pane
     */
    private void setupMouseClickAfterStudentStartMoving(Pane targetPane, StudentDropTarget dropTarget) {
        if (isPrimary) {
            targetPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                for (StudentDropTarget allowedDropTarget : allowedDropDestinationsForDrag) {
                    if (allowedDropTarget == dropTarget) {
                        //Action registered successfully - post notification
                        HashMap<String, Object> userInfo = new HashMap<>();
                        userInfo.put("dropTarget", dropTarget);
                        NotificationCenter.shared().post(NotificationName.JavaFXDidEndMovingStudent, null, userInfo);
                        //End the operation
                        break;
                    }
                }
                event.consume();
            });
        }
    }

    /**
     * {@code StartStudentMove} callback, used to switch the board mode for clicks depending on whether a Character Card is active or not
     * @param notification (type Notification) The Student Move notification
     */
    private void didReceiveStartStudentMoveNotification(Notification notification) {
        if (isPrimary) {
            switch (parameterMode) {
                case Disabled -> {
                    //Normal board behavior
                    List<StudentDropTarget> allowableDefaultMovements = Arrays.stream(allowedStudentDestinationsForPhase).toList();
                    List<StudentDropTarget> dropDestinationsForDrag = Arrays.stream(((StudentDropTarget[]) notification.getUserInfo().get(NotificationKeys.StudentDropTargets.getRawValue()))).toList();
                    this.allowedDropDestinationsForDrag = dropDestinationsForDrag.stream().filter(allowableDefaultMovements::contains).toList().toArray(new StudentDropTarget[0]);
                    //Highlight areas that can be a target for the operation
                    for (StudentDropTarget dropTarget : allowedDropDestinationsForDrag) {
                        setEnabledWithDropTarget(dropTarget, true);
                    }
                }
                case StudentPicker -> {
                    this.allowedDropDestinationsForDrag = new StudentDropTarget[0];
                    //Immediately send the closed loop notification & cleanup
                    cleanupStyles();
                    setDisabled(true);
                    setDisable(true); //We wait fo the response before enabling
                    setCardParameterMode(CardParameterMode.Disabled, null);
                    HashMap<String, Object> userInfo = new HashMap<>();
                    userInfo.put(NotificationKeys.JavaFXPlayedCharacter.getRawValue(), activeCharacter);
                    userInfo.put(NotificationKeys.CharacterCardDestinationStudent.getRawValue(), notification.getUserInfo().get(NotificationKeys.ClickedStudentColor.getRawValue()));
                    new Thread(() -> NotificationCenter.shared().post(NotificationName.JavaFXDidEndCharacterCardLoop, null, userInfo)).start();
                }
                case StudentSwap -> {
                    this.allowedDropDestinationsForDrag = new StudentDropTarget[0];
                    if (pickedSrcStudentForSwap == null) {
                        pickedSrcStudentForSwap = (Student) notification.getUserInfo().get(NotificationKeys.ClickedStudentColor.getRawValue());
                    } else {
                        //Immediately send the closed loop notification & cleanup
                        cleanupStyles();
                        setDisabled(true);
                        setDisable(true); //We wait fo the response before enabling
                        setCardParameterMode(CardParameterMode.Disabled, null);
                        HashMap<String, Object> userInfo = new HashMap<>();
                        userInfo.put(NotificationKeys.JavaFXPlayedCharacter.getRawValue(), activeCharacter);
                        userInfo.put(NotificationKeys.CharacterCardSourceStudent.getRawValue(), pickedSrcStudentForSwap);
                        userInfo.put(NotificationKeys.CharacterCardDestinationStudent.getRawValue(), notification.getUserInfo().get(NotificationKeys.ClickedStudentColor.getRawValue()));
                        new Thread(() -> NotificationCenter.shared().post(NotificationName.JavaFXDidEndCharacterCardLoop, null, userInfo)).start();
                    }
                }
            }
        }
    }

    /**
     * Enables the pane linked to a {@link it.polimi.ingsw.utils.ui.StudentDropTarget StudentDropTarget}
     * @param dropTarget (type StudentDropTarget) The drop target to highlight
     * @param mutuallyDisablingOtherAreas (type boolean) true if it needs to enable only the target and disable the others
     */
    private void setEnabledWithDropTarget(StudentDropTarget dropTarget, boolean mutuallyDisablingOtherAreas) {
        switch (dropTarget) {
            case ToEntrance -> {
                entranceGrid.setDisable(false);
                if (mutuallyDisablingOtherAreas) {
                    diningGrid.setDisable(true);
                    diningGrid.highlight(false);
                }
                entranceGrid.highlight(true);
            }
            case ToDiningRoom -> {
                if (mutuallyDisablingOtherAreas) {
                    entranceGrid.setDisable(true);
                    entranceGrid.highlight(false);
                }
                diningGrid.setDisable(false);
                diningGrid.highlight(true);
            }
        }
    }

    /**
     * {@code CharacterCardPlayed} callback, intercepts the start of a character card use and adapts the interface accordingly
     * @param notification (type notification) The Character Card Played notification
     */
    private void didReceiveCharacterCardPlayedNotification(Notification notification) {
        if (notification.getUserInfo() != null && isPrimary) {
            this.allowedDropDestinationsForDrag = new StudentDropTarget[0];
            setAllowedStudentDestinationsForPhase(StudentDropTarget.all());
            activeCharacter = (Character) notification.getUserInfo().get(NotificationKeys.JavaFXPlayedCharacter.getRawValue());
            CardParameterMode parameterMode = CardParameterMode.Disabled;
            StudentDropTarget dropTarget = null;
            if (activeCharacter == Character.Circus) {
                parameterMode = CardParameterMode.StudentPicker;
                dropTarget = StudentDropTarget.ToEntrance;
            } else if (activeCharacter == Character.Musician) {
                parameterMode = CardParameterMode.StudentSwap;
                pickedSrcStudentForSwap = null;
            }
            setCardParameterMode(parameterMode, dropTarget);
        }
    }

    /**
     * Sets the board pane mode, disabling/enabling panes and actions accordingly
     * @param newParameterMode (type CardParameterMode) The board pane mode
     * @param studentSource (type StudentDropTarget) The drop target where students can be picked from (for the StudentPicker mode)
     */
    private void setCardParameterMode(CardParameterMode newParameterMode, StudentDropTarget studentSource) {
        this.parameterMode = newParameterMode;
        switch (newParameterMode) {
            case Disabled -> {
                cleanupStyles();
                entranceGrid.getChildren().forEach((node) -> {
                    StudentPane studentPane = (StudentPane) node;
                    studentPane.configureClickForDropTargets(defaultDropDestinationsForEntranceStudents);
                });
                diningGrid.getChildren().forEach((node) -> {
                    StudentPane studentPane = (StudentPane) node;
                    studentPane.configureClickForDropTargets(defaultDropDestinationsForDiningStudents);
                });
                //All behavior will automatically return to the default
            }
            case StudentPicker -> {
                //Highlight the correct area
                entranceGrid.getChildren().forEach((node) -> {
                    StudentPane studentPane = (StudentPane) node;
                    studentPane.configureClickForDropTargets(StudentDropTarget.all());
                });
                diningGrid.getChildren().forEach((node) -> {
                    StudentPane studentPane = (StudentPane) node;
                    studentPane.configureClickForDropTargets(null);
                });
                setEnabledWithDropTarget(studentSource, true);
            }
            case StudentSwap -> {
                //Enable both drop targets - will disable one of them after a click
                setEnabledWithDropTarget(StudentDropTarget.ToEntrance, true);
                //Add additional actions to students so that when we click on them we also disable the source area to have only 1 area active at any given time
                Platform.runLater(() -> {
                    entranceGrid.getChildren().forEach((node) -> {
                        StudentPane studentPane = (StudentPane) node;
                        studentPane.configureClickForDropTargets(new StudentDropTarget[]{StudentDropTarget.ToDiningRoom});
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> setEnabledWithDropTarget(StudentDropTarget.ToDiningRoom, true));
                    });
                    diningGrid.getChildren().forEach((node) -> {
                        StudentPane studentPane = (StudentPane) node;
                        studentPane.configureClickForDropTargets(new StudentDropTarget[]{StudentDropTarget.ToEntrance});
                    });
                });
            }
        }
    }

    /**
     * cleans the style after click
     */
    private void cleanupStyles() {
        entranceGrid.setStyle("");
        diningGrid.setStyle("");
    }

    /**
     * {@code EndStudentMove} callback, used to cleanup the styles after a student move action
     * @param notification (type notification) The Student Move notification
     */
    private void didReceiveEndStudentMoveNotification(Notification notification) {
        this.allowedDropDestinationsForDrag = new StudentDropTarget[0]; //To reset to the initial default state
        cleanupStyles();
    }

    /**
     * {@code CardParameterMode} The list of possible board modes
     */
    private enum CardParameterMode {
        Disabled,
        StudentPicker,
        StudentSwap
    }
}
