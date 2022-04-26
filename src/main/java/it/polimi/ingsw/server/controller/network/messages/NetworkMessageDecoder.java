package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;

public class NetworkMessageDecoder {
	
	public NetworkMessage decodeMessage(String serializedString) throws MessageDecodeException {
		try {
			return new PingPongMessage(serializedString);
		} catch (MessageDecodeException e0) {
			//Try another message
			try {
				return new LoginMessage(serializedString);
			} catch (MessageDecodeException e1) {
				//Try another message
				try {
					return new LoginResponse(serializedString);
				} catch (MessageDecodeException e2) {
					//Try another message
					try {
						return new TableStateMessage(serializedString);
					} catch (MessageDecodeException e3) {
						//Try another message
						try {
							return new PlayerStateMessage(serializedString);
						} catch (MessageDecodeException e4) {
							//Try another message
							try {
								return new ActivePlayerMessage(serializedString);
							} catch (MessageDecodeException e5) {
								//Try another message
								try {
									return new MatchStateMessage(serializedString);
								} catch (MessageDecodeException e6) {
									//Try another message
									try {
										return new PlayerActionMessage(serializedString);
									} catch (MessageDecodeException e7) {
										//Try another message
										try {
											return new PlayerActionResponse(serializedString);
										} catch (MessageDecodeException e8) {
											//Try another message
											try {
												return new VictoryMessage(serializedString);
											} catch (MessageDecodeException e9) {
												//Try another message, throw with error
												return new MatchTerminationMessage(serializedString);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
}
