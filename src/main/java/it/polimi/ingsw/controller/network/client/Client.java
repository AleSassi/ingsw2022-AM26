package it.polimi.ingsw.controller.network.client;

import it.polimi.ingsw.controller.network.messages.*;
import it.polimi.ingsw.controller.notifications.NotificationCenter;
import it.polimi.ingsw.controller.notifications.NotificationKeys;
import it.polimi.ingsw.controller.notifications.NotificationName;
import it.polimi.ingsw.exceptions.model.MessageDecodeException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;


public class Client {

    private int serverPort;
    private String ip;
    private NetworkMessageDecoder decoder;
    private Socket socket;

    private BufferedReader bufferedReader;
    private OutputStreamWriter outputStreamWriter;

    private Client(int serverPort, String ip) {
        this.serverPort = serverPort;
        this.ip = ip;
    }

    /**
     * This method opens the socket
     */
    public void connectToServer(String ip, int serverPort) {
        try {
            this.decoder = new NetworkMessageDecoder();
            this.socket = new Socket(ip, serverPort);
            Thread thread = new Thread(() -> {
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append(System.lineSeparator());
                        }
                        String json = stringBuilder.toString();
                        try {
                            NetworkMessage message = decoder.decodeMessage(json);
                            if (isTerminationMessage(message)) {
                                break;
                            }
                            didReceiveMessage(message);
                        } catch (MessageDecodeException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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


    public void sendMessage(NetworkMessage m) {
        try {
            if (outputStreamWriter == null) {
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            }
            outputStreamWriter.write(m.serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method send the notification to the relative listener
     * @param m message
     */
    private void didReceiveMessage(NetworkMessage m) {
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), m);
        if(m instanceof LoginResponse) {
            NotificationCenter.shared().post(NotificationName.ClientDidReceiveLoginMessage, null, userInfo);
        } else if (m instanceof ActivePlayerMessage) {
            NotificationCenter.shared().post(NotificationName.ClientDidReceiveActivePlayerMessage, null, userInfo);
        } else if (m instanceof MatchStateMessage) {
            NotificationCenter.shared().post(NotificationName.ClientDidReceiveMatchStateMessage, null, userInfo);
        } else if (m instanceof MatchTerminationMessage) {
            NotificationCenter.shared().post(NotificationName.ClientDidReceiveMatchTerminationMessage, null, userInfo);
        } else if (m instanceof PingPongMessage) {
            PingPongMessage pong = new PingPongMessage(false);
            sendMessage(pong);
        } else if (m instanceof PlayerActionMessage) {
            NotificationCenter.shared().post(NotificationName.ClientDidReceivePlayerActionMessage, null, userInfo);
        } else if (m instanceof PlayerActionResponse) {
            NotificationCenter.shared().post(NotificationName.ClientDidReceivePlayerActionResponse, null, userInfo);
        } else if (m instanceof PlayerStateMessage) {
            NotificationCenter.shared().post(NotificationName.ClientDidReceivePlayerStateMessage, null, userInfo);
        } else if (m instanceof TableStateMessage) {
            NotificationCenter.shared().post(NotificationName.ClientDidReceiveTableStateMessage, null, userInfo);
        } else if (m instanceof VictoryMessage) {
            NotificationCenter.shared().post(NotificationName.ClientDidReceiveVictoryMessage, null, userInfo);
        }
    }

    public void teardown() {
        try {
            bufferedReader.close();
            outputStreamWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
