package client;

import java.util.HashMap;

import app_kvServer.ServerModel;

public class Metadata {

	private static HashMap<String, ServerModel> metadataFile = new HashMap<String, ServerModel>();
	
	public static boolean updateMetadata(){
		boolean isUpdated = false;
		return isUpdated;
	}
	public static boolean updateMetadata(byte[] rawMetadata){
		boolean isUpdated = false;
		return isUpdated;
	}
	public static ServerModel findServer(String key){
		/*
		 * TODO find the appropriate server from metadata using this key.
		 * 		and return the value mapped to that key
		 * 		1. Iterate through map metadataFile
		 * 		2. Compare key with the ranges of the servers
		 * 		3. For appropriate server, return the ServerModel, i.e., the value associated with that key.
		 */
		
		return null;
	}
}
