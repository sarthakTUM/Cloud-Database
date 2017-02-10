package app_kvServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Queue;

import common.messages.Payload;

public class OutputHandler implements Runnable{

	private Long clientID;
	private Queue<DatabaseResponse> outputQueue;
	private boolean isRunning;
	private Long outputHandlerId;
	private ObjectOutputStream objectOutputStream;
	private static final String LOG = "LOG:OUTPHNDLR:";
	
	public OutputHandler(Long clientID, Queue<DatabaseResponse> outputQueue, ObjectOutputStream objectOutputStream){
		System.out.println(LOG + "Creating job handler for the client : " + clientID);
		this.clientID = clientID;
		//this.jobQueue = jobQueue;
		this.outputQueue = outputQueue;
		this.objectOutputStream = objectOutputStream;
		this.isRunning = false;
		
	}
	@Override
	public synchronized void run() {
		
		this.outputHandlerId = Thread.currentThread().getId();
		System.out.println(LOG + "Output Handler <OID> initialized with : <clientID><outputQueue> " +  " " + this.clientID + " " + this.outputQueue.hashCode());
		System.out.println(LOG + "Starting OP handler ID : " + outputHandlerId + " for client ID : " + this.clientID);
		this.isRunning = true;
		while(isRunning){
			if(outputQueue.peek() != null){
				System.out.println(LOG + "outputQueue not empty");
				
				DatabaseResponse response = outputQueue.poll();
				System.out.println(LOG + "polled a payload from OID: " + outputHandlerId);
				if(response != null){
					/*
					 * TODO 
					 * Add a timeOut if the payload is taking time to process.
					 */
					System.out.println(LOG + "message on outputQueue for CID: " + this.clientID);

					


					Payload payload = new Payload(response);
					//if(payload.getRequestType() == "SYNC"){
					
					
					try {
						objectOutputStream.writeObject(payload);
						objectOutputStream.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(LOG + "payload flushed to client");
				}
			}
			//System.out.println(LOG + "jobQueue empty");
		}
	}

}
