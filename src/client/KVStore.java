package client;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import common.messages.KVMessage;
import common.messages.Message;
import common.messages.Payload;

public class KVStore implements KVCommInterface {

	private final static String LOG = "LOG:KVSTORE$:";
	private String serverAddress;
	private int serverPort;
	private Socket clientSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	/**
	 * Initialize KVStore with address and port of KVServer
	 * @param address the address of the KVServer
	 * @param port the port of the KVServer
	 */
	public KVStore(String address, int port) {
		System.out.println(LOG + "initialized system with : " + address + ":" + port);
		this.serverAddress = address;
		this.serverPort = port;
	}
	
	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub
		System.out.println(LOG + "connecting to server : " + serverAddress + ":" + serverPort);
		clientSocket = new Socket(serverAddress, serverPort);
		inputStream = clientSocket.getInputStream();
		outputStream = clientSocket.getOutputStream();
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public KVMessage put(String key, String value) throws Exception {
		

		Payload payload = new Payload(key, value, "PUT");
		Message message = new Message(payload);
		byte[] request = message.serializeMessage();
		System.out.println(LOG + "serialized request: " + request);

		outputStream.write(request);
		outputStream.flush();

		// call receive message that will read KVMessage returned by the server.
		receiveMessage();
		
		return null;
	}

	@Override
	public KVMessage get(String key) throws Exception {
		// TODO Auto-generated method stub
		Payload payload = new Payload(key, "null", "GET");
		Message message = new Message(payload);
		byte[] request = message.serializeMessage();
		System.out.println(LOG + "serialized request: " + request);

		outputStream.write(request);
		outputStream.flush();
		return null;
	}
	
	private void receiveMessage(){
		
	}

	
}
