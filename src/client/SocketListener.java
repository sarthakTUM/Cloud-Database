package client;

import java.io.IOException;

import common.messages.Payload;

public interface SocketListener {

	public enum SocketState{
		DISCONNECTED,
		CONNECTED,
		CONNECTION_LOST
	}
	
	public void handleStatus(SocketState socketState);
	public Payload receiveMessage() throws IOException;
	
}
