package app_kvServer;

import java.util.Queue;

import common.messages.Payload;

public class JobHandler implements Runnable{

	private Long clientID;
	private Queue<Payload> jobQueue;
	private Queue<DatabaseResponse> outputQueue;
	private boolean isRunning;
	private Long jobHandlerID;
	private static final String LOG = "LOG:JOBHNDLR:";
	
	public JobHandler(Long clientID, Queue<Payload> jobQueue, Queue<DatabaseResponse> outputQueue){
		System.out.println(LOG + "Creating job handler for the client : " + clientID);
		this.clientID = clientID;
		this.jobQueue = jobQueue;
		this.outputQueue = outputQueue;
		this.isRunning = false;
		
	}
	@Override
	public void run() {
		
		this.jobHandlerID = Thread.currentThread().getId();
		System.out.println(LOG + "Job Handler <JID> initialized with : <clientID> <jobQueue> <outputQueue> " + this.jobHandlerID + " " + this.clientID + " " + this.jobQueue.hashCode() + " " + this.outputQueue.hashCode());
		System.out.println(LOG + "Starting job handler ID : " + jobHandlerID + " for client ID : " + this.clientID);
		this.isRunning = true;
		while(isRunning){
			if(jobQueue.peek() != null){
				System.out.println(LOG + "jobQueue not empty");
				
				Payload payload = jobQueue.poll();
				System.out.println(LOG + "polled a payload from JID: " + jobHandlerID);
				if(payload != null){
					/*
					 * TODO 
					 * Add a timeOut if the payload is taking time to process.
					 */
					DatabaseResponse response = Processor.process(payload);
					if(response != null){

						outputQueue.add(response);
						
					}
				}
			}
			//System.out.println(LOG + "jobQueue empty");
		}
	}

	
}
