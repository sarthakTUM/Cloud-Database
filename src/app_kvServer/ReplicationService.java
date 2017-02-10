package app_kvServer;

import java.util.List;

import javafx.util.Pair;

import common.messages.KVMessage;

public class ReplicationService {

	private static ServerContainerModel metadata = ServerSystem.getMetadata();
	private static int numberOfReplications = ServerSystem.getReplicationCount();
	public static void replicate(String key, String value){
		ServerContainerModel replicationNode = metadata.getNextNnodes(ServerSystem.getIdentity().getIP(), ServerSystem.getIdentity().getPort(), numberOfReplications);
		for(int node = 0; node<replicationNode.count(); node++){
			ServerModel toReplicateOnNode = replicationNode.getServerByIndex(node);
			String serverIP = toReplicateOnNode.getIP();
			int serverPort = toReplicateOnNode.getPort();
			
			ServerCommunication serverComm = new ServerCommunication(serverIP, serverPort);
			try {
				serverComm.connect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try {
					KVMessage kvMessage = serverComm.put(key, value);
					if(kvMessage != null){
						switch(kvMessage.getStatus()){
						case PUT_ERROR:
							break;
						case PUT_SUCCESS:
							break;
						case PUT_UPDATE:
							break;
							default:
								/*
								 * TODO unknown status
								 */
								break;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					serverComm.disconnect();
				}
			}
		}
	}
	
	public static void replicate(List<Pair<String, String>> records, ServerModel toReplicateOnNode){
		ServerCommunication serverComm = new ServerCommunication(toReplicateOnNode.getIP(), toReplicateOnNode.getPort());
		try {
			serverComm.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				KVMessage kvMessage = null;
				for(int index = 0; index<records.size(); index++){
					kvMessage = serverComm.put(records.get(index).getKey(), records.get(index).getValue());;
				}
				
				if(kvMessage != null){
					switch(kvMessage.getStatus()){
					case PUT_ERROR:
						break;
					case PUT_SUCCESS:
						break;
					case PUT_UPDATE:
						break;
						default:
							/*
							 * TODO unknown status
							 */
							break;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				serverComm.disconnect();
			}
		}
	}
}
