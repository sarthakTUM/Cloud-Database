package client;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import common.messages.DeltaSync;
import common.messages.KVMessage;
import common.messages.KVMessage.MessageSource;
import common.messages.KVMessage.StatusType;
import common.messages.Payload;

public class KVStore implements KVCommInterface {

	private final static String LOG = "LOG:KVSTORE$:";
	private String serverAddress;
	private int serverPort;
	private Socket clientSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	private ClientSocketListener clientSocketListener;
	private MessageSource messageSource = MessageSource.CLIENT;
	/**
	 * Initialize KVStore with address and port of KVServer
	 * @param address the address of the KVServer
	 * @param port the port of the KVServer
	 */
	public KVStore(String address, int port) {
		System.out.println(LOG + "initialized system with : " + address + ":" + port);
		this.serverAddress = "127.0.0.1";
		this.serverPort = port;
	}

	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub
		System.out.println(LOG + "connecting to server : " + serverAddress + ":" + serverPort);
		clientSocket = new Socket(serverAddress, serverPort);
		inputStream = clientSocket.getInputStream();
		outputStream = clientSocket.getOutputStream();
		objectOutputStream = new ObjectOutputStream(outputStream);
		//objectOutputStream.flush();
		objectInputStream = new ObjectInputStream(inputStream);
		clientSocketListener = new ClientSocketListener(clientSocket, inputStream, objectInputStream);
		clientSocketListener.start();
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public KVMessage put(String key, String value) throws Exception {


		Payload payload = new Payload(key, value, "PUT");
		payload.setSource(this.messageSource);
		payload.setStatusType(StatusType.PUT);
		
		/*Message message = new Message(payload);
		byte[] request = message.serializeMessage();

		System.out.println(LOG + "serialized request: " + request);

		outputStream.write(request);
		outputStream.write(13);
		outputStream.flush();*/
		objectOutputStream.writeObject(payload);

		Thread.sleep(2000);

		payload = clientSocketListener.getPayload();
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
		Payload payload = new Payload(key, "null", "GET");
		payload.setSource(messageSource);
		payload.setStatusType(StatusType.GET);
		
		/*
		Message message = new Message(payload);
		byte[] request = message.serializeMessage();
		System.out.println(LOG + "serialized request: " + request);

		outputStream.write(request);
		outputStream.write(13);
		outputStream.flush();
		System.out.println(LOG + "waiting for response...");*/
		/*
		 * TODO instead, call getPayload() of the clientSocketListener.
		 */

		objectOutputStream.writeObject(payload);

		Thread.sleep(2000);

		payload = clientSocketListener.getPayload();
		if(payload != null){
			System.out.println(LOG + "payload received : " + payload.getStatus());
		}

		/*
		 * TODO check if the source of the payload is SERVER then only return it. 
		 */
		return payload;

		/*
		 * TODO check if the payload is from SERVER before returning.
		 */
	}

	public KVMessage sync(String fileName) throws IOException{
		Payload payload = new Payload(fileName, "null", "SYNC");
		payload.setSource(messageSource);
		payload.setStatusType(StatusType.SYNC);
		payload.setKey(fileName);
		String clientFileName = fileName;
		File clientFile = new File(clientFileName);
		
		objectOutputStream.writeObject(payload);

		System.out.println(LOG + "waiting for response...");
		boolean proceed;
		while(ClientSocketListener.syncProtocolFirstResponse == false){
			proceed  = false;
		}
		System.out.println(LOG + "proceed = true");
		proceed = true;
		if(proceed){
			payload = receiveMessage();
			if(payload.getStatus() == StatusType.FILE_EXISTS){
				/*
				 * get the SCT 
				 */
				List<Long> serverChecksumTable = payload.getServerChecksumTable();
				System.out.println(LOG + "SCT size = " + serverChecksumTable.size());
				/*
				 * prepare IS by sending the SCT to function of preparing IS.
				 */
				List<Map.Entry<Integer, Long>> instructionStream = DeltaSync.getInstructionStream(serverChecksumTable, clientFile);
				/*
				 * put the instructionStream on the outputStream
				 */
				Payload instructionStreamPayload = new Payload(fileName, String.valueOf(DeltaSync.totalDataTransferred), "SYNC_IS");
				instructionStreamPayload.setSource(messageSource);
				instructionStreamPayload.setStatusType(StatusType.SYNC_IS);
				instructionStreamPayload.setInstructionStream(instructionStream);
				
				objectOutputStream.writeObject(instructionStreamPayload);
				
				while(ClientSocketListener.syncProtocolSecondResponse != true){
					proceed = false;
				}
				proceed = true;
				if(proceed){
					payload = receiveMessage();
					
				}

			}
			else{
				/*
				 * TODO send complete file.
				 */
			}
		}
		return payload;
	}

	private Payload receiveMessage(){
		return clientSocketListener.getPayload();
	}


}
