package it.polimi.ingsw.client.controller.network;

import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.controller.network.messages.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;


public class GameClient {
    
    private static GameClient instance;

    private final int serverPort;
    private final String serverIP;
    private final NetworkMessageDecoder decoder;
    private Socket socket;
    
    private BufferedReader bufferedReader;
    private OutputStreamWriter outputStreamWriter;

    private GameClient(int serverPort, String serverIP) {
        this.serverPort = serverPort;
        this.serverIP = serverIP;
        this.decoder = new NetworkMessageDecoder();
        
        NotificationCenter.shared().addObserver((notification) -> teardown(), NotificationName.ClientDidReceiveMatchTerminationMessage, null);
    }
    
    public static void createClient(String serverIP, int serverPort) {
        if (instance == null) {
            instance = new GameClient(serverPort, serverIP);
        } else {
            System.out.println(StringFormatter.formatWithColor("WARNING: common Client object already initialized with server address " + instance.serverIP + ":" + instance.serverPort, ANSIColors.Yellow));
        }
    }
    
    public static GameClient shared() {
        return instance;
    }

    /**
     * This method opens the socket
     */
    public void connectToServer() throws ConnectException {
        try {
            this.socket = new Socket(serverIP, serverPort);
            this.outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            // Decode the JSON to NetworkMessage
            // The message is wrong - we do nothing
            //TODO: Send an error message (malformed response)
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
        } catch (ConnectException e) {
            throw new ConnectException();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isTerminationMessage(NetworkMessage message) {
        return message instanceof MatchTerminationMessage;
    }

    public synchronized void sendMessage(NetworkMessage message) {
        try {
            outputStreamWriter.write(message.serialize() + "\n");
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method send the notification to the relative listener
     * @param message message
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
        }
    }

    private synchronized void teardown() {
        try {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.shutdownInput();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void terminate() {
        teardown();
    }
}
