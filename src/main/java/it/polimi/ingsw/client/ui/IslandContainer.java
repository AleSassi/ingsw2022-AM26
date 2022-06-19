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

    /**
     * Initialize the IslandContainer with the islands
     * @param notification
     */
    public IslandContainer(Notification notification) {
        for(int i = 0; i < 12; i++) {
            IslandPane island = new IslandPane(i, notification);
            islandsPane.add(island);
            Platform.runLater(() -> getChildren().add(island));
        }

        rescale(1);

        NotificationCenter.shared().addObserver(this, this::didReceiveTableState, NotificationName.ClientDidReceiveTableStateMessage, null);

    }


    /**
     * TableState notification callback, updates the number of island from the TableState notification
     * @param notification
     */
    private void didReceiveTableState(Notification notification) {
        TableStateMessage tableStateMessage = (TableStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
        Platform.runLater(() ->  {
            while(getChildren().size() > tableStateMessage.getIslands().size()) {
                islandsPane.get(islandsPane.size() - 1).deleteIsland();
                getChildren().remove(getChildren().size() - 1);
                islandsPane.remove(islandsPane.size() - 1);
            }
            rescale(1);
        });
    }

    public void setAllowedStudentMovements(StudentDropTarget[] validStudentDestinations) {
        for (IslandPane island : islandsPane) {
            island.setAllowedStudentDestinationsForPhase(validStudentDestinations);
        }
    }

    public void rescale(double scale) {
        setPrefSize(500 * scale, 500 * scale);
        setLayoutX(GUI.getWindowWidth() - (500 * scale));
        setLayoutY(0);
        radius = 250 * scale;

        if (islandsPane.size() == 0) {
            return;
        }
        double dtheta = 2 * Math.PI / islandsPane.size();
        double radians = 0;
        for (IslandPane islandPane: islandsPane) {
            double x = radius * Math.cos(radians) + radius;
            double y = radius * Math.sin(radians) + radius;
            islandPane.setLayoutX(x);
            islandPane.setLayoutY(y);
            radians += dtheta;
        }
    }


}
