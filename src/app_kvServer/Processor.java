package app_kvServer;

import app_kvServer.DataStoreWrapperFactory.DataStore;
import common.messages.KVMessage.MessageSource;
import common.messages.Payload;

public class Processor {

	private static final String LOG = "LOG:PROCESOR:";
	
	/*
	 * TODO for each message, check server's state first, and also, update state as required.
	 */
	public static DatabaseResponse process(Payload payload){
		
		System.out.println(LOG + "processing payload");
		
		MessageSource messageSource = payload.getMessageSource();
		System.out.println("message source: " + messageSource);
		DatabaseResponse databaseResponse = null;
		switch(messageSource){
		case CLIENT:
			//processClientMessage(payload);
			String instruction = payload.getRequestType();
			System.out.println("payload instruction : " + instruction);
			switch(instruction){
			case "GET":
				
				/*
				 * TODO check if key is available in cache, else, go for the database.
				 */
				databaseResponse = DataStoreWrapperFactory.getDataStore(DataStore.XMLStore).get(payload.getKey());
				
				
				break;
			case "PUT":
				
				/*
				 * TODO 1. check if key is available in cache, else, go for the database.
				 * 2. Replicate the data by calling NextNNodes.
				 */
				databaseResponse = DataStoreWrapperFactory.getDataStore(DataStore.XMLStore).put(payload.getKey(), payload.getValue());
				
				break;
				
				default:
					break;
			}
			break;
		case CLUSTER:
			//processClusterMessage(payload);
			break;
		case ECSCLIENT:
			//processECSMessage(payload);
			
			/*
			 * TODO create a Global Server state class to query for start, stop, shutdown, etc.
			 */
			/*
			 * TODO Create a metadata instance using ServerContainerModel - dev/Anant
			 */
			switch(payload.getRequestType()){
			case "START":
				break;
			case "STOP":
				break;
			case "SHUTDOWN":
				/*
				 * TODO deallocate memory
				 */
				break;
			case "ADD":
				
				/*
				 * LOGIC:
				 * 1. Pass current IP and port to getServerModel and get the ServerModel which is the identity of current server and store it as a global identity.
				 * 2. call getNextNNode(ServerModel, 1) which will return ServerContainerModel.
				 * 3. ServerModel node = serverContainerModel.getServerByIndex(0);
				 * 4. call get(start, end) with start and end range of node(3) which willr eturn List<K,V> of current Server to be transferred to node(3).
				 * 5. connect to node from (3) 
				 * 6. pass the List to node(3).
				 * 7. call put(start, end) with the same range as retreived in (4).
				 * For this, get node number of this.server by 
				 * 3. 
				 */
				break;
			case "REMOVE":
				
				break;
			case "META":
				
				ServerContainerModel metadata = ServerSystem.getMetadata();
				/*
				 * TODO create a MISC field in Payload to store Metadata.
				 */
				
				// gets the metadata from misc and updates using ServerContainerModel instance.
				//metadata.cnvMetaToServList(payload.getMisc());
				
				/*
				 *metadata.
				 * metadata = payload.getMisc();
				 */
				
				break;
				default:
					/*
					 * TODO command not valid.
					 * 
					 */
					break;
			}
			break;
		default:
			/*
			 * Invalid message.
			 */
			break;
		
			
		}
		
		return databaseResponse;
	}
	
}
