package app_kvEcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import app_kvClient.CommandModel;
import javafx.util.Pair;


public class ECSClient {
//move these variables to another ecs handler class begin
	static ECSState currentState;
//end
private static ServerContainerModel FullServerList = new ServerContainerModel();
private static String metadata;
private static ServerContainerModel ActiveServerList= new ServerContainerModel();
	   public static void main(String[] args) throws IOException, NoSuchAlgorithmException 
	   {
		  
		   
		   
	    	String sCurrentLine;
	    	System.out.println(new File(".").getAbsolutePath());
	    	BufferedReader br = new BufferedReader(new FileReader("ecsconfig.config"));
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
	    	        initKVService(4, 10, "lifo");
	    	       
	    	        System.out.println(ActiveServerList.getServerByIndex(0).getName()+ActiveServerList.getServerByIndex(0).getHashValue());
	    	        System.out.println(ActiveServerList.getServerByIndex(1).getName()+ActiveServerList.getServerByIndex(1).getHashValue());
	    	       ActiveServerList.sortHash();
	    	       ActiveServerList.prepareMetaData();
	    	       System.out.println(ActiveServerList.stringify());
	    	       ActiveServerList.remove(0);
					ActiveServerList.prepareMetaData();
					System.out.println(ActiveServerList.stringify());
	    	        
	    	        //processMessage(new ECSCommandModel("add 11 lifo"));
	    	        
}
	   
	   private static void processMessage(ECSCommandModel command) throws NoSuchAlgorithmException, UnsupportedEncodingException{

			

				//Logging.FILE_LOGGER.debug("Number of tokens greater than 0");
				
				
				switch(command.getInstruction()){
				case "initkvservice":
					//check if command has the cache size and strategy parameters
					initKVService(Integer.parseInt(command.getParameters()[1]),Integer.parseInt(command.getParameters()[2]),command.getParameters()[3]);
				break;
				case "start":
					start(command);
					
					
					break;
					
				case "stop":
					stop(command);
					
					break; 
				
				case "shutdown":
					shutDown(command);
				break;
				case "add":
					addRandomNode();
					//TODOcheck if sorting is happening properly);
					
				break;
				case "remove":
					
					removeRandomNode();
					break;
				
			}}
		private static void addRandomNode()
		{boolean uniqueServerFound= false;
		boolean serverAlreadyExists=true;
		int uniqueServerIndex=0;
		System.out.println(ActiveServerList.count());
			if(ActiveServerList.count()<FullServerList.count())		
			{//handle condition when activeserver is already same as fullserverList
				while(uniqueServerFound!=true)
				{
					serverAlreadyExists=false;
					Random rn = new Random();
					int randomIndex=rn.nextInt(FullServerList.count());
			
						for(int ctr=0; ctr<ActiveServerList.count(); ctr++)
						{
							if(ActiveServerList.getServerByIndex(ctr).getName()==FullServerList.getServerByIndex(randomIndex).getName())
							{
								serverAlreadyExists=true;
								break;
							}
				
						}
						if(serverAlreadyExists!=true)
						{
							uniqueServerFound=true;
							uniqueServerIndex= randomIndex;
						}
				
				}
				
				}
			else
			{
			System.out.println("No inactive servers remain in the ecs config");
				//set view to display, that server already exists and dont execute the logic below
			}
			
             //assuming the add command is like add 10 LIFO
			
		}
		
		private static void start(ECSCommandModel command)
		{
			for(int ctr=0; ctr<ActiveServerList.count(); ctr++)
			{
				String Response=sendData(command, ActiveServerList.getServerByIndex(ctr));
			
			}
		}
		private static void stop(ECSCommandModel command)
		{if(currentState==currentState.START && currentState!= currentState.SHUTDOWN)
			{	
				for(int ctr=0; ctr<ActiveServerList.count(); ctr++)
				{
					String response=sendData(command, ActiveServerList.getServerByIndex(ctr));
				System.out.println(response);
				}
				currentState=currentState.STOP;
			}	
		else{
			
			System.out.println("STOP incomplete, possibly because the system is already down or hasnt been started");
		}
		//TODO put a if condition to check if the stop was successful then set the state
		
		}
		private static void shutDown(ECSCommandModel command)
		{
			if((currentState==currentState.START || currentState==currentState.STOP || currentState==currentState.INITIALIZED) && currentState!= currentState.SHUTDOWN)
			{
				for(int ctr=0; ctr<ActiveServerList.count(); ctr++)
				{
					String response=sendData(command, ActiveServerList.getServerByIndex(ctr));
				System.out.println(response);
				}
			//TODO put a if condition to check if the stop was successful then set the state otherwise throw an error
			currentState=currentState.SHUTDOWN;
		    }
			else{
				
				System.out.println("SHUTDOWN incomplete, possibly because the system is already shut down or hasnt been started");
			}
		}
		private static void removeRandomNode()
		{
			Random rn = new Random();
			int randomIndex=rn.nextInt(ActiveServerList.count());
			ActiveServerList.remove(randomIndex);
			ActiveServerList.prepareMetaData();
			metadata=ActiveServerList.stringify();
			
		}
	   private static boolean initKVService(int NumberofNodes, int cacheSize, String displacementStrategy) {
					
					//Initialize ActiveServerList BY randomly selected nodes (without replacement) from FullServerList
					List<Integer> numbersList = IntStream.rangeClosed(1,FullServerList.count()).boxed().collect(Collectors.toList());
					Collections.shuffle(numbersList);
					System.out.println(numbersList.toString());
					for(int i=0; i<NumberofNodes; i++)
					{
						ActiveServerList.add(FullServerList.getServerByIndex(numbersList.get(i)-1));
					}
					for(int i=0; i<NumberofNodes; i++)
					{
						System.out.println(ActiveServerList.stringify());
					}
					//end
					
					//Launch ssh for each of the nodes in the ActiveServerList
					for(int i=0; i<NumberofNodes; i++)
					{//takes parameters as ip,port,cachestrategy,size and launches ssh
						ServerModel Temp = ActiveServerList.getServerByIndex(i);
						//SSHPublicKeyAuthentication.sshConnection(Temp.getIP(),Temp.getPort() , displacementStrategy, cacheSize);
					}
					//sendMetadata to all of the initialized nodes
					//
					return true;
				}
				private static boolean initKVService(ServerModel server) 
				{
	
					//takes parameters as ip,port,cachestrategy,size and launches ssh
					
					//SSHPublicKeyAuthentication.ssh Connection(Temp.getIP(),Temp.getPort() , displacementStrategy, cacheSize);
					
					//sendMetadata to all of the initialized nodes
				
					//
					return true;
				}
				
				private static String sendData(ECSCommandModel command,ServerModel server)
				{
					//populate relevantServers with either the activeServerList(when doing masscommands like shutdown and start) or individual servers(when doing add/remove)
					
						try 
						{
							
								ECSKVstore.connect(server.getIP(),server.getPort());
								//TODOuse payload to send data instead of parsing via a string like right now
								ECSKVstore.put(command.getCompleteCommandString());
								 
								//TODO remember to flush the response string so previous responses dont get repeated
								ECSKVstore.disconnect();
								
								
						}
							 	catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
						}
						
			return "returnresponse";
			}

	

	
}
			
