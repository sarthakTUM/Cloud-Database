package app_kvServer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javafx.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;

import app_kvServer.DataStoreWrapper.DBCommand;
import app_kvServer.DataStoreWrapper.DBRequestResult;
import app_kvServer.DataStoreWrapperFactory.DataStore;
import common.messages.DeltaSync;
import common.messages.KVMessage.MessageSource;
import common.messages.KVMessage.StatusType;
import common.messages.Payload;

public class Processor {

	private static final String LOG = "LOG:PROCESOR:";
	
	/*
	 * TODO for each message, check server's state first, and also, update state as required.
	 */
	public static DatabaseResponse process(Payload payload){
		
		System.out.println(LOG + "processing payload");
		
		MessageSource messageSource = payload.getMessageSource();
		System.out.println(LOG + "message source: " + messageSource);
		DatabaseResponse databaseResponse = null;
		switch(messageSource){
		case CLIENT:
			//processClientMessage(payload);
			boolean serverResponsible = true;
			//serverResponsible = checkServerResponsibility(payload.getKey());
			if(!serverResponsible){

				databaseResponse = new DatabaseResponse();
				databaseResponse.setKey(payload.getKey());
				databaseResponse.setMessage("Server not responsible");
				databaseResponse.setResult(DBRequestResult.FAIL);
				databaseResponse.setReuqestType(DBCommand.valueOf(payload.getRequestType()));
				databaseResponse.setStatus(StatusType.SERVER_NOT_RESPONSIBLE);
				//databaseResponse.setValue(payload.getValue());
				/*
				 * TODO add current metadata as well.
				 */
				return databaseResponse;
			}
			String instruction = payload.getRequestType();
			System.out.println(LOG + "payload instruction : " + instruction);
			switch(instruction){
			case "GET":
				
				/*
				 * TODO check if key is available in cache, else, go for the database.
				 * 
				 */
				//if(ServerSystem.isInitialized()){
					databaseResponse = DataStoreWrapperFactory.getDataStore(DataStore.XMLStore).get(payload.getKey());
				//}
				//else{
					/*
					 * TODO give a response that server is not yet responsible.
					 */
				//}
				
				
				
				break;
			case "PUT":
				
				/*
				 * TODO 1. check if key is available in cache, else, go for the database.
				 */
				//if(ServerSystem.isInitialized() && !ServerSystem.isWriteLocked()){
					databaseResponse = DataStoreWrapperFactory.getDataStore(DataStore.XMLStore).put(payload.getKey(), payload.getValue());
					//if(ServerSystem.getReplicationEnabled()){
						//ReplicationService.replicate(payload.getKey(), payload.getValue());
					//}
				//}
				//else{
					/*
					 * TODO give the response that server is either write_locked or not accepting any connections.
					 */
				//}
				
				break;
			case "SYNC":
				File directory = new File(String.valueOf("SarthakGupta"));
			    if (! directory.exists()){
			        directory.mkdir();
			        // If you require it to make the entire directory path including parents,
			        // use directory.mkdirs(); here instead.
			    }
			    else{
			    	String fileName = payload.getKey();
			    	File f = new File("SarthakGupta//" + fileName);
			    	if(f.exists() && !f.isDirectory()) { 
			    	    // do something
			    		/*
			    		 * TODO create SCT of that file.
			    		 */
			    		System.out.println(LOG + "<file> exists and it is not a directory: " + fileName);
			    		List<Long> serverChecksumTable = DeltaSync.createSCT(f);
			    		databaseResponse = new DatabaseResponse();
			    		databaseResponse.setReuqestType(DBCommand.SYNC);
			    		databaseResponse.setKey(payload.getKey());
			    		databaseResponse.setResult(DBRequestResult.SUCCESS);
			    		databaseResponse.setStatus(StatusType.FILE_EXISTS);
			    		databaseResponse.setValue("null");
			    		databaseResponse.setServerChecksumTable(serverChecksumTable);
			    		
			    		return databaseResponse;
			    		
			    		
			    	}
			    	else{
			    		/*
			    		 * TODO file needs complete replication.
			    		 */
			    	}
			    }
				break;
			case "SYNC_IS":
				String fileName = payload.getKey();
				File f = new File("SarthakGupta//" + fileName);
				try {
					DeltaSync.constructFile(payload.getInstructionStream(), f);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				default:
					break;
			}
			break;
		case CLUSTER:
			//processClusterMessage(payload);
			break;
		case ECS:
			//processECSMessage(payload);


			switch(payload.getRequestType()){
			case "start":
				System.out.println(LOG + "START from ECS. Updating the System state of Server listening on port: " + ServerSystem.getIdentity().getPort());
				ServerSystem.setInitialized(true);
				ServerSystem.setClosed(false);
				ServerSystem.setHalted(false);
				ServerSystem.setRunning(true);
				ServerSystem.setShutDown(false);
				ServerSystem.setWriteLocked(false);
				
				
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
				 *
				 * 3. ServerModel node = serverContainerModel.getServerByIndex(0);
				 * 4. call get(start, end) with start and end range of node(3) which will return List<K,V> of current Server to be transferred to node(3).
				 * 5. connect to node from (3) 
				 * 6. pass the List to node(3).
				 * 7. call put(start, end) with the same range as retreived in (4): used for deletion.
				 * For this, get node number of this.server by 
				 * 3. 
				 */
				
				ServerContainerModel nextNNodes = new ServerContainerModel().getNextNnodes(ServerSystem.getIdentity(), 1);
				ServerModel node = nextNNodes.getServerByIndex(0);
				try {
					List<Pair<String, String>> records = DataStoreWrapperFactory.getDataStore(DataStore.XMLStore).get(node.getStartIndex(), node.getEndIndex());
					ReplicationService.replicate(records, node);
				} catch (NoSuchAlgorithmException
						| UnsupportedEncodingException | DOMException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				break;
			case "REMOVE":
				
				break;
			case "META":
				try {
					ServerSystem.getMetadata().cnvMetaToServList(payload.getValue());
					ServerSystem.setServerIdentity(ServerSystem.getMetadata().getServerByPortAndIP(ServerSystem.getIdentity().getIP(), ServerSystem.getIdentity().getPort()));
					ServerSystem.setReplicatedServerList(ServerSystem.getMetadata().getPrevNnodes(ServerSystem.getIdentity().getIP(), ServerSystem.getIdentity().getPort(), 2));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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

	private static boolean checkServerResponsibility(String key) {
		
		boolean isResponsible = false;
		try {
			if(!ServerSystem.getIdentity().isResponsible(key)){
				isResponsible = true;
				return isResponsible;
			}
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ServerContainerModel replicatedServers = ServerSystem.getReplicatedServerList();
		for(int i=0; i<replicatedServers.count(); i++){
			ServerModel replicatedServer = replicatedServers.getServerByIndex(i);
			try {
				if(replicatedServer.isResponsible(key)){
					isResponsible = true;
					break;
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isResponsible;
		// TODO Auto-generated method stub
		
		
	}
	
}
