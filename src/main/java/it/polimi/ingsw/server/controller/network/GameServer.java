package it.polimi.ingsw.server.controller.network;

import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.server.UnavailablePortException;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

/**
 * Class {@code GameServer} represent the Game Server
 */
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

	/**
	 * Constructor sets up the server on the specified port
	 * @param desiredPort (type int) The server port
	 */
	public GameServer(int desiredPort) {
		this.serverPort = desiredPort;
		this.connectedClients = new ArrayList<>();
		this.activeControllers = new ArrayList<>();
		this.receivedPingsInCurrentTrip = new ArrayList<>();
		this.sentPingsInCurrentTrip = new ArrayList<>();
	}

	/**
	 * Starts listening to new connections from {@link it.polimi.ingsw.jar.Client Clients}
	 * @throws UnavailablePortException whenever the chosen port is unavailable
	 */
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

	/**
	 * Gets the ping delay (in milliseconds)
	 * @return (type int) returns the ping delay (in milliseconds)
	 */
	public static int getPingDelayMS() {
		return pingDelayMS;
	}

	/**
	 * Gets the ping interval (in milliseconds)
	 * @return (type int) returns the ping interval (in milliseconds)
	 */
	public static int getPingIntervalMS() {
		return pingIntervalMS;
	}

	/**
	 * Starts the ping timer
	 */
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

	/**
	 * Creates a new Server-Client connection
	 * @param clientSocket (type Socket) {@link it.polimi.ingsw.jar.Client Client's} socket
	 * @param executor (type executor) The executor used to schedule client threads on
	 */
	private synchronized void createClientConnection(Socket clientSocket, ExecutorService executor) {
		connectedClients.add(new VirtualClient(clientSocket, executor, this));
		if (!isPinging) {
			startPingTimer();
		}
	}

	/**
	 * Pings the {@link it.polimi.ingsw.server.controller.network.VirtualClient Clients}
	 */
	private synchronized void pingClients() {
		// Signal disconnection if at least one client did not send the PONG response in time
		if (!isFirstPing) {
			List<VirtualClient> disconnectedClients = connectedClients.stream().filter((client) -> !receivedPingsInCurrentTrip.contains(client.getNickname()) && sentPingsInCurrentTrip.contains(client.getNickname())).toList();
			findClientsAndDisconnect(disconnectedClients);
			receivedPingsInCurrentTrip = new ArrayList<>();
		}
		isFirstPing = false;
		PingPongMessage pingMessage = new PingPongMessage(true);
		sentPingsInCurrentTrip = connectedClients.stream().filter(VirtualClient::isPingable).map(VirtualClient::getNickname).filter(Objects::nonNull).toList();
		broadcastMessage(pingMessage);
	}
	
	/**
	 * Given a list of {@link it.polimi.ingsw.server.controller.network.VirtualClient disconnected clients} it finds all matches that contained them and disconnects all players in their same match, cleaning up the leftover state
	 * @param disconnectedClients The list of clients that have disconnected from the server
	 */
	private void findClientsAndDisconnect(List<VirtualClient> disconnectedClients) {
		List<VirtualClient> associatedDisconnectedClients = new ArrayList<>();
		Set<GameController> disconnectedControllers = new HashSet<>();
		for (VirtualClient disconnectedClient: disconnectedClients) {
			// Signal the termination
			Optional<GameController> activeController = activeControllers.stream().filter((controller) -> controller.containsPlayerWithNickname(disconnectedClient.getNickname())).findFirst();
			if (activeController.isPresent()) {
				String[] controllerNicknames = activeController.get().getConnectedPlayerNicknames();
				for (VirtualClient associatedClient: connectedClients) {
					if (Arrays.stream(controllerNicknames).toList().contains(associatedClient.getNickname())) {
						if (!disconnectedClients.stream().map(VirtualClient::getNickname).toList().contains(associatedClient.getNickname())) {
							associatedClient.notifyPlayerDisconnection();
						}
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
	}

	/**
	 * Callback for messages received from {@link it.polimi.ingsw.jar.Client Clients}
	 * @param message (type NetworkMessage) the received message
	 * @param client (type VirtualClient) The client which sent the message
	 */
	protected synchronized void didReceiveMessageFromClient(NetworkMessage message, VirtualClient client) {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), message);
		if (message instanceof LoginMessage login) {
			handleLoginReceived(login, client, userInfo);
		} else if (message instanceof PlayerActionMessage) {
			getControllerWithNickname(client.getNickname()).ifPresent((controller) -> NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo));
		} else if (message instanceof MatchTerminationMessage) {
			if (client.isPingable()) {
				findClientsAndDisconnect(List.of(new VirtualClient[]{client}));
			}
		} else if (message instanceof PingPongMessage) {
			receivedPingsInCurrentTrip.add(client.getNickname());
		}
		// For any other wrong message types we do nothing
	}
	
	/**
	 * Handles the login event of a client
	 * @param login The login message from the Client
	 * @param client The client that wants to log in
	 * @param userInfo The data to send with the server notification used to tell game controllers that a login has happened
	 */
	private void handleLoginReceived(LoginMessage login, VirtualClient client, HashMap<String, Object> userInfo) {
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
			client.sendMessage(new MatchTerminationMessage("The nickname you entered is not unique. Please choose another nickname", false));
			client.terminateConnection();
			connectedClients.remove(client);
		}
	}

	/**
	 * Gets the {@link it.polimi.ingsw.server.controller.GameController GameController} associated with the {@link it.polimi.ingsw.server.model.Player Player's} nickname
	 * @param nickname (type String) {@code Player's} nickname
	 * @return (type Optional(GameController)) returns the {@code GameController} associated with the {@code Player's} nickname
	 */
	private Optional<GameController> getControllerWithNickname(String nickname) {
		return activeControllers.stream().filter((controller) -> controller.containsPlayerWithNickname(nickname)).findFirst();
	}

	/**
	 * Sends the {@link it.polimi.ingsw.server.controller.network.messages.NetworkMessage NetworkMessage}
	 * @param message (type NetworkMessage) message to send
	 * @param playerNickname (type String) {@link it.polimi.ingsw.server.model.Player Player's} nickname to send to
	 */
	public synchronized void sendMessage(NetworkMessage message, String playerNickname) {
		for (VirtualClient client: connectedClients) {
			if (playerNickname.equals(client.getNickname())) {
				client.sendMessage(message);
				break;
			}
		}
	}

	/**
	 * Sends the message to all {@link it.polimi.ingsw.jar.Client Clients}
	 * @param message (type NetworkMessage) message to send
	 */
	public synchronized void broadcastMessage(NetworkMessage message) {
		for (VirtualClient client: connectedClients) {
			if (client.getNickname() != null) {
				client.sendMessage(message);
			}
		}
	}

}
