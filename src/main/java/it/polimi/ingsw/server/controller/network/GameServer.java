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
	
	private final static int pingDelayMS = 0;
	private final static int pingIntervalMS = 2000;

	private final int serverPort;
	private final List<VirtualClient> connectedClients;
	private final List<GameController> activeControllers; //TODO: In case of multiple matches, this can become a list of controllers, then use a getter to get the controller hosting the match for a nickname
	
	// For Ping-Pong round trips
	private boolean isPinging = false;
	private boolean isFirstPing = true;
	private List<String> receivedPingsInCurrentTrip, sentPingsInCurrentTrip;
	
	public GameServer(int desiredPort) {
		this.serverPort = desiredPort;
		this.connectedClients = new ArrayList<>();
		this.activeControllers = new ArrayList<>();
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
	
	public static int getPingDelayMS() {
		return pingDelayMS;
	}
	
	public static int getPingIntervalMS() {
		return pingIntervalMS;
	}
	
	private synchronized void startPingTimer() {
		Timer pingTimer = new Timer("PingTimer");
		pingTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				pingClients();
			}
		}, pingDelayMS, pingIntervalMS);
		isPinging = true;
	}
	
	private synchronized void createClientConnection(Socket clientSocket, ExecutorService executor) {
		connectedClients.add(new VirtualClient(clientSocket, executor, this));
		if (!isPinging) {
			startPingTimer();
		}
	}
	
	private synchronized void pingClients() {
		// Signal disconnection if at least one client did not send the PONG response in time
		if (!isFirstPing) {
			List<VirtualClient> disconnectedClients = connectedClients.stream().filter((client) -> !receivedPingsInCurrentTrip.contains(client.getNickname()) && sentPingsInCurrentTrip.contains(client.getNickname())).toList();
			List<VirtualClient> associatedDisconnectedClients = new ArrayList<>();
			Set<GameController> disconnectedControllers = new HashSet<>();
			for (VirtualClient disconnectedClient: disconnectedClients) {
				// Signal the termination
				//TODO: With multiple concurrent matches we need to find the match to which the Player belongs to. In this case it is not needed, since we only have one match
				Optional<GameController> activeController = activeControllers.stream().filter((controller) -> controller.containsPlayerWithNickname(disconnectedClient.getNickname())).findFirst();
				if (activeController.isPresent()) {
					String[] controllerNicknames = activeController.get().getConnectedPlayerNicknames();
					for (VirtualClient associatedClient: connectedClients) {
						if (Arrays.stream(controllerNicknames).toList().contains(associatedClient.getNickname())) {
							associatedClient.notifyPlayerDisconnection();
							associatedClient.terminateConnection();
							associatedDisconnectedClients.add(associatedClient);
						}
					}
					NotificationCenter.shared().post(NotificationName.ServerDidTerminateMatch, activeController.get(), null);
					disconnectedControllers.add(activeController.get());
				} else {
					System.out.println("Client disconnected, but not logged in");
					disconnectedClient.terminateConnection();
				}
			}
			connectedClients.removeAll(disconnectedClients);
			connectedClients.removeAll(associatedDisconnectedClients);
			activeControllers.removeAll(disconnectedControllers);
			receivedPingsInCurrentTrip = new ArrayList<>();
		}
		isFirstPing = false;
		PingPongMessage pingMessage = new PingPongMessage(true);
		sentPingsInCurrentTrip = connectedClients.stream().filter(VirtualClient::isPingable).map(VirtualClient::getNickname).filter(Objects::nonNull).toList();
		broadcastMessage(pingMessage);
	}
	
	protected synchronized void didReceiveMessageFromClient(NetworkMessage message, VirtualClient client) {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), message);
		if (message instanceof LoginMessage login) {
			System.out.println("Received login message: nickname " + login.getNickname());
			Optional<GameController> matchingController = Optional.empty();
			for (GameController controller: activeControllers) {
				if (controller.getMatchVariant() == login.getMatchVariant() && controller.getMaxPlayerCount() == login.getDesiredNumberOfPlayers() && controller.acceptsPlayers()) {
					matchingController = Optional.of(controller);
					break;
				}
			}
			if (getControllerWithNickname(login.getNickname()).isEmpty()) {
				GameController availableController = matchingController.orElse(new GameController(this));
				NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, availableController, userInfo);
				if (matchingController.isEmpty()) {
					activeControllers.add(availableController);
				}
				client.setPingable(true);
			} else {
				System.out.println("Sending nickname error");
				NetworkMessage errorMessage = new LoginResponse(login.getNickname(), false, Integer.MAX_VALUE, "The nickname you entered is not unique. Please choose another nickname");
				client.sendMessage(errorMessage);
				// Disconnect immediately
				//TODO: Can we avoid disconnecting the Client?
				client.sendMessage(new MatchTerminationMessage("The nickname you entered is not unique. Please choose another nickname", false));
				client.terminateConnection();
				connectedClients.remove(client);
			}
		} else if (message instanceof PlayerActionMessage) {
			getControllerWithNickname(client.getNickname()).ifPresent((controller) -> NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo));
		} else if (message instanceof MatchTerminationMessage) {
			if (client.isPingable()) {
				getControllerWithNickname(client.getNickname()).ifPresent((controller) -> NotificationCenter.shared().post(NotificationName.ServerDidTerminateMatch, controller, userInfo));
			}
		} else if (message instanceof PingPongMessage) {
			receivedPingsInCurrentTrip.add(client.getNickname());
		}
		// For any other wrong message types we do nothing
	}
	
	private Optional<GameController> getControllerWithNickname(String nickname) {
		return activeControllers.stream().filter((controller) -> controller.containsPlayerWithNickname(nickname)).findFirst();
	}
	
	public synchronized void sendMessage(NetworkMessage message, String playerNickname) {
		for (VirtualClient client: connectedClients) {
			if (playerNickname.equals(client.getNickname())) {
				client.sendMessage(message);
				break;
			}
		}
	}
	
	public synchronized void broadcastMessage(NetworkMessage message) {
		for (VirtualClient client: connectedClients) {
			if (client.getNickname() != null) {
				client.sendMessage(message);
			}
		}
	}

}
