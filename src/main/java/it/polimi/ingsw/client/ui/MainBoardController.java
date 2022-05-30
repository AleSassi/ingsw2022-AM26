package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    
    public void load() {
        schoolBoardContainers = new ArrayList<>();
        SchoolBoardContainer primaryContainer = new SchoolBoardContainer(true, Client.getNickname());
        primaryContainer.setLayoutX(0);
        primaryContainer.setLayoutY(0);
        Platform.runLater(() -> mainPane.getChildren().add(primaryContainer));
        schoolBoardContainers.add(primaryContainer);
        NotificationCenter.shared().addObserver(this::didReceivePlayerStatusNotification, NotificationName.ClientDidReceivePlayerStateMessage, null);
        NotificationCenter.shared().addObserver(this::didReceiveWindowDidResizeNotification, NotificationName.JavaFXWindowDidResize, null);
    }
    
    protected void didReceivePlayerStatusNotification(Notification notification) {
        if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof PlayerStateMessage message) {
            if (!schoolBoardContainers.stream().map(SchoolBoardContainer::getOwnerNickname).toList().contains(message.getNickname())) {
                //Create the new container
                SchoolBoardContainer newContainer = new SchoolBoardContainer(false, message.getNickname());
                Platform.runLater(() -> mainPane.getChildren().add(newContainer));
                schoolBoardContainers.add(0, newContainer);
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
