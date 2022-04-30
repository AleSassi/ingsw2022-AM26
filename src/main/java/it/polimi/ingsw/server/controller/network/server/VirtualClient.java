package it.polimi.ingsw.server.controller.network.server;

import it.polimi.ingsw.server.controller.network.messages.LoginMessage;
import it.polimi.ingsw.server.controller.network.messages.MatchTerminationMessage;
import it.polimi.ingsw.server.controller.network.messages.NetworkMessage;
import it.polimi.ingsw.server.controller.network.messages.NetworkMessageDecoder;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

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
		System.out.println("Accepted a connection to " + socket.getRemoteSocketAddress());
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
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void sendMessage(NetworkMessage message) {
		try {
			if (outputStreamWriter == null) {
				outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
			}
			System.out.println("Sending " + message.serialize());
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
		sendMessage(new MatchTerminationMessage("Another Player disconnected and this Match has ended", true));
	}
	
	public void terminateConnection() {
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
