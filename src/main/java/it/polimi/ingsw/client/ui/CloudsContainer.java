package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Class {@code CloudsContainer} represent the JavaFX container for the {@link it.polimi.ingsw.client.ui.CloudPane CloudPanes}, managing their position
 */
public class CloudsContainer extends RescalableAnchorPane {

    private final List<CloudPane> clouds;

    /**
     * Constructor creates a new {@code CloudsContainer} from a Table message
     * @param notification (type Notification) The Table STate notification
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
        NotificationCenter.shared().addObserver(this, this::didReceivePlayedCharacterCardNotification, NotificationName.JavaFXDidPlayCharacterCard, null);
    }
    
    @Override
    public double getUnscaledWidth() {
        return 70;
    }
    
    @Override
    public double getUnscaledHeight() {
        return 70;
    }
    
    /**
     * Sets the child {@link it.polimi.ingsw.client.ui.CloudPane CloudPane}s as selectable, highlighting and enabling them for clicks
     * @param activate (type boolean) true if it needs to show the {@code CloudPane}s as selectable
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
    
    /**
     * Intercepts the {@link it.polimi.ingsw.notifications.Notification notification} thrown when a Card is activated, so that it can clean up the style of the clouds (i.e. disable them) in order to allow the Character Card to be played
     * @param notification The {@link it.polimi.ingsw.notifications.Notification notification} thrown when a Character card is activated
     */
    private void didReceivePlayedCharacterCardNotification(Notification notification) {
        setActivateCloudPick(false);
    }
    
    @Override
    public void rescale(double scale) {
        setPrefSize(getUnscaledWidth() * scale, getUnscaledHeight() * scale);
        double radius = getUnscaledWidth() * scale;
    
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
