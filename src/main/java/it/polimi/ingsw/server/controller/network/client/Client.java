package it.polimi.ingsw.server.controller.network.client;

import it.polimi.ingsw.server.controller.notifications.NotificationCenter;
import it.polimi.ingsw.server.controller.notifications.NotificationKeys;
import it.polimi.ingsw.server.controller.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.controller.network.messages.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;


public class Client {

    private final int serverPort;
    private final String ip;
    private final NetworkMessageDecoder decoder;
    private Socket socket;

    private BufferedReader bufferedReader;
    private OutputStreamWriter outputStreamWriter;

    private Client(int serverPort, String ip) {
        this.serverPort = serverPort;
        this.ip = ip;
        this.decoder = new NetworkMessageDecoder();
    }

    /**
     * This method opens the socket
     */
    public void connectToServer() {
        try {
            this.socket = new Socket(ip, serverPort);
            Thread thread = new Thread(() -> {
                StringBuilder sb = new StringBuilder();
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line).append(System.lineSeparator());
                        }
                        String json = sb.toString();
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
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isTerminationMessage(NetworkMessage message) {
        return message instanceof MatchTerminationMessage;
    }

    public void sendMessage(NetworkMessage message) {
        try {
            if (outputStreamWriter == null) {
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            }
            outputStreamWriter.write(message.serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method send the notification to the relative listener
     * @param message message
     */
    private void didReceiveMessage(NetworkMessage message) {
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

    public void teardown() {
        try {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            socket.shutdownInput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
