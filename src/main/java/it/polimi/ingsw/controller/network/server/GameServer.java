package it.polimi.ingsw.controller.network.server;

import it.polimi.ingsw.controller.network.messages.NetworkMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class GameServer {

	private int serverPort;
	private ServerSocket serverSocket;
	
	public GameServer(int desiredPort) {
		this.serverPort = desiredPort;
	}
	
	public void startListeningIncomingConnections() {
	
	}
	
	private void createClientConnection(Socket clientSocket) {
	
	}
	
	public void sendMessage(NetworkMessage m, String playerNickname) {
	
	}
	
	public void broadcastMessage(NetworkMessage m) {
	
	}
	
	private void endGameAfterPlayerDisconnectionEvent() {
	
	}

}
