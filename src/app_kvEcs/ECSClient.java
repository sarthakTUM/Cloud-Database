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
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ECSClient {
	
private static ServerContainerModel FullServerList = new ServerContainerModel();
private static String metadata;
private static ServerContainerModel ActiveServerList= new ServerContainerModel();
	   public static void main(String[] args) throws IOException, NoSuchAlgorithmException 
	   {
	    	
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
	    	       
	    	        for (int i = 0; i < rows.length; i++) {
	    	        
	    	        	String[] columns = rows[i].split(" ");
	    	        	
	    	        	FullServerList.add(new ServerModel(columns[0],columns[1],Integer.parseInt(columns[2])));
	    	        	
	    	       
	    	       
	    	        
	    	        }
	    	        initKVService(2, 10, "lifo");
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
					
					
					sendData(command);
					break;
					
				case "stop":
					sendData(command);
					
					break;
				
				case "shutdown":
					sendData(command);
				break;
				case "add":
					boolean uniqueServerFound= false;
					boolean serverAlreadyExists=true;
					int uniqueServerIndex=0;
					System.out.println(ActiveServerList.count());//TODOcheck if sorting is happening properly);
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
					//set view to display, that server already exists and dont execute the logic below
					}
					
                     //assuming the add command is like add 10 LIFO
					int cacheSize=Integer.parseInt(command.getParameters()[0]);
					String cacheStrategy=command.getParameters()[1];
					ServerModel serverNode = new ServerModel(FullServerList.getServerByIndex(uniqueServerIndex));
					serverNode.setCacheSize(cacheSize);
					serverNode.setCacheStrategy(cacheStrategy);
					ActiveServerList.add(serverNode);
					ActiveServerList.sortHash();//TODOcheck if sorting is happening properly
					ActiveServerList.prepareMetaData();
					System.out.println(ActiveServerList.stringify());
					
					//check if preperation of metadata is happening properly
					
					//initKVService(serverNode);
					
				    //initialize the newly added server through overloaded call of iniKvservice 
					//and call sendata() parsing the metadata as a parameter
					
					
					//sendData(command);
				break;
				case "remove":
					Random rn = new Random();
					int randomIndex=rn.nextInt(ActiveServerList.count());
					ActiveServerList.remove(randomIndex);
					ActiveServerList.prepareMetaData();
					metadata=ActiveServerList.stringify();
					
					
					break;
				
			}}
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
				
				private static void sendData(ECSCommandModel command){
					
					
					
				
//						try 
//						{
//							KVStore.connect(temp[i][1], Integer.parseInt(temp[i][2]));
//							KVStore.put(command, data);
//							KVStore.disconnect();
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
					}
			
			}

	

	

			
