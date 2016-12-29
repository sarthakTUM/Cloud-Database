package app_kvClient;

//import common.messages.KVMessage;

import common.messages.KVMessage;

import app_kvClient.Response.ResponseResult;
import app_kvClient.Response.ResponseSource;
import client.KVStore;

public class ServerMessageHandler extends Handler{

	private static final String LOG = "LOG:SMHANDLR:";
    /*
	 * TODO implement switch case for handling various commands
	 *
	 * and call appropriate KVStore method which implements our
	 * communication interface.
	 */
	private static KVStore kvStore;
	public Response processCommand(CommandModel serverCommand){
		Response response = null;
		//KVMessage kvMessage;
		System.out.println(LOG + "Command Model Class: " + serverCommand.getClass().getSimpleName());
		switch(serverCommand.getCommandInstruction()){
		case "CONNECT":
			// TODO call KVStore  connect method
			System.out.println(LOG + "executing CONNECT command");
			String serverAddress = serverCommand.getCommandAttributes()[1];
			int serverPort = Integer.valueOf(serverCommand.getCommandAttributes()[2]);
			kvStore = new KVStore(serverAddress, serverPort);
			try {
				kvStore.connect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				response = new Response(ResponseSource.SERVER, ResponseResult.FAIL, "Could not establish connection");
				e.printStackTrace();
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
						break;
					case PUT_UPDATE:
						response = new Response(ResponseSource.SERVER, ResponseResult.SUCCESS, "PUT request successful. Tuple updated : " + key + ":" + value);
						break;
					case SERVER_NOT_RESPONSIBLE:
						updateMetadataFile(kvMessage);
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
		case "GET":
			/*
			 * TODO receive KVMessage and based on the status, build a response, execute proper action.
			 */
			key = serverCommand.getCommandAttributes()[1];
			try {
				kvStore.get(key);
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
