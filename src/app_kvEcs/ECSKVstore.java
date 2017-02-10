package app_kvEcs;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import common.messages.*;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import client.KVCommInterface;
//import utils.Logging;
//import Processing.DataManager;
//import Processing.Message;
//import utils.Logging;
import common.messages.KVMessage;

public class ECSKVstore {

	
	private  static Socket clientSocket;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;

	private static OutputStream oStream;
	private static InputStream iStream;
	private static BufferedInputStream bis;
	private static BufferedOutputStream bos;
	private static boolean connectionMade = false;
	private String command;
	private final String delimiter = " ";
	private static String message;
	private static String responseMessage;
	private KVMessage kvMessage;
	private String address;
	private int port;
	
	
	/**
	 * Initialize KVStore with address and port of KVServer
	 * @param address the address of the KVServer
	 * @param port the port of the KVServer
	 */
	public static String returnResponse()
	{
		return responseMessage;
	}
	
	public static void connect(String ip, int port) throws Exception {
		// TODO Auto-generated method stub
		clientSocket = new Socket(ip, port);
		
		if(clientSocket.isConnected()){
			
			connectionMade = true;
			//Logging.FILE_LOGGER.debug("Connection is Successful");
		}
		else{

			connectionMade = false;
			//Logging.FILE_LOGGER.debug("Connection is unsuccessful");
		}

		iStream = clientSocket.getInputStream();
		bis = new BufferedInputStream(iStream);
		oos= new ObjectOutputStream(oStream);
		oos.flush();
		ois= new ObjectInputStream(iStream);

		//Logging.FILE_LOGGER.debug("GET Input Stream for the Socket");

		oStream = clientSocket.getOutputStream();
		bos = new BufferedOutputStream(oStream);
		//Logging.FILE_LOGGER.debug("GET Output Stream for the Socket");
		Thread.sleep(1000);
		String receivedMessage = receiveMessage();
		System.out.println("RM : " + receivedMessage);
		
	}

	public static void disconnect() throws Exception{
		// TODO Auto-generated method stub
		if(clientSocket != null){
			
				clientSocket.close();
				oStream.close();
				iStream.close();
				bis.close();
				bos.close();
			
			
			if(clientSocket.isClosed()){
				connectionMade = false;
				//Logging.FILE_LOGGER.debug("Socket is closed");
			}
		}
		
	}
	public static boolean connectionExists(){

		return connectionMade;

	}

	public static void put(Payload message) throws Exception {
		// TODO Auto-generated method stub
		
		//if(value == null){
			//message = "@" + delimiter + command + delimiter + data;
		//}
		/*else{
			message = command + delimiter + key; 
		}*/
		System.out.println(message);
		byte[] rawBytes = convertToBytes(message);
		oos.writeObject(message);
		oos.flush();
		
		//receive KVMessage from server.
		int attempts=0;
		while(ois.available() == 0 && attempts < 100)
        {
            attempts++;
            Thread.sleep(10);
        }
		
		
		String receivedMessage =  receiveMessage();
		responseMessage= receivedMessage;
		
		System.out.println("RM : " + receivedMessage);
		
		//call KVMessage instance with receivedMessage as argument.
		//KVMessage kvm = new KVMessageClass(receivedMessage);
		//return kvm;
		
	}

	/*public KVMessage get(String key) throws Exception {
		// TODO Auto-generated method stub
		command = "@ get";
		message = command + delimiter + key;
		byte[] rawBytes = convertToBytes(message);
		bos.write(rawBytes, 0, rawBytes.length);
		bos.write(13);
		
		//receive KVMessage from server.
		String receivedMessage = receiveMessage();
		System.out.println("RM 1: " + receivedMessage);
		//call KVMessage instance with receivedMessage as argument.
		KVMessage kvm = new KVMessageClass(receivedMessage);
		return kvm;
	}*/
	
	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	private static byte[] convertToBytes(String message){
		byte[] rawBytes = message.getBytes();
		return rawBytes;
	}
	
	public static String receiveMessage() throws ClassNotFoundException, IOException{
Payload payload=(Payload)ois.readObject();
return payload.getValue();

		/*List<Integer> receivedMessageInBytes = new ArrayList<>();
		String receivedMessageString = null;

		try {
			
			if(bis.available() != 0){

				int c; 
				while((c = bis.read()) != 13){
					System.out.print((char)c + " ");		
					receivedMessageInBytes.add(c);
				}
				
				receivedMessageString = convertIntegerToString(receivedMessageInBytes);
				
				//Message.setUserResponseMessage(receivedMessageString);

				//Logging.FILE_LOGGER.debug("Server Response: " + receivedMessageString);

			}
			else{
				//Logging.FILE_LOGGER.debug("No server response in InputStream");
				System.out.println("nothing");
			}
		} catch (IOException e) {
			e.printStackTrace();
			//Logging.FILE_LOGGER.error("IOException occurred during receiving message");
		}
		System.out.println("RMC : " + receivedMessageString);
		return receivedMessageString;
		*/
	}
	
	private static String convertIntegerToString(List<Integer> receivedMessageInBytes){

		String receivedMessage = new String();
		
		StringBuilder strbul  = new StringBuilder();
	    Iterator<Integer> iter = receivedMessageInBytes.iterator();
	     while(iter.hasNext())
	     {
	        strbul.append((char)((int)iter.next()));
	        
	     }
	    receivedMessage = strbul.toString();
		System.out.println("SB : " + receivedMessage);
		return receivedMessage;
		
		
	}


	
}