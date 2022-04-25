package it.polimi.ingsw.controller.network;

import it.polimi.ingsw.controller.network.messages.NetworkMessage;

import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {

	private int serverPort;
	private ServerSocket serverSocket;
	
	public GameServer(int desiredPort) {
	
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
