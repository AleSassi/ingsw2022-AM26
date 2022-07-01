package it.polimi.ingsw.server.controller.network;

import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Class {@code VirtualClient} represent the {@code Client} from {@code Server's} side
 */
public class VirtualClient {
	
	private String nickname;
	private final Socket socket;
	private final NetworkMessageDecoder decoder;
	private final GameServer parentServer;
	private final Future<?> task;
	private boolean isPingable = false;
	
	private BufferedReader bufferedReader;
	private OutputStreamWriter outputStreamWriter;

	/**
	 * Constructor sets the {@code VirtualClient's} parameters
	 * @param socket (type Socket) connection socket
	 * @param executorService (type executorService)
	 * @param parentServer (type GameServer) {@link it.polimi.ingsw.server.controller.network.GameServer GameServe} to connect to
	 */
	public VirtualClient(Socket socket, ExecutorService executorService, GameServer parentServer) {
		this.socket = socket;
		this.decoder = new NetworkMessageDecoder();
		this.parentServer = parentServer;
		System.out.println("Accepted a connection to " + socket.getRemoteSocketAddress());
		// Create a Runnable instance that will listen to incoming messages
		task = executorService.submit(this::readMessagesFromSocket);
	}
	
	/**
	 * Reads a message arriving from the Socket, decodes it and notifies the Server about it
	 */
	private void readMessagesFromSocket() {
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				String json = bufferedReader.readLine();
				
				if (json != null && !json.isEmpty() && !json.isBlank()) {
					// Decode the JSON to NetworkMessage
					try {
						NetworkMessage message = decoder.decodeMessage(json);
						if (isTerminationMessage(message)) {
							break;
						}
						didReceiveMessage(message);
					} catch (MessageDecodeException e) {
						// The message is wrong - we do nothing
						//Send an error message (malformed request)
						sendMessage(new PlayerActionResponse(nickname, PlayerActionMessage.ActionType.DidPlayAssistantCard, false, "Malformed request"));
					}
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
			task.cancel(true);
		}
	}

	/**
	 * Gets the {@link it.polimi.ingsw.server.model.Player Player's} nickname
	 * @return (type String) returns the {@code Player's} nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Checks if the {@code Client} is pingable
	 * @return (type boolean) returns true if {@code Client} is pingable
	 */
	public boolean isPingable() {
		return isPingable;
	}

	/**
	 * Sets this {@code VirtualClient} to pingable
	 * @param pingable (type boolean) true if it needs to be pingable
	 */
	public void setPingable(boolean pingable) {
		isPingable = pingable;
	}

	/**
	 * Sends the {@link it.polimi.ingsw.server.controller.network.messages.NetworkMessage NetworkMessage} to the Client
	 * @param message (type NetworkMessage) message to send
	 */
	public synchronized void sendMessage(NetworkMessage message) {
		if (socket != null && !socket.isClosed()) {
			try {
				if (outputStreamWriter == null) {
					outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
				}
				outputStreamWriter.write(message.serialize() + "\n");
				outputStreamWriter.flush();
			} catch (SocketException e) {
				// Broken pipe - client disconnected
				parentServer.didReceiveMessageFromClient(new MatchTerminationMessage("Client disconnected", false), this);
				terminateConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@link it.polimi.ingsw.server.controller.network.messages.NetworkMessage NetworkMessage} received callback, used to notify the server that a message was received by this client
	 * @param message (type NetworkMessage) received message
	 */
	public synchronized void didReceiveMessage(NetworkMessage message) {
		if (message instanceof LoginMessage loginMessage) {
			nickname = loginMessage.getNickname();
		}
		parentServer.didReceiveMessageFromClient(message, this);
	}

	/**
	 * Checks if a message is a Termination message
	 * @param message (type NetworkMessage) received message
	 * @return (type boolean) returns true the {@code NetworkMessage} is a termination message
	 */
	private boolean isTerminationMessage(NetworkMessage message) {
		return message instanceof MatchTerminationMessage;
	}

	/**
	 * Sends to the Client the termination message for when a Player disconnected from the match
	 */
	public void notifyPlayerDisconnection() {
		sendMessage(new MatchTerminationMessage("Another Player disconnected and this Match has ended", true));
	}

	/**
	 * Terminates the connection
	 */
	public synchronized void terminateConnection() {
		try {
			task.cancel(true);
			if (bufferedReader != null) {
				bufferedReader.close();
				bufferedReader = null;
			}
			if (outputStreamWriter != null) {
				outputStreamWriter.close();
			}
			if (!socket.isClosed()) {
				socket.shutdownInput();
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		VirtualClient that = (VirtualClient) o;
		
		if (!Objects.equals(nickname, that.nickname)) return false;
		if (!Objects.equals(socket, that.socket)) return false;
		if (!Objects.equals(decoder, that.decoder)) return false;
		if (!Objects.equals(parentServer, that.parentServer)) return false;
		if (!Objects.equals(bufferedReader, that.bufferedReader))
			return false;
		return Objects.equals(outputStreamWriter, that.outputStreamWriter);
	}
	
	@Override
	public int hashCode() {
		int result = nickname != null ? nickname.hashCode() : 0;
		result = 31 * result + (socket != null ? socket.hashCode() : 0);
		result = 31 * result + (decoder != null ? decoder.hashCode() : 0);
		result = 31 * result + (parentServer != null ? parentServer.hashCode() : 0);
		result = 31 * result + (bufferedReader != null ? bufferedReader.hashCode() : 0);
		result = 31 * result + (outputStreamWriter != null ? outputStreamWriter.hashCode() : 0);
		return result;
	}
}
