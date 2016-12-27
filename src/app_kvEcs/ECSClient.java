package app_kvEcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ECSClient {
	
private static ServerContainerModel FullServerList;
private static ServerContainerModel ActiveServerList;
	   public static void main(String[] args) throws IOException, NoSuchAlgorithmException 
	   {
	    	
	    	String sCurrentLine;
	    	System.out.println(new File(".").getAbsolutePath());
	    	BufferedReader br = new BufferedReader(new FileReader("ecs.config"));
	    	StringBuilder builder = new StringBuilder();
	    	while ((sCurrentLine = br.readLine()) != null) {
	    			builder.append(sCurrentLine);
	    			builder.append('\n');
	    	}
	    			StringBuilder metabuilder = new StringBuilder();
	    	 
	    	        String result= builder.toString();
	    	        
	    	        String[] rows = result.split("\n");
	    	        
	    	        for (int i = 0; i < rows.length; i++) {
	    	        
	    	        	String[] columns = rows[i].split(" ");
	    	        	FullServerList.add(new ServerModel(columns[0],columns[1],Integer.parseInt(columns[2])));
	    	        	
	    	       
	    	       
	    	        
	    	        }
}
	   
	   private static void processMessage(){

			if(returntokens.length > 0){

				//Logging.FILE_LOGGER.debug("Number of tokens greater than 0");
				setCommand(tokens[0]);
				String returnCommand = getCommand().toLowerCase();
				switch(returnCommand){

				case "start":
					start();
					//initService(2, 10, "fifo");
					//sendData("", returnCommand);
					break;
					
				case "stop":
					sendData("", returnCommand);
					
					break;
				
				case "shutdown":
					sendData("", returnCommand);
				break;
				case "add":
					
				break;
				case "remove":
					break;
				
			}
				private static boolean initService(int numberOfNodes, int cacheSize, String displacementStrategy) {
					
					//Initialize ActiveServerList BY randomly selected nodes (without replacement) from FullServerList
					List<Integer> numbersList = IntStream.rangeClosed(1,FullServerList.count()).boxed().collect(Collectors.toList());
					Collections.shuffle(numbersList);
					for(int i=0; i<numberOfNodes; i++)
					{
						ActiveServerList.add(FullServerList.getServerByIndex(i));
					}
					//end
					
					//Launch ssh for each of the nodes in the ActiveServerList
					for(int i=0; i<numberOfNodes; i++)
					{//takes parameters as ip,port,cachestrategy,size and launches ssh
						ServerModel Temp = ActiveServerList.getServerByIndex(i);
						SSHPublicKeyAuthentication.sshConnection(Temp.getIP(),Temp.getPort() , displacementStrategy, cacheSize);
					}
					//sendMetadata to all of the initialized nodes
					sendData(metadata, "meta");
					//
					return true;
				}
				private static void sendData(String data, String command){
					
					String[][] temp = getsData();

					for(int i=0; i < numberofnodes;i++)
					{
						try {
							KVStore.connect(temp[i][1], Integer.parseInt(temp[i][2]));
							KVStore.put(command, data);
							KVStore.disconnect();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			
			}
}
			
