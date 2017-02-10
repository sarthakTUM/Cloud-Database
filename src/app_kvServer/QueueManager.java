package app_kvServer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import common.messages.Payload;

public class QueueManager{

	/*
	 * TODO create an output queue map.
	 */
	private static HashMap<Long, Queue<Payload>> jobQueue = new HashMap<>();
	private static HashMap<Long, Queue<DatabaseResponse>> outputQueue = new HashMap<>();
	
	public static Queue<Payload> getJobQueue(Long ID){
		return jobQueue.get(ID);
	}
	public static Queue<DatabaseResponse> getOutputQueue(Long ID){
		return outputQueue.get(ID);
	}
	
	public static void createOutputQueue(Long ID){
		outputQueue.put(ID, new ConcurrentLinkedQueue<DatabaseResponse>());
	}
	public static void createJobQueue(Long ID){
		jobQueue.put(ID, new ConcurrentLinkedQueue<Payload>());
	}
	
}
