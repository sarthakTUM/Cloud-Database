package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import common.messages.Payload;

public class ClientSocketListener extends Thread implements SocketListener{

	private Socket clientSocket;
	private boolean isRunning;
	private InputStream inputStream;
	private static final int BUFFER_SIZE = 1024;
	private static final int DROP_SIZE = 1024 * BUFFER_SIZE;
	private Payload payload = null;
	
	public ClientSocketListener(Socket clientSocket) {
		// TODO Auto-generated constructor stub
		this.clientSocket = clientSocket;
		try {
			inputStream = this.clientSocket.getInputStream();
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
		/*
		 * Update socket state if some exception occurs.
		 */
		
	}
	
	@Override
	public Payload receiveMessage() throws IOException{
		/*
		 * TODO receive Message from this.clientSocket's inputStreamin form of bytes[]
		 * 		and construct a Payload and return it.
		 * 		1. Check if inputStream is not null.
		 * 		2. If something is present on it, read until delimiter.
		 * 		3. Store the bytes[] and pass them to Payload construvtor to construct a new message of type KVMessage
		 * 		
		 */
		Payload payload = null;
		if(inputStream.available() != 0){
			/*
			 * TODO data is available, read it.
			 */
			int index = 0;
			byte[] msgBytes = null, tmp = null;
			byte[] bufferBytes = new byte[BUFFER_SIZE];
			
			/* read first char from stream */
			byte read = (byte) inputStream.read();	
			boolean reading = true;
			
			while(read != 13 && reading) {/* carriage return */
				/* if buffer filled, copy to msg array */
				if(index == BUFFER_SIZE) {
					if(msgBytes == null){
						tmp = new byte[BUFFER_SIZE];
						System.arraycopy(bufferBytes, 0, tmp, 0, BUFFER_SIZE);
					} else {
						tmp = new byte[msgBytes.length + BUFFER_SIZE];
						System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
						System.arraycopy(bufferBytes, 0, tmp, msgBytes.length,
								BUFFER_SIZE);
					}

					msgBytes = tmp;
					bufferBytes = new byte[BUFFER_SIZE];
					index = 0;
				} 
				
				/* only read valid characters, i.e. letters and numbers */
				if((read > 31 && read < 127)) {
					bufferBytes[index] = read;
					index++;
				}
				
				/* stop reading is DROP_SIZE is reached */
				if(msgBytes != null && msgBytes.length + index >= DROP_SIZE) {
					reading = false;
				}
				
				/* read next char from stream */
				read = (byte) inputStream.read();
			}
			
			if(msgBytes == null){
				tmp = new byte[index];
				System.arraycopy(bufferBytes, 0, tmp, 0, index);
			} else {
				tmp = new byte[msgBytes.length + index];
				System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
				System.arraycopy(bufferBytes, 0, tmp, msgBytes.length, index);
			}
			
			msgBytes = tmp;
			/*
			 * TODO build a final payload with msgBytes.
			 */
			payload = new Payload(msgBytes);
			
			
		}
		return payload;
		
	}
	
	public Payload getPayload(){
		return this.payload;
	}

	
}
