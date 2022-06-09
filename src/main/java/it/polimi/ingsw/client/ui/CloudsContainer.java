package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;

import java.lang.reflect.AnnotatedArrayType;
import java.util.ArrayList;
import java.util.List;

public class CloudsContainer extends RescalableAnchorPane{

    private List<CloudPane> clouds;
    private double radius;

    public CloudsContainer(Notification notification) {
        TableStateMessage tableStateMessage = (TableStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
        clouds = new ArrayList<>();
        for(int i = 0; i < tableStateMessage.getManagedClouds().size(); i++) {
            CloudPane cloud = new CloudPane(i, notification);
            clouds.add(cloud);
            getChildren().add(cloud);
        }
        rescale(1);

    }
    protected void layoutChildrenInCircle() {
        if(getChildren().size() == 0) {
            return;
        }
        final  double increment = 360.0 / getChildren().size();
        double degreese = -90;
        for (Node node : getChildren()) {
            double x = radius * Math.cos(Math.toRadians(degreese)) + getWidth() / 2;
            double y = radius * Math.sin(Math.toRadians(degreese)) + getHeight() / 2;
            layoutInArea(node, x - node.getBoundsInLocal().getWidth() / 2, y - node.getBoundsInLocal().getHeight() / 2, getWidth(), getHeight(), 0.0, HPos.LEFT, VPos.TOP);
            degreese += increment;
        }

    }

    public void setActivateCloudPick(boolean activate) {
        setDisable(false);
        for (CloudPane cloud : clouds) {
                cloud.showSelection(activate);
        }
        if(!activate) {
            setDisable(true);
        }
    }

    public void rescale(double scale) {
        setPrefSize( 500 * scale, 500 * scale);
        setLayoutX(GUI.getWindowWidth() - getWidth());
        setLayoutY(0);
        radius = 70 * scale;
        layoutChildrenInCircle();
    }
}
