/**
 * @author Sarthak Gupta
 */
package app_kvClient;

//import common.messages.KVMessage;

import java.io.File;
import java.io.IOException;

import app_kvClient.Response.ResponseResult;
import app_kvClient.Response.ResponseSource;
import app_kvServer.ServerContainerModel;
import app_kvServer.ServerModel;
import client.KVStore;
import client.Metadata;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;

/*
 * TODO for all messages, extract the valid server from metadata.
 */
public class ServerMessageHandler implements Handler{

	private static final String LOG = "LOG:SMHANDLR:";

	private static KVStore kvStore;
	public Response processCommand(CommandModel serverCommand){
		Response response = null;
		//KVMessage kvMessage;
		System.out.println(LOG + "Command Model Class: " + serverCommand.getClass().getSimpleName());
		switch(serverCommand.getCommandInstruction()){
		case "CONNECT":
			// TODO call KVStore  connect method
			boolean isConnected = true;
			System.out.println(LOG + "executing CONNECT command");
			String serverAddress = serverCommand.getCommandAttributes()[1];
			int serverPort = Integer.valueOf(serverCommand.getCommandAttributes()[2]);
			kvStore = new KVStore(serverAddress, serverPort);
			try {
				kvStore.connect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				isConnected = false;

				e.printStackTrace();
			}
			finally{
				if(isConnected){
					response = new Response(ResponseSource.SERVER, ResponseResult.SUCCESS, "Connection established");
				}
				else{
					response = new Response(ResponseSource.SERVER, ResponseResult.FAIL, "Could not establish connection");
				}
			}
			break;
		case "PUT":
			String key = serverCommand.getCommandAttributes()[1];
			String value = serverCommand.getCommandAttributes()[2];

			System.out.println(LOG + "executing PUT: " + key + "-" + value);
			try {
				/*
				 * TODO receive KVMessage and based on the status, build a response. execute proper action.
				 */
				KVMessage kvMessage = kvStore.put(key, value);
				if(kvMessage != null){
					System.out.println(LOG + "matching statustype: " + kvMessage.getStatus());
					switch(kvMessage.getStatus()){
					case DELETE_ERROR:
						break;
					case DELETE_SUCCESS:
						break;
					case GET:
						break;
					case GET_ERROR:
						break;
					case GET_SUCCESS:
						break;
					case PUT:
						break;
					case PUT_ERROR:
						response = new Response(ResponseSource.SERVER, ResponseResult.FAIL, "PUT request not successful. Please try again");
						break;
					case PUT_SUCCESS:
						response = new Response(ResponseSource.SERVER, ResponseResult.SUCCESS, "PUT request successful. Inserted : " + key + ":" + value);
						System.out.println(LOG + "response created : " + response.getResponseResult() + " " + response.getResponseSource());
						break;
					case PUT_UPDATE:
						response = new Response(ResponseSource.SERVER, ResponseResult.SUCCESS, "PUT request successful. Tuple updated : " + key + ":" + value);
						break;
					case SERVER_NOT_RESPONSIBLE:

						/*
						 * retry workflow:
						 * 1. Disconnect from old server
						 * 2. update metadata file
						 * 3. find new server
						 * 4. processCommand() with connect to new server
						 * 5. PUT to new server
						 */
						ClientSystem.getMetadata().cnvMetaToServList(kvMessage.getValue());


						ServerModel serverConfig = new ServerContainerModel().getResponsibleServer(key);
						/*
						 * initiate a new PUT request with this serverConfig.
						 */
						if(serverConfig != null){
							
							response = processCommand(new ServerCommand("SERVER", new ServerMessageHandler(), new String[]{serverConfig.getIP(), String.valueOf(serverConfig.getPort())}, "CONNECT", "SERVER"));
							if(response.getResponseResult() == ResponseResult.SUCCESS){
								
								processCommand(new ServerCommand("SERVER", new ServerMessageHandler(), new String[]{key, value}, "PUT", "SERVER"));
							}
						}

						break;
					case SERVER_STOPPED:
						break;
					case SERVER_WRITE_LOCK:
						break;
					default:
						break;

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(LOG + "could not execute PUT request. Please try again.");
				response = new Response(ResponseSource.SERVER, ResponseResult.FAIL, "Could not execute PUT command. Please try again.");
				e.printStackTrace();
			}
			break;

		case "SYNC":
			try {
				String fileName = serverCommand.getCommandAttributes()[1];
				File f = new File(ClientSystem.clientSyncFolderName + "//" + fileName);
				if(!f.exists()){
					response = new Response(ResponseSource.CLIENT, ResponseResult.FAIL, "File Doesnt exist:" + fileName);
				}
				else{
					KVMessage kvMessage = kvStore.sync(serverCommand.getCommandAttributes()[1]);
					if(kvMessage.getStatus() == StatusType.SYNC_COMPLETE){
						response = new Response(ResponseSource.SERVER, ResponseResult.SUCCESS, "File Synced:" + fileName + " Summary: " + kvMessage.getValue());
					}
				}
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case "GET":
			/*
			 * TODO receive KVMessage and based on the status, build a response, execute proper action.
			 */
			key = serverCommand.getCommandAttributes()[1];
			try {
				KVMessage kvMessage = kvStore.get(key);
				if(kvMessage != null){
					switch(kvMessage.getStatus()){
					case DELETE_ERROR:
						break;
					case DELETE_SUCCESS:
						break;
					case GET:
						break;
					case GET_ERROR:
						break;
					case GET_SUCCESS:
						response = new Response(ResponseSource.SERVER, ResponseResult.SUCCESS, "GET request successful. KEY: " + kvMessage.getKey() + " VALUE: " + kvMessage.getValue());
						break;
					case PUT:
						break;
					case PUT_ERROR:
						break;
					case PUT_SUCCESS:
						break;
					case PUT_UPDATE:
						break;
					case SERVER_NOT_RESPONSIBLE:

						/*
						 * TODO retry workflow:
						 * 1. Disconnect from old server
						 * 2. update metadata file
						 * 3. find new server
						 * 4. processCommand() with connect to new server
						 * 5. PUT to new server
						 */

						boolean isUpdated = Metadata.updateMetadata(fetchMetadata(kvMessage));
						if(isUpdated){
							ServerModel serverConfig = new ServerContainerModel().getResponsibleServer(key);
							/*
							 * initiate a new PUT request with this serverConfig.
							 */
							if(serverConfig != null){
								/*
								 * TODO call processCommand() with a new ServerCommand of connect
								 */

								//uncomment the below line when serverModel is incorporated.
								response = processCommand(new ServerCommand("SERVER", new ServerMessageHandler(), new String[]{serverConfig.getIP(), String.valueOf(serverConfig.getPort())}, "CONNECT", "SERVER"));
								if(response.getResponseResult() == ResponseResult.SUCCESS){
									/*
									 * TODO call processCommand() with a new PUT command
									 */
									//processCommand(new ServerCommand("SERVER", new ServerMessageHandler(), new String[]{key, value}, "PUT", "SERVER"));
								}
							}
						}
						break;
					case SERVER_STOPPED:
						break;
					case SERVER_WRITE_LOCK:
						break;
					default:
						break; 
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(LOG + "could not execute GET request. Please try again.");
				response = new Response(ResponseSource.SERVER, ResponseResult.FAIL, "Could not execute GET command. Please try again.");
				e.printStackTrace();
			}
			break;
		case "DISCONNECT":
			kvStore.disconnect();
			break;

		default:
			// TODO error message
		}
		return response;
	}

	private byte[] fetchMetadata(KVMessage kvMessage){
		/*
		 * TODO fetch optinalField from the payload
		 */
		return null;
	}
	public boolean updateMetadataFile(KVMessage kvMessage){

		boolean isUpdated = false;
		/*
		 * TODO implement a function to fetch metadata from the kvMessage
		 * 		and update the local metadata file.
		 */
		/*
		 * TODO find the right Server from metadata file and initiate a PUT request
		 * 		to that server by connecting to that server first, and then calling
		 * 		PUT.
		 */
		return isUpdated;
	}

}
