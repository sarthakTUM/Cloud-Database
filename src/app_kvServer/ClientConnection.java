package app_kvServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;

import client.SocketListener;
import common.messages.Message;
import common.messages.Payload;

public class ClientConnection implements Runnable, SocketListener{

	//private static Logger logger = Logger.getRootLogger();
	private final static String LOG = "LOG:CLNTCONN:";
	private boolean isOpen;
	private static final int BUFFER_SIZE = 1024;
	private static final int DROP_SIZE = 128 * BUFFER_SIZE;
	
	private Socket clientSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Payload payload;
	private JobHandler jobHandler;
	private Long clientID;
	
	private volatile Queue<DatabaseResponse> outputQueue;
	private volatile Queue<Payload> jobQueue;
	
	public ClientConnection(Socket clientSocket) {
		System.out.println(LOG + "new connection arrived from : " + clientSocket.getLocalSocketAddress());
		this.clientSocket = clientSocket;
		
		System.out.println(LOG + "clientSocket : " + clientSocket);
		this.isOpen = true;
	}
	
	@Override
	public void run() {

		this.clientID = Thread.currentThread().getId();
		System.out.println(LOG + "clientID: " + clientID);
		
		QueueManager.createOutputQueue(this.clientID);
		QueueManager.createJobQueue(this.clientID);
		
		this.outputQueue = QueueManager.getOutputQueue(this.clientID);
		this.jobQueue = QueueManager.getJobQueue(this.clientID);
		
		System.out.println(LOG + "outputQueue for client ID: " + this.clientID + " is : " + outputQueue.hashCode());
		System.out.println(LOG + "jobQueue for client ID: " + this.clientID + " is : " + jobQueue.hashCode());
		
		this.jobHandler = new JobHandler(clientID, jobQueue, outputQueue);
		new Thread(jobHandler).start();
		
		try {
			inputStream = clientSocket.getInputStream();
			outputStream = clientSocket.getOutputStream();
			while(isOpen){
				this.payload = receiveMessage();
				if(payload != null){
					
					boolean isAdded = jobQueue.offer(payload);
					if(isAdded){
						System.out.println(LOG + "received message on <CID> and pushed to JobQueue: " + this.clientID);
					}
					else{
						System.out.println(LOG + "payload could not be pushed to the queue of <CID>: " + this.clientID);
					}
				}
				if(outputQueue.peek() != null){
					System.out.println(LOG + "message on outputQueue for CID: " + this.clientID);
					DatabaseResponse response = outputQueue.poll();
	
					Payload payload = new Payload(response);
					/*
					 * TODO check the payload status, if the request can be re-issued at server end itself.
					 */
					byte[] serializedMessage = new Message(payload).serializeMessage();
					if(outputStream != null){
						outputStream.write(serializedMessage);
						outputStream.write(13);
						outputStream.flush();
					}
					
				}
			}
		} catch (IOException e) {
	
			e.printStackTrace();
		}
		
	}

	@Override
	public void handleStatus(SocketState socketState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Payload receiveMessage() throws IOException {
		
		Payload payload = null;
		if(inputStream.available() != 0){
			/*
			 * TODO data is available, read it.
			 */
			System.out.println(LOG + "bytes on inputStream for <CID> available : " + this.clientID);
			int index = 0;
			byte[] msgBytes = null, tmp = null;
			byte[] bufferBytes = new byte[BUFFER_SIZE];
			
			/* read first char from stream */
			byte read = (byte) inputStream.read();	
			boolean reading = true;
			
			while(read != 13 && reading) {/* carriage return */
				/* if buffer filled, copy to msg array */
				System.out.println(LOG + "reading input bytes CID: " + this.clientID);
				if(index == BUFFER_SIZE) {
					System.out.println(LOG + "index == buffer_size");
					if(msgBytes == null){
						System.out.println(LOG + msgBytes == null);
						tmp = new byte[BUFFER_SIZE];
						System.arraycopy(bufferBytes, 0, tmp, 0, BUFFER_SIZE);
					} else {
						System.out.println(LOG + "msgBytes != null");
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
					System.out.println(LOG + "read a valid character: " + read + ":" + (char)read);
					bufferBytes[index] = read;
					index++;
					System.out.println(LOG + "index: " + index);
				}
				
				/* stop reading is DROP_SIZE is reached */
				if(msgBytes != null && msgBytes.length + index >= DROP_SIZE) {
					System.out.println(LOG + "drop_size reached");
					reading = false;
				}
				
				/* read next char from stream */
				read = (byte) inputStream.read();
			}
			System.out.println(LOG + "exiting loop");
			if(msgBytes == null){
				System.out.println(LOG + "msgBytes == null");
				tmp = new byte[index];
				System.arraycopy(bufferBytes, 0, tmp, 0, index);
			} else {
				System.out.println(LOG + "msgBytes != null");
				tmp = new byte[msgBytes.length + index];
				System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
				System.arraycopy(bufferBytes, 0, tmp, msgBytes.length, index);
				System.out.println(LOG + "arrayCopy done");
			}
			
			msgBytes = tmp;
			System.out.println(LOG + "message bytes : " + msgBytes + " length : " + msgBytes.length);
			payload = new Payload(msgBytes);
			System.out.println(LOG + "payload created with : " + payload.getRequestType() + ":" + payload.getKey() + ":" + payload.getValue());
			
			
		}
		return payload;
	}

}
