package app_kvServer;

import java.util.LinkedList;
import java.util.Queue;

import common.messages.Payload;

public class FIFOQueueWrapper implements QueueWrapper<Payload>{

	private Queue<Payload> defaultFIFOQueue = new LinkedList<Payload>();
	@Override
	public void push(Payload payload) {
		// TODO Auto-generated method stub
		defaultFIFOQueue.add(payload);
		
	}
	@Override
	public void push(Payload payload, long ID) {
		// TODO Auto-generated method stub
		QueueManager.getJobQueue(ID).add(payload);
		
	}

}
