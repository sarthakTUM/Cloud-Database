package app_kvServer;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


/**
 * Represents a connection end point for a particular client that is 
 * connected to the server. This class is responsible for message reception 
 * and sending. 
 * The class also implements the echo functionality. Thus whenever a message 
 * is received it is going to be echoed back to the client.
 * @param <KVServer>
 */
public class ClientConnection implements Runnable {

	// Private static Logger log = Logger.getRootLogger ();
	private boolean isOpen;
	private static final int BUFFER_SIZE = 1024;
	private static final int DROP_SIZE = 128 * BUFFER_SIZE;
	public String status;
	public String response;
	private Socket clientSocket;
	private InputStream input;
	private OutputStream output;
	private KVServer serverObj;
	private String reply;
	String data;

	
	/**
	 * Constructs a new CientConnection object for a given TCP socket.
	 * @param clientSocket the Socket object for the client connection.
	 */
	public ClientConnection(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.isOpen = true;
	}
	
	/**
	 * Initializes and starts the client connection. 
	 * Loops until the connection is closed or aborted by the client.
	 */
	
	private String getValue(String[] tokens, int startpos)
	{
		StringBuilder builder = new StringBuilder();
		for (int i=startpos; i<tokens.length; i++)
		{
			builder.append(tokens[i]);
		}
		return builder.toString();
	}
	
	
	
	public void run() {
		try {
			
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();
		
			/*sendMessage(new TextMessage(
					"HIHIHIHI " 
					+ clientSocket.getLocalAddress () + "/"
					+ clientSocket.getLocalPort()));*/
			sendMessage(new TextMessage(
					"Connection to MSRG Echo server established: " 
					+ clientSocket.getLocalAddress () + "/"
					+ clientSocket.getLocalPort()));
			
			while(isOpen) {
				try {
					TextMessage latestMsg = receiveMessage();
					data = latestMsg.getMsg();
					String[] tokens= data.split(" ");
					//sendMessage(new TextMessage("while"));
					if(tokens[0].equals("@"))
					{
						processECSCmd(tokens);
						
					}
					
					else
					{
						if(KVServer.isServerActiveCheck())
						{
							processClientCmd(tokens);
						}// send error 
					}
					//toLowercase
					
			
		} catch (IOException yes) {
			//logger.error("Error! Connection could not be established!", ioe);
			
		} finally {
			
			try {
				if (clientSocket != null) {
					input.close();
					output.close();
					clientSocket.close();
					sendMessage(new TextMessage("closed while"));
				}
			} catch (IOException yes) {
				//logger.error("Error! Unable to tear down connection!", ioe);
			}
		}
	}
	
	/**
	 * Method sends a TextMessage using this socket.
	 * @param msg the message that is to be sent.
	 * @throws IOException some I/O error regarding the output stream 
	 */
	

	
	
	public void sendMessage(TextMessage msg) throws IOException {
		byte[] msgBytes = msg.getMsgBytes();
		output.write(msgBytes, 0, msgBytes.length);
		output.flush();
		/*//logger.info("SEND \t<" 
				+ clientSocket.getInetAddress().getHostAddress() + ":" 
				+ clientSocket.getPort() + ">: '" 
				+ msg.getMsg() +"'");*/
    }
	
	
	private TextMessage receiveMessage() throws IOException {
		
		int index = 0;
		byte[] msgBytes = null, tmp = null;
		byte[] bufferBytes = new byte[BUFFER_SIZE];
		
		/* read first char from stream */
		byte read = (byte) input.read();	
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
			
			/* only read valid characters, i.e. letters and constants */
			bufferBytes[index] = read;
			index++;
			
			/* stop reading is DROP_SIZE is reached */
			if(msgBytes != null && msgBytes.length + index >= DROP_SIZE) {
				reading = false;
			}
			
			/* read next char from stream */
			read = (byte) input.read();
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
		
		/* build final String */
		TextMessage msg = new TextMessage(msgBytes);
		/*logger.info("RECEIVE \t<" 
				+ clientSocket.getInetAddress().getHostAddress() + ":" 
				+ clientSocket.getPort() + ">: '" 
				+ msg.getMsg().trim() + "'");*/
		//System.out.println("msg : " + msg);
		return msg;
    }
	
	private void processClientCmd(String[] tokens)
	{
		
		if (tokens[0].equals("get"))
		{
	//cache check
				 response=ServerKVStore.get(tokens[1]);//include status message
				 status=ServerKVStore.getStatus();
				
		}		
		else if (tokens[0].equals("put"))
		{
		//cache check
			if (tokens.length>2)
			{
				System.out.println("length greater than 2");
		String putV=getValue(tokens, 2);
				ServerKVStore.put(tokens[1],putV);//add value to storage
				status=ServerKVStore.getStatus();		
				response=tokens[1]+" "+putV;
				System.out.println("response : " + response);
			}
			else{
	 ServerKVStore.put(tokens[1]);//delete the key in the storage
	status=ServerKVStore.getStatus();		
			response=tokens[1]+" ";
			}
		}	
	
	
		TextMessage sendMsg = new TextMessage(status+" "+response);
		ServerKVStore.setStatus("");
		try {
			sendMessage(sendMsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
/* connection either terminated by the client or lost due to 
 * network problems*/	

	}
	private void processECSCmd(String[] tokens){
		
		switch(tokens[1]){
		case "start":
			KVServer.serveractivecheck = true;
			break;
			
		case "stop":
			KVServer.serveractivecheck = false;
			
			break;
		
		case "shutdown":
			System.exit(1);
		break;
		case "add":
		break;
		case "remove":
			break;
		case "metadata":
			if(tokens.length>2)
			{
				KVServer.setMetadata(data.substring(7));
			}
			break;
		
	}
	
		}
		
		
	}
}