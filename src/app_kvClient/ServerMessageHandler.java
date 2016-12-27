package app_kvClient;

//import common.messages.KVMessage;

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
				kvStore.put(key, value);
				//kvMessage.getStatus();
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
	
	public void receiveMessage(){
		
		
	}
	
}
