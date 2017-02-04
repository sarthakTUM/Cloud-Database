package app_kvServer;

import common.messages.Payload;

public class QueueWrapperFactory {

	private static final QueueWrapper<Payload> queueWrapper = new FIFOQueueWrapper();
	//private static final QueueWrapper<DatabaseResponse> outputQueueWrapper = new FIFOQueueWrapper();
	
	public static QueueWrapper<Payload> getQueueWrapper(){
		return queueWrapper;
	}
}
