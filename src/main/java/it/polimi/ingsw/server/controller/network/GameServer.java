package it.polimi.ingsw.server.controller.network;

import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
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
	private boolean isPinging = false;
	private boolean isFirstPing = true;
	private List<String> receivedPingsInCurrentTrip, sentPingsInCurrentTrip;
	
	public GameServer(int desiredPort) {
		this.serverPort = desiredPort;
		this.connectedClients = new ArrayList<>();
		this.activeController = new GameController(this);
		this.receivedPingsInCurrentTrip = new ArrayList<>();
		this.sentPingsInCurrentTrip = new ArrayList<>();
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
		System.out.println("Server ready - listening at address " + serverSocket.getLocalSocketAddress() + ":" + serverSocket.getLocalPort());
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
		int pingDelayMS = 0;
		int pingIntervalMS = 8000;
		pingTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				pingClients();
			}
		}, pingDelayMS, pingIntervalMS);
		isPinging = true;
	}
	
	private void createClientConnection(Socket clientSocket, ExecutorService executor) {
		connectedClients.add(new VirtualClient(clientSocket, executor, this));
		if (!isPinging) {
			startPingTimer();
		}
	}
	
	private void pingClients() {
		// Signal disconnection if at least one client did not send the PONG response in time
		if (!isFirstPing) {
			List<VirtualClient> disconnectedClients = connectedClients.stream().filter((client) -> !receivedPingsInCurrentTrip.contains(client.getIpPortString()) && sentPingsInCurrentTrip.contains(client.getIpPortString())).toList();
			List<VirtualClient> associatedDisconnectedClients = new ArrayList<>();
			for (VirtualClient disconnectedClient: disconnectedClients) {
				// Signal the termination
				//TODO: With multiple concurrent matches we need to find the match to which the Player belongs to. In this case it is not needed, since we only have one match
				if (activeController.containsPlayerWithNickname(disconnectedClient.getNickname())) {
					System.out.println("Client disconnected, terminating message");
					NotificationCenter.shared().post(NotificationName.ServerDidTerminateMatch, activeController, null);
					for (VirtualClient associatedClient: connectedClients) {
						if (activeController.containsPlayerWithNickname(associatedClient.getNickname())) {
							associatedClient.notifyPlayerDisconnection();
							associatedClient.terminateConnection();
							associatedDisconnectedClients.add(associatedClient);
						}
					}
				} else {
					System.out.println("Client disconnected, but not logged in");
					disconnectedClient.terminateConnection();
				}
			}
			connectedClients.removeAll(disconnectedClients);
			connectedClients.removeAll(associatedDisconnectedClients);
			receivedPingsInCurrentTrip = new ArrayList<>();
		}
		isFirstPing = false;
		System.out.println("Pinging...");
		PingPongMessage pingMessage = new PingPongMessage(true);
		sentPingsInCurrentTrip = connectedClients.stream().map(VirtualClient::getIpPortString).toList();
		broadcastMessage(pingMessage);
	}
	
	protected void didReceiveMessageFromClient(NetworkMessage message, VirtualClient client) {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), message);
		if (message instanceof LoginMessage login) {
			System.out.println("Received login message: nickname " + login.getNickname());
			NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, activeController, userInfo);
		} else if (message instanceof PlayerActionMessage) {
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, activeController, userInfo);
		} else if (message instanceof MatchTerminationMessage) {
			NotificationCenter.shared().post(NotificationName.ServerDidTerminateMatch, activeController, userInfo);
		} else if (message instanceof PingPongMessage pingPongMessage) {
			receivedPingsInCurrentTrip.add(client.getIpPortString());
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
