package app_kvEcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import app_kvClient.CommandModel;
import javafx.util.Pair;

import app_kvServer.CacheSys;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import common.messages.Manager;
import app_kvServer.CacheFIFO2;
import app_kvServer.ServerKVStore;
public class ECSClient {
	
//move these variables to another ecs handler class begin
	static ECSState currentState;
//end
private static ServerContainerModel FullServerList = new ServerContainerModel();
private static String metadata;
private static ServerContainerModel ActiveServerList= new ServerContainerModel();
public static void SSHClient(String host,int port, String CacheStrategy, int cachesize) throws IOException{
    System.out.println("inside the ssh function");
   
    	String hostname = host;
		String username = "Anant";

		File keyfile = new File("C:/cygwin64/home/Anant/.ssh/id_rsa"); // or "~/.ssh/id_dsa"
		String keyfilePass = "a"; // will be ignored if not needed

		try
		{
			/* Create a connection instance */

			Connection conn = new Connection(hostname);

			/* Now connect */

			conn.connect();

			/* Authenticate */

			boolean isAuthenticated = conn.authenticateWithPublicKey(username, keyfile, keyfilePass);

			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");

			/* Create a session */

			Session sess = conn.openSession();

		sess.execCommand("nohup java -jar C:/Users/Anant/ec.jar "+port+" "+CacheStrategy+" "+cachesize+" ERROR & ");

			InputStream stderr= new StreamGobbler(sess.getStdout());

			BufferedReader br = new BufferedReader(new InputStreamReader(stderr));

			System.out.println("Here is some information about the remote host:");

		
			/* Close this session */
			
			sess.close();

			/* Close the connection */

			conn.close();

		}
	finally{}
	
    }
   
public static void failureDetection()
{
	Timer timer = new Timer();
	TimerTask myTask = new TimerTask() 
	{
	    @Override
	    public void run()
	    {ECSCommandModel command = new ECSCommandModel();
	    command.setInstruction("ping");
	    for(int ctr=0; ctr<ActiveServerList.count(); ctr++)
		{   System.out.println("Pinging KV ServerNode: "+ ActiveServerList.getServerByIndex(ctr).getName());
			String Response=sendData(command, ActiveServerList.getServerByIndex(ctr));
			if(Response.toUpperCase().contains("success"))
			{
				System.out.println("Ping was successful");
			}
			else 
			{
			    System.out.println("Ping unsuccesful");
			    handleFailedNode(ctr);
			    //handle failure by first removing the crashed node, updating everything
			}
		
		}
		{//check if the response is succesful or not
		}
	    	
	        // ping all servers every 5 minutes and check if they are up, else execute logic to replace them
	    }

		
	};

	timer.schedule(myTask, 300000, 300000);
}
public static void main(String[] args) throws Exception 
	   {
	   ECSKVstore.connect("127.0.0.1", 6001);
	   ECSKVstore.put("ECS ping");
	   ECSKVstore.put("ECS ping");
	   ECSKVstore.put("ECS ping");
	   ECSKVstore.disconnect();
	   failureDetection();
	
	  // System.out.println(ServerKVStore.get(range-2, range+2));
	   
		   Process proc = null;
		   String script = "C:/cygwin64/bin/bash.exe C:/Users/Anant/Documents/Cloud-Database/src/app_kvEcs/script.sh";
            
		 

	        // Grab output and print to display
	        
		   
	    	String sCurrentLine;
	    	System.out.println(new File(".").getAbsolutePath());
	    	BufferedReader br = new BufferedReader(new FileReader("ecsconfig.txt"));
	    	StringBuilder builder = new StringBuilder();
	    	while ((sCurrentLine = br.readLine()) != null) {
	    			builder.append(sCurrentLine);
	    			builder.append('\n');
	    	}  
	    			StringBuilder metabuilder = new StringBuilder();
	    	   
	    	        String result= builder.toString();  
	    	        
	    	        String[] rows = result.split("\n");
	    	       
	    	        for (int i = 0; i < rows.length; i++) 
	    	        {
	    	        
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
private static void handleFailedNode(int serverByIndex) {
	//modify the metadata on ecs to reflect the node revoal
	ActiveServerList.remove(serverByIndex);
	ActiveServerList.prepareMetaData();
	metadata=ActiveServerList.stringify();
	
	updateMetaData(ActiveServerList);
	
	//add a randomly selected node to the ecs
	ECSCommandModel command = new ECSCommandModel();
	String[] parameters = new String[2];
	parameters[0]="10";
	parameters[1]="fifo";
	command.setParameters(parameters);
    try {
		addRandomNode(command);
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
	   @SuppressWarnings("unused")
	private static void processMessage(ECSCommandModel command) throws NoSuchAlgorithmException, NumberFormatException, IOException{

			

				//Logging.FILE_LOGGER.debug("Number of tokens greater than 0");
				
				
				switch(command.getInstruction()){
				case "initkvservice":
					//check if command has the cache size and strategy parameters
					initKVService(Integer.parseInt(command.getParameters()[1]),Integer.parseInt(command.getParameters()[2]),command.getParameters()[3]);
				break;
				case "start":
					start(command,ActiveServerList);
					
					
					break;
					
				case "stop":
					stop(command);
					
					break; 
				
				case "shutdown":
					shutDown(command,ActiveServerList);
				break;
				case "add":
					addRandomNode(command);
					//TODOcheck if sorting is happening properly);
					
				break;
				case "remove":
					
					removeRandomNode();
					break;
				
			}}
		private static void addRandomNode(ECSCommandModel command) throws NoSuchAlgorithmException, UnsupportedEncodingException
		{boolean uniqueServerFound= false;
		boolean serverAlreadyExists=true;
		int uniqueServerIndex=0;
		System.out.println(ActiveServerList.count());
			if(ActiveServerList.count()<  FullServerList.count())		
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
				int cacheSize=Integer.parseInt(command.getParameters()[0]);
				String cacheStrategy=command.getParameters()[1];
				ServerModel AddedServerNode = new ServerModel(FullServerList.getServerByIndex(uniqueServerIndex));
				AddedServerNode.setCacheSize(cacheSize);
				AddedServerNode.setCacheStrategy(cacheStrategy);
				ActiveServerList.add(AddedServerNode);
				ActiveServerList.sortHash();//TODOcheck if sorting is happening properly
				ActiveServerList.prepareMetaData();
				metadata=ActiveServerList.stringify();
				initKVService(AddedServerNode);//launch the newly added node
				 ServerContainerModel addedServerList = new ServerContainerModel();
				 addedServerList.add(AddedServerNode);
				 start(new ECSCommandModel("start"), addedServerList);//start the newly added node
				 //send transfer command to the previous node/affected neighbour to initiate handoff
				 ServerModel prevNode = ActiveServerList.getPreviousNode(AddedServerNode);
				 ECSCommandModel addTransferCmd= new ECSCommandModel();
				 updateMetaData(ActiveServerList);
				 addTransferCmd.setInstruction("add");
				 System.out.println(sendData(addTransferCmd, prevNode));
				 //update metadata for all affected nodes
				 
				 
				//check if preperation of metadata is happening properly
				
				//initKVService(serverNode);
				
			    //initialize the newly added server through overloaded call of iniKvservice 
				//and call sendata() parsing the metadata as a parameter
				
				
				//sendData(command);
				}
			else
			{
			System.out.println("No inactive servers remain in the ecs config");
				//set view to display, that server already exists and dont execute the logic below
			}
			
             //assuming the add command is like add 10 LIFO
			
		}
		
		private static void start(ECSCommandModel command, ServerContainerModel serverList )
		{ command.setInstruction("start");
			for(int ctr=0; ctr<ActiveServerList.count(); ctr++)
			{   
				String Response=sendData(command, serverList.getServerByIndex(ctr));
			
			}
			if(serverList.count()>1)
			currentState=currentState.START;
			updateMetaData(serverList);
			
			
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
		private static void shutDown(ECSCommandModel command,ServerContainerModel serverList)
		{//look into whether to remove all nodes when shutting down
			command.setInstruction("shutdown");
			if((currentState==currentState.START || currentState==currentState.STOP || currentState==currentState.INITIALIZED) && currentState!= currentState.SHUTDOWN)
			{
				for(int ctr=0; ctr<serverList.count(); ctr++)
				{ 
					String response=sendData(command, serverList.getServerByIndex(ctr));
				System.out.println(response);
				}
			//TODO put a if condition to check if the stop was successful then set the state otherwise throw an error
			    if(serverList.count()>1)
				currentState=currentState.SHUTDOWN;
		    }
			else
			{
				
				System.out.println("SHUTDOWN incomplete, possibly because the system is already shut down or hasnt been started");
			}
		}
		private static void removeRandomNode()
		{
			Random rn = new Random();
			//choose a random node from ActiveServerList
			int randomIndex=rn.nextInt(ActiveServerList.count());
			removeNode(randomIndex);
			
		}
		private static void removeNode(int index)
		{
			ServerModel deletedNode = ActiveServerList.getServerByIndex(index);
			
			//store the previous node (affected neighbor)
			ServerModel prevNode = ActiveServerList.getPreviousNode(deletedNode);
			ECSCommandModel deleteTransferCmd= new ECSCommandModel();
			deleteTransferCmd.setInstruction("remove");
			
			//initiate handover of data from affected neighbor
			System.out.println(sendData(deleteTransferCmd, deletedNode));
			//TODO check what response the KVsrver sends and continue only if the response is positive
			ServerContainerModel addedNodeList = new ServerContainerModel();
			addedNodeList.add(deletedNode);
			 
			//shutdown the deleted node 
			shutDown(deleteTransferCmd, addedNodeList);
			
			//
			
		
			//modify the metadata on ecs to reflect the node revoal
			ActiveServerList.remove(index);
			ActiveServerList.prepareMetaData();
			metadata=ActiveServerList.stringify();
			
			//send updatedMetadata to the affected Neighbours
		    ServerContainerModel affectedNeighbours= new ServerContainerModel();
		    affectedNeighbours.add(prevNode);
			updateMetaData(ActiveServerList);
		}
		private static void updateMetaData(ServerContainerModel serverList)
		{
			ECSCommandModel updateCommand= new ECSCommandModel();
			updateCommand.setInstruction("meta");
			String[] parameters= new String[1];
			parameters[0]=metadata;
			updateCommand.setParameters(parameters);
			for(int ctr=0; ctr<serverList.count(); ctr++)
			{
				System.out.println(sendData(updateCommand, serverList.getServerByIndex(ctr)));
			}
			
  
			
			
			
		}
	   private static void initKVService(int NumberofNodes, int cacheSize, String displacementStrategy) throws IOException {
					
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
						ServerModel temp = ActiveServerList.getServerByIndex(i);
						SSHClient(temp.getIP(),temp.getPort(), displacementStrategy, cacheSize);
					}
					//sendMetadata to a   ll of the initialized nodes
					//
					//return true;
				}
				private static void initKVService(ServerModel server) 
				{
					
					//SSHPublicKeyAuthentication.sshConnection(Temp.getIP(),Temp.getPort() , displacementStrategy, cacheSize);
					//takes parameters as ip,port,cachestrategy,size and launches ssh
					try {
						SSHClient(server.getPort(), server.getCacheStrategy());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//SSHPublicKeyAuthentication.ssh Connection(Temp.getIP(),Temp.getPort() , displacementStrategy, cacheSize);
					
					//sendMetadata to all of the initialized nodes
				
					//
					//return true;
				}
				
				private static String sendData(ECSCommandModel command,ServerModel server)
				{
					//populate relevantServers with either the activeServerList(when doing masscommands like shutdown and start) or individual servers(when doing add/remove)
					
						try 
						{
							
								ECSKVstore.connect(server.getIP(),server.getPort());
								//TODOuse payload to send data instead of parsing via a string like right now
								ECSKVstore.put("ECS "+command.getCompleteCommandString());
								 
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
			
