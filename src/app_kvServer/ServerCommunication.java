package app_kvServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import common.messages.KVMessage;
import common.messages.Message;
import common.messages.Payload;
import common.messages.KVMessage.MessageSource;
import common.messages.KVMessage.StatusType;
import client.ClientSocketListener;
import client.KVCommInterface;

public class ServerCommunication implements KVCommInterface {

	private final static String LOG = "LOG:SRVRCOMM:";
	private String serverAddress;
	private int serverPort;
	private Socket serverSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private static final int BUFFER_SIZE = 1024;
	private static final int DROP_SIZE = 1024 * BUFFER_SIZE;
	//private ClientSocketListener clientSocketListener;
	private MessageSource messageSource = MessageSource.CLUSTER;

	public ServerCommunication(String serverIP, int serverPort){
		this.serverAddress = serverIP;
		this.serverPort = serverPort;
	}
	@Override
	public void connect() throws Exception {

		System.out.println(LOG + "connecting to server : " + serverAddress + ":" + serverPort);
		serverSocket = new Socket(serverAddress, serverPort);
		inputStream = serverSocket.getInputStream();
		outputStream = serverSocket.getOutputStream();
		//clientSocketListener = new ClientSocketListener(clientSocket);
		//clientSocketListener.start();

	}

	@Override
	public void disconnect() {
		// TODO close the current connection and close the streams.
		

	}

	@Override
	public KVMessage put(String key, String value) throws Exception {

		Payload payload = new Payload(key, value, "PUT");
		payload.setSource(this.messageSource);
		payload.setStatusType(StatusType.PUT);
		Message message = new Message(payload);
		byte[] request = message.serializeMessage();

		System.out.println(LOG + "serialized request: " + request);

		outputStream.write(request);
		outputStream.write(13);
		outputStream.flush();

		Thread.sleep(2000);

		payload = receiveMessage();
		if(payload != null){
			System.out.println(LOG + "payload received : " + payload.getStatus());
		}

		/*
		 * TODO check if the source of the payload is SERVER then only return it. 
		 */
		return payload;

	}

	@Override
	public KVMessage get(String key) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Payload receiveMessage() throws IOException{

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

}
