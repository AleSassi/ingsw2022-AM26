package it.polimi.ingsw.client.controller.network;

import it.polimi.ingsw.server.controller.network.GameServer;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.controller.network.messages.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The main Client-side class handling network communication with the Server
 *
 * This class wraps around the <code>Socket</code> connected with the Server to provide easy access to methods such as sending messages. It supports async message reception and handles errors in case of network timeouts or match termination
 * @author Alessandro Sassi
 */
public class GameClient {
    
    private static GameClient instance;

    private final int serverPort;
    private final String serverIP;
    private final NetworkMessageDecoder decoder;
    private Socket socket;
    private Timer pongTimer;
    private TimerTask pongTimerTask;
    private final boolean isGUI;
    
    private BufferedReader bufferedReader;
    private OutputStreamWriter outputStreamWriter;
    
    /**
     * Initialize the Client instance with netwrok parameters and the UI mode
     * @param serverPort (type int) The port on the server open to accepting connections with the client
     * @param serverIP (type String) The server IP address
     * @param isGUI (type boolean) Whether the client is in GUI mode or CLI
     */
    private GameClient(int serverPort, String serverIP, boolean isGUI) {
        this.serverPort = serverPort;
        this.serverIP = serverIP;
        this.decoder = new NetworkMessageDecoder();
        this.isGUI = isGUI;
        
        NotificationCenter.shared().addObserver(this, (notification) -> teardown(), NotificationName.ClientDidReceiveMatchTerminationMessage, null);
    }
    
    /**
     * Static method used to create a GameClient object, if such object does not already exist
     * @param serverPort (type int) The port on the server open to accepting connections with the client
     * @param serverIP (type String) The server IP address
     * @param isGUI (type boolean) Whether the client is in GUI mode or CLI
     */
    public static void createClient(String serverIP, int serverPort, boolean isGUI) {
        if (instance == null) {
            instance = new GameClient(serverPort, serverIP, isGUI);
        } else {
            System.out.println(StringFormatter.formatWithColor("WARNING: common Client object already initialized with server address " + instance.serverIP + ":" + instance.serverPort, ANSIColors.Yellow));
        }
    }
    
    /**
     * Accesses the Singleton instance of the client
     * @return The singleton client instance
     */
    public static GameClient shared() {
        return instance;
    }

    /**
     * This method opens the socket with the server (if not already opened and active) and starts the Thread used to asynchronously read from the Socket
     * @throws IOException When the default error handling behavior, which closes the socket and deallocates internal objects, fails
     */
    public void connectToServer() throws IOException {
        if (this.socket != null && this.socket.isConnected() && !this.socket.isClosed()) return;
        this.socket = new Socket(serverIP, serverPort);
        this.outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        // Decode the JSON to NetworkMessage
        // The message is wrong - we do nothing
        Thread readThread = new Thread(() -> {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    String json = bufferedReader.readLine();
                
                    if (json != null && !json.isEmpty() && !json.isBlank()) {
                        // Decode the JSON to NetworkMessage
                        try {
                            NetworkMessage message = decoder.decodeMessage(json);
                            didReceiveMessage(message);
                            if (isTerminationMessage(message)) {
                                break;
                            }
                        } catch (MessageDecodeException e) {
                            // The message is wrong - we do nothing
                            //TODO: Send an error message (malformed response)
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                try {
                    bufferedReader.close();
                    if (outputStreamWriter != null) {
                        outputStreamWriter.close();
                    }
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                    bufferedReader = null;
                    outputStreamWriter = null;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        readThread.start();
    }
    
    /**
     * Starts a Timer for intercepting disconnections with the Server on the Client side.
     * This allows the client to be notified when the network connection is down and the server cannot send the termination message
     */
    public void startPongTimeoutTimer() {
        if (pongTimer == null) {
            pongTimer = new Timer("PongTimer");
        }
        int pingDelayMS = GameServer.getPingDelayMS();
        int pingIntervalMS = GameServer.getPingIntervalMS();
        int pingDelayTolerance = 2000;
        pongTimerTask = new TimerTask() {
            @Override
            public void run() {
                NotificationCenter.shared().post(NotificationName.ClientDidTimeoutNetwork, null, null);
                if (!isGUI) {
                    System.exit(0);
                }
            }
        };
        pongTimer.schedule(pongTimerTask, pingDelayMS + pingIntervalMS + pingDelayTolerance);
    }
    
    /**
     * Checks if a network message signals termination
     * @param message The network message to check
     * @return Whether a network message signals termination
     */
    private boolean isTerminationMessage(NetworkMessage message) {
        return message instanceof MatchTerminationMessage;
    }
    
    /**
     * Sends the message to the Server synchronously using the TCP Socket
     * @param message The message to send
     */
    public synchronized void sendMessage(NetworkMessage message) {
        try {
            outputStreamWriter.write(message.serialize() + "\n");
            outputStreamWriter.flush();
        } catch (SocketException e) {
            // Broken pipe - client disconnected
            new Thread(() -> didReceiveMessage(new MatchTerminationMessage("Cannot connect to the Server", false))).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dispatches an appropriate notification depending on the received message type
     * @param message The received network message
     */
    private synchronized void didReceiveMessage(NetworkMessage message) {
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), message);
        NotificationName clientNotificationName = message.clientReceivedMessageNotification();
        if (clientNotificationName != null) {
            NotificationCenter.shared().post(clientNotificationName, null, userInfo);
        } else if (message instanceof PingPongMessage) {
            PingPongMessage pong = new PingPongMessage(false);
            sendMessage(pong);
            if (pongTimer != null) {
                pongTimerTask.cancel();
            }
            startPongTimeoutTimer();
        }
    }
    
    /**
     * Closes the connection with the server
     */
    public synchronized void teardown() {
        try {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.shutdownInput();
                socket.close();
            }
            if (pongTimer != null) {
                pongTimerTask.cancel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Tears down the connection with the server and kills the program
     */
    public void terminate() {
        teardown();
        System.exit(0);
    }
}
