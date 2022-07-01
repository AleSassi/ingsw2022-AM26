package it.polimi.ingsw.client.ui.islands;

import it.polimi.ingsw.client.ui.HighlightableGridPane;
import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.ui.GUIUtils;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;

/**
 * Class {@code IslandPane} represent the rescalable pane for an {@link it.polimi.ingsw.server.model.student.Island Island}
 */
public class IslandPane extends RescalableAnchorPane {
    
    private final int idx;

    private final AnchorPane stop;
    private final AnchorPane motherNature;
    private final AnchorPane tower = new AnchorPane();
    private final List<StudentOnIsland> students;
    private final HighlightableGridPane gridPane = new HighlightableGridPane();
    private final Label towerLabel = new Label("0");
    private String address = null;
    private AnchorPane destinationModeInterceptor;
    private final double defaultMNWidth = 30;
    private final double defaultMNHeight = 30;
    private final double defaultTowerWidth = 60;
    private final double defaultTowerHeight = 60;
    private final double defaultLabelFont = 20;
    private final double defaultLabelLayoutX = 100;
    private final double defaultLabelLayoutY = 75;
    private final double defaultTowerLayoutX = 75;
    private final double defaultTowerLayoutY = 65;
    private final double defaultGridWidth = 85;
    private final double defaultGridHeight = 85;
    private final double defaultGridLayoutX = 25;
    private final double defaultGridLayoutY = 25;
    private final double defaultRowConstraints = 85.0;
    private final double defaultColumnConstraints = 85.0;

    private StudentDropTarget[] allowedDropDestinationsForDrag, allowedStudentDestinationsForPhase;

    /**
     * Constructor creates an {@code IslandPane} from an initial table message
     * @param idx (type int) {@link it.polimi.ingsw.server.model.student.Island Island's} index
     * @param notification (type Notification) The initial notification
     */
    public IslandPane(int idx, Notification notification) {
        this.allowedDropDestinationsForDrag = new StudentDropTarget[0];
        this.allowedStudentDestinationsForPhase = new StudentDropTarget[0];
        students = new ArrayList<>();
        setupMouseClickAfterStudentStartMoving(this, StudentDropTarget.ToIsland);

        this.idx = idx;

        Random ran = new Random();
        switch (ran.nextInt(0, 2)) {
            case 0 -> address = "images/islands/island1.png";
            case 1 -> address = "images/islands/island2.png";
            case 2 -> address = "images/islands/island3.png";
        }
        GUIUtils.setStyleWithBackgroundImage(this, address);

        motherNature = GUIUtils.createImageViewWithImageNamed("images/mothernature.png");
        stop = GUIUtils.createImageViewWithImageNamed("images/stop.png");
        Platform.runLater(() -> {
            setVisible(true);
            getChildren().addAll(gridPane, tower, towerLabel);
            towerLabel.setVisible(false);
            tower.setVisible(false);
            gridPane.addColumn(3);
            gridPane.addRow(3);
            motherNature.setVisible(false);
            stop.setVisible(false);
            gridPane.add(motherNature, 1, 2);
            gridPane.add(stop, 0, 2);

        });

        for (Student s : Student.values()) {
            StudentOnIsland studentOnIsland = new StudentOnIsland(s, idx);
            students.add(studentOnIsland);
            switch (s) {
                case BlueUnicorn -> Platform.runLater(() -> gridPane.add(studentOnIsland, 0, 0));
                case RedDragon -> Platform.runLater(() -> gridPane.add(studentOnIsland, 1, 0));
                case GreenFrog -> Platform.runLater(() -> gridPane.add(studentOnIsland, 2, 0));
                case PinkFair -> Platform.runLater(() -> gridPane.add(studentOnIsland, 0, 1));
                case YellowElf -> Platform.runLater(() -> gridPane.add(studentOnIsland, 1, 1));
            }
            studentOnIsland.didReceiveTableState(notification);
        }
        rescale(getCurrentScaleValue());
        NotificationCenter.shared().addObserver(this, this::didReceiveTableState, NotificationName.ClientDidReceiveTableStateMessage, null);
        NotificationCenter.shared().addObserver(this, this::didReceiveStartStudentMoveNotification, NotificationName.JavaFXDidStartMovingStudent, null);
        NotificationCenter.shared().addObserver(this, this::didReceiveEndStudentMoveNotification, NotificationName.JavaFXDidEndMovingStudent, null);
    }
    
    @Override
    public double getUnscaledWidth() {
        return 50;
    }
    
    @Override
    public double getUnscaledHeight() {
        return 50;
    }
    
