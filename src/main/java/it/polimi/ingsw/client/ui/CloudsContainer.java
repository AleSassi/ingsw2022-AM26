package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Class {@code CloudsContainer} represent the JavaFX controller for the {@link it.polimi.ingsw.client.ui.CloudPane CloudPanes} container
 */
public class CloudsContainer extends RescalableAnchorPane {

    private final List<CloudPane> clouds;

    /**
     * Constructor creates a new {@code CloudsContainer}
     * @param notification (type Notification)
     */
    public CloudsContainer(Notification notification) {
        TableStateMessage tableStateMessage = (TableStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
        clouds = new ArrayList<>();
        for(int i = 0; i < tableStateMessage.getManagedClouds().size(); i++) {
            CloudPane cloud = new CloudPane(i, notification);
            clouds.add(cloud);
            getChildren().add(cloud);
        }
        rescale(getCurrentScaleValue());
    }

    /**
     * Shows the selectables {@link it.polimi.ingsw.client.ui.CloudPane CloudPane}
     * @param activate (type boolean) true if needs to show the selectables {@code CloudPane}
     */
    public void setActivateCloudPick(boolean activate) {
        if (activate) {
            setDisable(false);
            for (CloudPane c: clouds) {
                c.showSelection();
            }
        } else {
            setDisable(true);
        }
    }

    public void rescale(double scale) {
        setPrefSize(70 * scale, 70 * scale);
        double radius = 70 * scale;
    
        if (clouds.size() == 0) {
            return;
        }
        double dtheta = 2 * Math.PI / clouds.size();
        double radians = 0;
        for (CloudPane cloud: clouds) {
            double x = radius * Math.cos(radians) + radius;
            double y = radius * Math.sin(radians) + radius;
            cloud.setLayoutX(x);
            cloud.setLayoutY(y);
            radians += dtheta;
        }
    }
}
