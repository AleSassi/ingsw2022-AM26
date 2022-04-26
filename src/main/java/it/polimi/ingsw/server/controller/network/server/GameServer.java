package it.polimi.ingsw.server.controller.network.server;

import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.server.controller.notifications.NotificationCenter;
import it.polimi.ingsw.server.controller.notifications.NotificationKeys;
import it.polimi.ingsw.server.controller.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.server.UnavailablePortException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class GameServer {

	private final int serverPort;
	private final List<VirtualClient> connectedClients;
	private final GameController activeController; //TODO: In case of multiple matches, this can become a list of controllers, then use a getter to get the controller hosting the match for a nickname
	
	// For Ping-Pong round trips
	private List<String> receivedPingsInCurrentTrip;
	
	public GameServer(int desiredPort) {
		this.serverPort = desiredPort;
		this.connectedClients = new ArrayList<>();
		this.activeController = new GameController(this);
		this.receivedPingsInCurrentTrip = new ArrayList<>();
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
		startPingTimer();
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
	
	private void startPingTimer() {
		Timer pingTimer = new Timer("PingTimer");
		pingTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				pingClients();
			}
		}, 1000, 5000);
	}
	
	private void createClientConnection(Socket clientSocket, ExecutorService executor) {
		connectedClients.add(new VirtualClient(clientSocket, executor, this));
	}
	
	private void pingClients() {
		// Signal disconnection if at least one client did not send the PONG response in time
		List<VirtualClient> disconnectedClients = connectedClients.stream().filter((client) -> !receivedPingsInCurrentTrip.contains(client.getNickname())).toList();
		for (VirtualClient disconnectedClient: disconnectedClients) {
			// Signal the termination
			//TODO: With multiple concurrent matches we need to find the match to which the Player belongs to. In this case it is not needed, since we only have one match
			if (activeController.containsPlayerWithNickname(disconnectedClient.getNickname())) {
				NotificationCenter.shared().post(NotificationName.ServerDidTerminateMatch, activeController, null);
				break;
			}
		}
		receivedPingsInCurrentTrip = new ArrayList<>();
		PingPongMessage pingMessage = new PingPongMessage(true);
		broadcastMessage(pingMessage);
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
		} else if (message instanceof PingPongMessage pingPongMessage) {
			receivedPingsInCurrentTrip.add(clientUsername);
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

}
