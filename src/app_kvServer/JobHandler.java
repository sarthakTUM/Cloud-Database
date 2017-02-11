/**
 * @author Sarthak Gupta
 * A thread that constantly polls from the job Queue to fetch the latest requests that server
 * received.
 */

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
	
	/**
	 * 
	 * @param clientID The client to which this Job Handler will be attached to.
	 * @param jobQueue the queue which is associated with the client, which will be polled by the Job Handler
	 * @param outputQueue The queue to which Handler will reroute the outputs by the server.
	 */
	public JobHandler(Long clientID, Queue<Payload> jobQueue, Queue<DatabaseResponse> outputQueue){
		System.out.println(LOG + "Creating job handler for the client : " + clientID);
		this.clientID = clientID;
		this.jobQueue = jobQueue;
		this.outputQueue = outputQueue;
		this.isRunning = false;
		
	}
	@Override
	public synchronized void run() {
		
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
	
					DatabaseResponse response = Processor.process(payload);
					if(response != null){
						
						
						System.out.println(LOG + "response from server != null");
						System.out.println(LOG + response.getReuqestType() + " " + response.getStatus());
						boolean isAdded = outputQueue.offer(response);
						
						System.out.println(LOG + "response added to outputQueue?: " + isAdded);
						
					}
				}
			}
		}
	}

	
}
