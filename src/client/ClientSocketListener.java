package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import common.messages.KVMessage.StatusType;
import common.messages.Payload;

public class ClientSocketListener extends Thread implements SocketListener{

	private static final String LOG = "LOG:CLSCKLNR:";
	private Socket clientSocket;
	private boolean isRunning;
	private InputStream inputStream;
	private OutputStream outputStream;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	private static final int BUFFER_SIZE = 1024;
	private static final int DROP_SIZE = 1024 * BUFFER_SIZE;
	private volatile Payload payload = null;
	public volatile static boolean syncProtocolFirstResponse = false; 
	public volatile static boolean syncProtocolSecondResponse = false; 
	
	public ClientSocketListener(Socket clientSocket, InputStream inputStream, ObjectInputStream objectInputStream) throws IOException {
		
		System.out.println(LOG + "initializing clientSocketListener with: " + clientSocket);
		this.clientSocket = clientSocket;
		
		this.objectInputStream = objectInputStream;
	}
	
	public void run(){
		System.out.println(LOG + "running CSL");
		this.isRunning = true;
		while(isRunning){
			
			
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
		/*
		 * Update socket state if some exception occurs.
		 */
		
	}
	
	@Override
	public Payload receiveMessage() throws IOException{
		
		Payload payload = null;

		//if(objectInputStream.available() != 0){
		/*
		 * TODO data is available, read it.
		 */
		try {
			//synchronized (this) {
			payload = (Payload) objectInputStream.readObject();
			if(payload.getStatus() == StatusType.FILE_EXISTS){
				syncProtocolFirstResponse = true;
			}
			if(payload.getStatus() == StatusType.SYNC_COMPLETE){
				syncProtocolSecondResponse = true;
			}
			//}


		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//}
		return payload;
		
	}
	
	public Payload getPayload(){
		return this.payload;
	}

	
}