    /**
     * Sets the allowed {@link it.polimi.ingsw.server.model.student.Student Student's} destination relative to the current {@link it.polimi.ingsw.server.model.match.MatchPhase MatchPhase}
     * @param allowedStudentDestinationsForPhase (type StudentDropTarger[]) allowed {@code Student's} destinations for current {@code MatchPhase}
     */
    public void setAllowedStudentDestinationsForPhase(StudentDropTarget[] allowedStudentDestinationsForPhase) {
        this.allowedStudentDestinationsForPhase = allowedStudentDestinationsForPhase;
    }

    /**
     * Sets the MouseClick event
     * @param targetPane (type Pane) origin target
     * @param dropTarget (type Pane) destionation target
     */
    private void setupMouseClickAfterStudentStartMoving(Pane targetPane, StudentDropTarget dropTarget) {
        targetPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            for (StudentDropTarget allowedDropTarget : allowedDropDestinationsForDrag) {
                if (Arrays.stream(allowedDropDestinationsForDrag).toList().contains(dropTarget)) {
                    //Action registered successfully - post notification
                    HashMap<String, Object> userInfo = new HashMap<>();
                    userInfo.put("dropTarget", dropTarget);
                    userInfo.put("targetIslandIndex", idx);
                    NotificationCenter.shared().post(NotificationName.JavaFXDidEndMovingStudent, null, userInfo);
                    break;
                }
            }
            event.consume();
        });

    }

    /**
     * Deletes the {@code IslandPnae}
     */
    protected void deleteIsland() {
        new Thread(() -> NotificationCenter.shared().removeObserver(this)).start();
        for (StudentOnIsland s: students) {
            s.deleteStudent();
        }
    }

    /**
     * Sets the {@link it.polimi.ingsw.client.ui.characters.CharacterCardPane CharacterCardPane} to destination mode for Character cards
     * @param isCardDestinationMode (type boolean) true if needs to be set to destination mode
     * @param sourceNotificationInfo (type HashMap(String, Object) {@code sourceNotificationInfo} The user info from the source notification
     */
    protected void setCardDestinationMode(boolean isCardDestinationMode, HashMap<String, Object> sourceNotificationInfo) {
        //We use the sourceNotificationInfo param to forward it when we close the control loop
        setActiveBackground(isCardDestinationMode);
        if (isCardDestinationMode && destinationModeInterceptor == null) {
            destinationModeInterceptor = new AnchorPane();
            destinationModeInterceptor.setPickOnBounds(true);
            AnchorPane.setBottomAnchor(destinationModeInterceptor, 0.0);
            AnchorPane.setTopAnchor(destinationModeInterceptor, 0.0);
            AnchorPane.setLeftAnchor(destinationModeInterceptor, 0.0);
            AnchorPane.setRightAnchor(destinationModeInterceptor, 0.0);
            //Add an event handler for click on empty areas
            destinationModeInterceptor.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                event.consume();
                Platform.runLater(() -> {
                    getChildren().remove(destinationModeInterceptor);
                    destinationModeInterceptor = null;
                });
                //Send the CardEndControlLoop notification
                sourceNotificationInfo.put(NotificationKeys.CharacterCardTargetIslandIndex.getRawValue(), idx);
                NotificationCenter.shared().post(NotificationName.JavaFXDidEndCharacterCardLoop, null, sourceNotificationInfo);
            });
            Platform.runLater(() -> getChildren().add(destinationModeInterceptor));
        } else {
            Platform.runLater(() -> {
                getChildren().remove(destinationModeInterceptor);
                destinationModeInterceptor = null;
            });
        }
    }

    /**
     * Student movement start callback
     * @param notification (type Notification) The movement notification
     */
    private void didReceiveStartStudentMoveNotification(Notification notification) {
        List<StudentDropTarget> allowableDefaultMovements = Arrays.stream(allowedStudentDestinationsForPhase).toList();
        List<StudentDropTarget> dropDestinationsForDrag = Arrays.stream(((StudentDropTarget[]) notification.getUserInfo().get(NotificationKeys.StudentDropTargets.getRawValue()))).toList();
        this.allowedDropDestinationsForDrag = dropDestinationsForDrag.stream().filter(allowableDefaultMovements::contains).toList().toArray(new StudentDropTarget[0]);
        //Highlight areas that can be a target for the operation
        if (Arrays.stream(allowedDropDestinationsForDrag).toList().contains(StudentDropTarget.ToIsland)) {
            setActiveBackground(true);
        }
    }

    /**
     * Sets the background to the active or inactive style
     * @param active (type boolean) true if it needs to be activated
     */
    private void setActiveBackground(boolean active) {
        if (active) {
            gridPane.highlight(true);
        } else {
            gridPane.highlight(false);
            GUIUtils.setStyleWithBackgroundImage(this, address);
        }
    }

    /**
     * Student movement end callback, used to remove the Active appearance
     * @param notification (type Notification) Th estudent movement notification
     */
    private void didReceiveEndStudentMoveNotification(Notification notification) {
        this.allowedDropDestinationsForDrag = new StudentDropTarget[0]; //To reset to the initial default state
        setActiveBackground(false);
    }

    /**
     * {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage TableStateMessage} received callback, used to update the island state
     * @param notification (type Notification) The notification for the table message
     */
    public void didReceiveTableState(Notification notification) {
        if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof TableStateMessage message) {
            if (message.getIslands().size() - 1 >= idx) {
                setTowerOnIsland(message);
                setMotherNature(message);
                setStop(message);
            }
            //Clear the style
            setActiveBackground(false);
        }
    }

    /**
     * Sets the stop card on this {@code IslandPane}
     * @param tableStateMessage (type tableStateMessage) The table state message used to configure the stop card
     */
    private void setStop(TableStateMessage tableStateMessage) {
        stop.setVisible(tableStateMessage.getIslands().get(idx).itHasStopCard());
    }

    /**
     * Sets the mother nature on this {@code IslandPane}
     * @param tableStateMessage (type tableStateMessage) The table state message used to configure Mother Nature
     */
    private void setMotherNature(TableStateMessage tableStateMessage) {
        motherNature.setVisible(tableStateMessage.getIslands().get(idx).isMotherNaturePresent());
    }

    /**
     * Sets the {@link it.polimi.ingsw.server.model.Tower Towers} on this {@code IslandPane}
     * @param tableStateMessage (type tableStateMessage) The table state message used to configure the towers
     */
    private void setTowerOnIsland(TableStateMessage tableStateMessage) {
        if (tower != null) {
            if (tableStateMessage.getIslands().get(idx).getActiveTowerType() == null) {
                //Remove the tower!
                Platform.runLater(() -> {
                    tower.setVisible(false);
                    towerLabel.setVisible(false);
                    towerLabel.setText("0");
                });
            } else {
                switch (tableStateMessage.getIslands().get(idx).getActiveTowerType()) {
                    case Gray -> {
                        tower.setVisible(true);
                        towerLabel.setTextFill(Color.color(0, 0, 0));
                        tower.setStyle("-fx-background-image: url(" + GUIUtils.getURI("images/towers/Grey.png") + ");\n-fx-background-size: 100% 100%");
                    }
                    case Black -> {
                        tower.setVisible(true);
                        towerLabel.setTextFill(Color.color(1, 1, 1));
                        tower.setStyle("-fx-background-image: url(" + GUIUtils.getURI("images/towers/Black.png") + ");\n-fx-background-size: 100% 100%");
                    }
                    case White -> {
                        tower.setVisible(true);
                        towerLabel.setTextFill(Color.color(0, 0, 0));
                        tower.setStyle("-fx-background-image: url(" + GUIUtils.getURI("images/towers/White.png") + ");\n-fx-background-size: 100% 100%");
                    }
                    default -> {
                        return;
                    }
                }
                Platform.runLater(() -> {
                    int count = tableStateMessage.getIslands().get(idx).getTowerCount();
                    towerLabel.setVisible(count > 1);
                    towerLabel.setText("" + count);
                });
            }
        }
    }
    
    @Override
    public void rescale(double scale) {
        //rescale IslandPane
        setPrefSize(getUnscaledWidth() * scale, getUnscaledHeight() * scale);
    
        //rescale MN
        motherNature.setPrefSize(defaultMNWidth * scale, defaultMNHeight * scale);
    
        //rescale Tower
        if (tower != null) {
            tower.setPrefSize(defaultTowerWidth * scale, defaultTowerHeight * scale);
            towerLabel.setFont(new Font(defaultLabelFont * scale));
            towerLabel.setLayoutX(defaultLabelLayoutX * scale);
            towerLabel.setLayoutY(defaultLabelLayoutY * scale);
            tower.setLayoutX(defaultTowerLayoutX * scale);
            tower.setLayoutY(defaultTowerLayoutY * scale);
        }
    
        //rescale Grid && Columns and Rows
        gridPane.getRowConstraints().removeAll(gridPane.getRowConstraints());
        gridPane.getColumnConstraints().removeAll(gridPane.getColumnConstraints());
        gridPane.setPrefSize(defaultGridWidth * scale, defaultGridHeight * scale);
        gridPane.setLayoutX(defaultGridLayoutX * scale);
        gridPane.setLayoutY(defaultGridLayoutY * scale);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints(defaultRowConstraints / 3 * scale);
            gridPane.getColumnConstraints().add(col);
        }
        for (int i = 0; i < 3; i++) {
            RowConstraints row = new RowConstraints(defaultRowConstraints / 3 * scale);
            gridPane.getRowConstraints().add(row);
        }
    }


}

