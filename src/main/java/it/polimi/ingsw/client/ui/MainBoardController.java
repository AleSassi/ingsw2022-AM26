package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainBoardController implements JavaFXRescalable {

    @FXML private AnchorPane mainPane;
    
    private List<SchoolBoardContainer> schoolBoardContainers;
    private Student movingStudentColor;
    private boolean isActive = false;
    
    public void load() {
        schoolBoardContainers = new ArrayList<>();
        NotificationCenter.shared().addObserver(this::didReceiveWindowDidResizeNotification, NotificationName.JavaFXWindowDidResize, null);
        NotificationCenter.shared().addObserver(this::didReceivePlayerStatusNotification, NotificationName.ClientDidReceivePlayerStateMessage, null);
        NotificationCenter.shared().addObserver(this::didReceiveActivePlayerMessage, NotificationName.ClientDidReceiveActivePlayerMessage, null);
        NotificationCenter.shared().addObserver(this::didReceiveMatchStateMessage, NotificationName.ClientDidReceiveMatchStateMessage, null);
        NotificationCenter.shared().addObserver(this::didReceivePlayerActionResponse, NotificationName.ClientDidReceivePlayerActionResponse, null);
        NotificationCenter.shared().addObserver(this::didReceiveStudentMovementStart, NotificationName.JavaFXDidStartMovingStudent, null);
        NotificationCenter.shared().addObserver(this::didReceiveStudentMovementEnd, NotificationName.JavaFXDidEndMovingStudent, null);
    }
    
    protected void didReceivePlayerStatusNotification(Notification notification) {
        if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof PlayerStateMessage message) {
            if (!schoolBoardContainers.stream().map(SchoolBoardContainer::getOwnerNickname).toList().contains(message.getNickname())) {
                //Create the new container
                SchoolBoardContainer newContainer = new SchoolBoardContainer(message.getNickname().equals(Client.getNickname()), message.getNickname());
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
        if (notification.getUserInfo() != null) {
            if (notification.getUserInfo().containsKey("newWidth")) {
                // When we resize the width dimension, the container should not change (stays anchored to the left side of the window)
            } else if (notification.getUserInfo().containsKey("newHeight")) {
                // When we resize the height dimension, the container should rescale to fit into the container
                double newHeight = ((Number) notification.getUserInfo().get("newHeight")).doubleValue();
                double heightScale = newHeight / GUI.referenceHeight;
                double widthScale = GUI.getWindowWidth() / GUI.referenceWidth;
                double scale = Math.min(widthScale, heightScale);
                rescale(scale);
            }
        }
    }
    
    protected void didReceiveTableStateMessage(Notification notification) {
        //TODO: Forward to islands and Clouds
    }
    
    protected void didReceiveActivePlayerMessage(Notification notification) {
        if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof ActivePlayerMessage message) {
            isActive = message.getActiveNickname().equals(Client.getNickname());
            schoolBoardContainers.get(schoolBoardContainers.size() - 1).setActive(isActive);
        }
    }
    
    protected void didReceiveMatchStateMessage(Notification notification) {
        if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof MatchStateMessage message && isActive) {
            // Update the list of allowed moves
            switch (message.getCurrentMatchPhase()) {
                case PlanPhaseStepTwo -> {
                    // Allow only assistant card choice
                    schoolBoardContainers.get(schoolBoardContainers.size() - 1).setAllowedStudentMovements(new StudentDropTarget[0]);
                    showAssistantCardModalWindow();
                }
                case ActionPhaseStepOne -> {
                    // Allow student movement from entrance to everywhere, and allow character card purchase and play (if applicable)
                    schoolBoardContainers.get(schoolBoardContainers.size() - 1).setAllowedStudentMovements(StudentDropTarget.all());
                }
                case ActionPhaseStepTwo -> {
                    // Disable everything, present a popup to choose the number of steps MN must move by
                    schoolBoardContainers.get(schoolBoardContainers.size() - 1).setAllowedStudentMovements(new StudentDropTarget[0]);
                }
                case ActionPhaseStepThree -> {
                    // Disable everything except the Cloud tiles, when clicking on a Cloud tile send the event
                    schoolBoardContainers.get(schoolBoardContainers.size() - 1).setAllowedStudentMovements(new StudentDropTarget[0]);
                }
            }
        }
    }
    
    private void showAssistantCardModalWindow() {
        Platform.runLater(() -> {
            TextInputDialog td = new TextInputDialog("Assistant card index...");
            td.setHeaderText("Enter the assistant card index");
            td.showAndWait().ifPresentOrElse((text) -> {
                try {
                    int number = Integer.parseInt(text);
                    //Send the player action
                    PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPlayAssistantCard, number, null, false, -1, -1, -1, -1, null);
                    GameClient.shared().sendMessage(actionMessage);
                } catch (NumberFormatException e) {
                    this.showAssistantCardModalWindow();
                }
            }, this::showAssistantCardModalWindow);
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
            case ToEntrance -> {
                //TODO: Send a CharacterCardUse message with the target student
            }
            case ToDiningRoom -> {
                PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidMoveStudent, -1, movingStudentColor, false, -1, -1, -1, -1, null);
                GameClient.shared().sendMessage(actionMessage);
            }
            case ToIsland -> {
                int islandIndex = (int) notification.getUserInfo().get("targetIslandIndex");
                PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidMoveStudent, -1, movingStudentColor, true, islandIndex, -1, -1, -1, null);
                GameClient.shared().sendMessage(actionMessage);
            }
            case ToCharacterCard -> {
                //TODO: Send a CharacterCardUse message
            }
        }
    }
    
    private void didReceivePlayerActionResponse(Notification notification) {
        PlayerActionResponse response = (PlayerActionResponse) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
        if (!response.isActionSuccess()) {
            //Alert the user that the action was cancelled
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Invalid move", ButtonType.CLOSE);
            errorAlert.setContentText(response.getDescriptiveErrorMessage());
            Platform.runLater(errorAlert::show);
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
    }
}
