package it.polimi.ingsw.controller.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.network.messages.LoginMessage;
import it.polimi.ingsw.controller.network.messages.MatchTerminationMessage;
import it.polimi.ingsw.controller.network.messages.NetworkMessage;
import it.polimi.ingsw.controller.network.messages.PlayerActionMessage;
import it.polimi.ingsw.controller.notifications.Notification;
import it.polimi.ingsw.controller.notifications.NotificationCenter;
import it.polimi.ingsw.controller.notifications.NotificationKeys;
import it.polimi.ingsw.controller.notifications.NotificationName;
import it.polimi.ingsw.exceptions.server.UnavailablePortException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class GameServer {

	private final int serverPort;
	private final List<VirtualClient> connectedClients;
	private final GameController activeController; //TODO: In case of multiple matches, this can become a list of controllers, then use a getter to get the controller hosting the match for a nickname
	
	public GameServer(int desiredPort) {
		this.serverPort = desiredPort;
		this.connectedClients = new ArrayList<>();
		this.activeController = new GameController(this);
	}
	
	public void startListeningIncomingConnections() throws UnavailablePortException {
		ExecutorService executor = Executors.newCachedThreadPool();
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			// Unavailable port
			throw new UnavailablePortException();
		}
		System.out.println("Server ready");
		while (true) {
			try {
				createClientConnection(serverSocket.accept(), executor);
			} catch(IOException e) {
				// The serverSocket is closed
				break;
			}
		}
		executor.shutdown();
	}
	
	private void createClientConnection(Socket clientSocket, ExecutorService executor) {
		connectedClients.add(new VirtualClient(clientSocket, executor, this));
	}
	
	protected void didReceiveMessageFromClient(NetworkMessage message, String clientUsername) {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), message);
		if (message instanceof LoginMessage) {
			NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, activeController, userInfo);
		} else if (message instanceof PlayerActionMessage) {
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, activeController, userInfo);
		} else if (message instanceof MatchTerminationMessage) {
			NotificationCenter.shared().post(NotificationName.ServerDidTerminateMatch, activeController, userInfo);
		}
		// For any other wrong message types we do nothing
	}
	
	public void sendMessage(NetworkMessage message, String playerNickname) {
		for (VirtualClient client: connectedClients) {
			if (playerNickname.equals(client.getNickname())) {
				client.sendMessage(message);
				break;
			}
		}
	}
	
	public void broadcastMessage(NetworkMessage message) {
		for (VirtualClient client: connectedClients) {
			client.sendMessage(message);
		}
	}
	
	private void endGameAfterPlayerDisconnectionEvent() {
		// Invoked if no PONG is received after a PING message is sent
	}

}
