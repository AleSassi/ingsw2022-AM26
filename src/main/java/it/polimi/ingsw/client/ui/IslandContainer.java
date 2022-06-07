package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;

import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class IslandContainer extends RescalableAnchorPane{
    private List<IslandPane> islandsPane = new ArrayList<>();
    private double radius = 250;

    public IslandContainer(Notification notification) {
        for(int i = 0; i < 12; i++) {
            IslandPane island = new IslandPane(i, notification);
            islandsPane.add(island);
            Platform.runLater(() -> getChildren().add(island));
        }

        rescale(1);
        NotificationCenter.shared().addObserver(this::didReceiveTableState, NotificationName.ClientDidReceiveTableStateMessage, null);
        NotificationCenter.shared().addObserver(this::didReceiveEndStudentMoveNotification, NotificationName.JavaFXDidEndMovingStudent, null);

    }

    private void didReceiveEndStudentMoveNotification(Notification notification) {
    }

    private void didReceiveTableState(Notification notification) {
        TableStateMessage tableStateMessage = (TableStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
        Platform.runLater(() ->  {
            while(getChildren().size() > tableStateMessage.getIslands().size()) {
                getChildren().remove(getChildren().size() - 1);
            }

        });
    }

    public void setAllowedStudentMovements(StudentDropTarget[] validStudentDestinations) {
        for (IslandPane island : islandsPane) {
            island.setAllowedStudentDestinationsForPhase(validStudentDestinations);
        }
    }

    public void layoutChildrenInCircle() {
        if(getChildren().size() == 0) {
            return;
        }
        final double increment = 360.0 / getChildren().size();
        double degreese = -90;
        for (Node node : islandsPane) {
            double x = radius * Math.cos(Math.toRadians(degreese)) + getWidth() / 2;
            double y = radius * Math.sin(Math.toRadians(degreese)) + getHeight() / 2;
            layoutInArea(node, x - node.getBoundsInLocal().getWidth() / 2, y - node.getBoundsInLocal().getHeight() / 2, getWidth(), getHeight(), 0.0, HPos.LEFT, VPos.TOP);
            degreese += increment;
        }
    }

    public void rescale(double scale) {
        setPrefSize(500 * scale, 500 * scale);
        setLayoutX((1300*scale)-getWidth());
        setLayoutY(0);
        radius = 250 * scale;
        layoutChildrenInCircle();
    }


}
