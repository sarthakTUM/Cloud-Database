package app_kvServer;

import java.io.IOException;
import java.net.Socket;

import common.messages.Payload;
import client.SocketListener;

public class ServerSocketListener  extends Thread implements SocketListener{

	/*
	 * TODO move SocketListener to Commons
	 */
	
	public ServerSocketListener(Socket serverSocket) {
		// TODO Auto-generated constructor stub
		this.serverSocket = serverSocket;
		try {
			inputStream = this.serverSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		this.isRunning = true;
		while(isRunning){
			
			// TODO see if KVMessage can be returned.
			
			//KVMessage kvMessage;
			try {
				this.payload = receiveMessage();
				
			} catch (IOException e) {
				/*
				 * TODO some exception occurred, call handleStatus with new status.
				 */
				e.printStackTrace();
			}
			
		}
	}
	
	@Override
	public void handleStatus(SocketState socketState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Payload receiveMessage() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
