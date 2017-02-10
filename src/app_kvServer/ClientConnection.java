package app_kvServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;

import app_kvServer.DataStoreWrapper.DBCommand;
import app_kvServer.DataStoreWrapper.DBRequestResult;
import client.SocketListener;
import common.messages.KVMessage.StatusType;
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
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	private Payload payload;
	private JobHandler jobHandler;
	private OutputHandler outputHandler;
	private Long clientID;

	private  Queue<DatabaseResponse> outputQueue;
	private  Queue<Payload> jobQueue;

	public ClientConnection(Socket clientSocket) {
		System.out.println(LOG + "new connection arrived from : " + clientSocket.getLocalSocketAddress());
		this.clientSocket = clientSocket;

		System.out.println(LOG + "clientSocket : " + clientSocket);
		this.isOpen = true;

	}

	@Override
	public synchronized void run() {

		try {
			outputStream = clientSocket.getOutputStream();
			inputStream = clientSocket.getInputStream();
			objectOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());

			//synchronized (this) {
			objectOutputStream.flush();
			//}


			//objectOutputStream.close();

			objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.clientID = Thread.currentThread().getId();
		System.out.println(LOG + "clientID: " + clientID);

		QueueManager.createOutputQueue(this.clientID);
		QueueManager.createJobQueue(this.clientID);

		this.outputQueue = QueueManager.getOutputQueue(this.clientID);
		if(outputQueue != null){
			System.out.println(LOG + "created outputqueue");
		}
		this.jobQueue = QueueManager.getJobQueue(this.clientID);

		System.out.println(LOG + "outputQueue for client ID: " + this.clientID + " is : " + outputQueue.hashCode());
		System.out.println(LOG + "jobQueue for client ID: " + this.clientID + " is : " + jobQueue.hashCode());

		this.jobHandler = new JobHandler(clientID, jobQueue, outputQueue);
		new Thread(jobHandler).start();
		
		this.outputHandler = new OutputHandler(clientID, outputQueue, objectOutputStream);
		new Thread(outputHandler).start();

		try {

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
				//System.out.println(LOG + "here");
				
					
				
					//}
					/*
					 * TODO check the payload status, if the request can be re-issued at server end itself.
					 */


					/*else{
						byte[] serializedMessage = new Message(payload).serializeMessage();
						if(outputStream != null){
							outputStream.write(serializedMessage);
							outputStream.write(13);
							outputStream.flush();
						}
					}*/


				
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
		// TODO Auto-generated method stub
		Payload payload = null;

		//if(objectInputStream.available() != 0){
		/*
		 * TODO data is available, read it.
		 */
		try {
			//synchronized (this) {
			payload = (Payload) objectInputStream.readObject();
			//}


		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//}
		return payload;
		
	}

}
