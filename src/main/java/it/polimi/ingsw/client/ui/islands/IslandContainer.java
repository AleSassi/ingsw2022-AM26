package it.polimi.ingsw.client.ui.islands;

import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;

import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * Class {@code IslandContainer} is the javaFX container for the Islands
 * It manages their position in the main view and their arrangement in circles, and updates the number of visible islands depending on the table state
 * @author Federico Albertini
 */
public class IslandContainer extends RescalableAnchorPane {
    
    private final List<IslandPane> islands = new ArrayList<>();
    
    /**
     * Initialize the {@code IslandContainer} and its children {@link it.polimi.ingsw.client.ui.islands.IslandPane} after an initial table state notification
     * @param notification (type Notification) The table state notification used to initialize
     */
    public IslandContainer(Notification notification) {
        for(int i = 0; i < 12; i++) {
            IslandPane island = new IslandPane(i, notification);
            islands.add(island);
            Platform.runLater(() -> getChildren().add(island));
        }

        rescale(getCurrentScaleValue());

        NotificationCenter.shared().addObserver(this, this::didReceiveTableState, NotificationName.ClientDidReceiveTableStateMessage, null);
        NotificationCenter.shared().addObserver(this, this::didReceiveCharacterCardPlayedNotification, NotificationName.JavaFXDidPlayCharacterCard, null);
        NotificationCenter.shared().addObserver(this, this::cleanupAfterCardControlLoopEnds, NotificationName.JavaFXDidEndCharacterCardLoop, null);
    }
    
    @Override
    public double getUnscaledWidth() {
        return 500;
    }
    
    @Override
    public double getUnscaledHeight() {
        return 500;
    }
    
    /**
     * {@code TableState} notification callback, updates the number of {@link it.polimi.ingsw.client.ui.islands.IslandPane} from the TableState notification
     * @param notification (type Notification) The table state notification
     */
    private void didReceiveTableState(Notification notification) {
        TableStateMessage tableStateMessage = (TableStateMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
        Platform.runLater(() ->  {
            while(getChildren().size() > tableStateMessage.getIslands().size()) {
                islands.get(islands.size() - 1).deleteIsland();
                getChildren().remove(getChildren().size() - 1);
                islands.remove(islands.size() - 1);
            }
            rescale(getCurrentScaleValue());
        });
    }

    /**
     * {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard} played notification callback, use dto update the destination state of islands
     * @param notification (type Notification) The character card play notification
     */
    private void didReceiveCharacterCardPlayedNotification(Notification notification) {
        if (notification.getUserInfo() != null) {
            Character playedCharacter = (Character) notification.getUserInfo().get(NotificationKeys.JavaFXPlayedCharacter.getRawValue());
            if (playedCharacter == Character.Abbot || playedCharacter == Character.Ambassador || playedCharacter == Character.Herbalist) {
                //Highlight islands which enter "Destination mode" -> every click on them will send the CloseControlLoop message
                for (IslandPane islandPane: islands) {
                    islandPane.setCardDestinationMode(true, notification.getUserInfo());
                }
            }
        }
    }

    /**
     * Cleans up the pane after the {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard} control loop ends
     * @param notification (type Notification) The character card end playing notification
     */
    private void cleanupAfterCardControlLoopEnds(Notification notification) {
        for (IslandPane islandPane: islands) {
            islandPane.setCardDestinationMode(false, notification.getUserInfo());
        }
    }

    /**
     * Sets the allowed {@link it.polimi.ingsw.server.model.student.Student Student} movement for the Islands
     * @param validStudentDestinations (type StudentDropTarget[]) The valid student destinations
     */
    public void setAllowedStudentMovements(StudentDropTarget[] validStudentDestinations) {
        for (IslandPane island : islands) {
            island.setAllowedStudentDestinationsForPhase(validStudentDestinations);
        }
    }
    
    @Override
    public void rescale(double scale) {
        setPrefSize(getUnscaledWidth() * scale, getUnscaledHeight() * scale);
        setLayoutX(GUI.getWindowWidth() - (getUnscaledWidth() * scale));
        setLayoutY(0);
        double radius = getUnscaledWidth() * 0.5 * scale;

        if (islands.size() == 0) {
            return;
        }
        double dtheta = 2 * Math.PI / islands.size();
        double radians = 0;
        for (IslandPane islandPane: islands) {
            double x = radius * Math.cos(radians) + radius;
            double y = radius * Math.sin(radians) + radius;
            islandPane.setLayoutX(x);
            islandPane.setLayoutY(y);
            radians += dtheta;
        }
    }


}
