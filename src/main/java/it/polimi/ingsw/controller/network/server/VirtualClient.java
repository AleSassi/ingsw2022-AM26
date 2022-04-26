package it.polimi.ingsw.controller.network.server;

import it.polimi.ingsw.controller.network.messages.LoginMessage;
import it.polimi.ingsw.controller.network.messages.MatchTerminationMessage;
import it.polimi.ingsw.controller.network.messages.NetworkMessage;
import it.polimi.ingsw.controller.network.messages.NetworkMessageDecoder;
import it.polimi.ingsw.controller.notifications.NotificationCenter;
import it.polimi.ingsw.controller.notifications.NotificationName;
import it.polimi.ingsw.exceptions.model.MessageDecodeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

public class VirtualClient {
	
	private String nickname;
	private final Socket socket;
	private final NetworkMessageDecoder decoder;
	private final GameServer parentServer;
	
	private BufferedReader bufferedReader;
	private OutputStreamWriter outputStreamWriter;
	
	public VirtualClient(Socket socket, ExecutorService executorService, GameServer parentServer) {
		this.socket = socket;
		this.decoder = new NetworkMessageDecoder();
		this.parentServer = parentServer;
		// Create a Runnable instance that will listen to incoming messages
		executorService.submit(() -> {
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
						if (isTerminationMessage(message)) {
							break;
						}
						didReceiveMessage(message);
					} catch (MessageDecodeException e) {
						// The message is wrong - we do nothing
						//TODO: Send an error message (malformed request)
						e.printStackTrace();
					}
				}
				notifyPlayerDisconnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public String getNickname() {
		return nickname;
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
	
	public void didReceiveMessage(NetworkMessage message) {
		if (message instanceof LoginMessage loginMessage) {
			nickname = loginMessage.getNickname();
		}
		parentServer.didReceiveMessageFromClient(message, nickname);
	}
	
	private boolean isTerminationMessage(NetworkMessage message) {
		return message instanceof MatchTerminationMessage;
	}
	
	public void notifyPlayerDisconnection() {
		try {
			bufferedReader.close();
			outputStreamWriter.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
