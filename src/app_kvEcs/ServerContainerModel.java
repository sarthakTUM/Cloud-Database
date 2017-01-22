
package app_kvEcs;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ServerContainerModel {
	private List<ServerModel> ServerList = new ArrayList<ServerModel>();//be
	//private List<ServerModel> preSortedServerList= new ArrayList<ServerModel>();//before the sorting happens
	
	String Delimiter;//the delimiter of the metadata
	
	public void add(ServerModel NewNode)
	{
		this.ServerList.add(NewNode);
	}// appends a node to the serverList array
	public void cnvMetaToServList(String Metadata) throws NumberFormatException, NoSuchAlgorithmException, UnsupportedEncodingException
	{
		String[] rows = Metadata.split("\n");
		for (int i = 0; i < rows.length; i++) 
		{
	        
        	String[] columns = rows[i].split(",");
        	
        	
				this.ServerList.add(new ServerModel(columns[0],columns[1],Integer.parseInt(columns[2]),Integer.parseInt(columns[3]),Integer.parseInt(columns[4])));
			
				// TODO Auto-generated catch block  
				
		}
	}// converts metadata string to a servverlist object
	public void remove(int Index)
	{
		this.ServerList.remove(Index);
	}//remove a node at an arbitary position, 
	public ServerModel getServerByIndex(int Index)
	{
		return this.ServerList.get(Index);
	}
	public ServerModel getServerByPortAndIP(String IP,int Port)
	{
		// parsing the host and Ip for node 3, and the NumberOfNodes as 2, would return nodes 4 and 5, doing so for 4 would return 5 and 1. doing it for 5 would return 1 and 2
				for(int ctr=0; ctr<this.ServerList.size();ctr++)
				{
					if(ServerList.get(ctr).getIP().equalsIgnoreCase(IP) && ServerList.get(ctr).getPort()==port )
					{
						return getServerByIndex(ctr);
					}
				
				}
		return null;
	}
	public  ServerModel  getPreviousNode(String IP, int port )
	{
		//takes the Ip and port, and number as parameters to return a ServerContainerModel populated with the next N nodes, wraps around
		// parsing the host and Ip for node 3, and the NumberOfNodes as 2, would return nodes 4 and 5, doing so for 4 would return 5 and 1. doing it for 5 would return 1 and 2
		for(int ctr=0; ctr<this.ServerList.size();ctr++)
		{
			if(ServerList.get(ctr).getIP().equalsIgnoreCase(IP) && ServerList.get(ctr).getPort()==port )
			{
				return getServerByIndex(ctr-1);
				
			}
	  }
		return null;
	}
	public  ServerModel  getPreviousNode(ServerModel Server)
	{
		//takes the Ip and port, and number as parameters to return a ServerContainerModel populated with the next N nodes, wraps around
		// parsing the host and Ip for node 3, and the NumberOfNodes as 2, would return nodes 4 and 5, doing so for 4 would return 5 and 1. doing it for 5 would return 1 and 2
		for(int ctr=0; ctr<this.ServerList.size();ctr++)
		{
			if(ServerList.get(ctr).getIP().equalsIgnoreCase(Server.getIP()) && ServerList.get(ctr).getPort()==Server.getPort() )
			{
				return getServerByIndex(ctr-1);
				
			}
	  }
		return null;
	}
	public  ServerContainerModel  getNextNnodes(String IP, int port, int NumberOfNodes )
	{ServerContainerModel replicationServ = new ServerContainerModel();
		//takes the Ip and port, and number as parameters to return a ServerContainerModel populated with the next N nodes, wraps around
		// parsing the host and Ip for node 3, and the NumberOfNodes as 2, would return nodes 4 and 5, doing so for 4 would return 5 and 1. doing it for 5 would return 1 and 2
		for(int ctr=0; ctr<this.ServerList.size();ctr++)
		{
			if(ServerList.get(ctr).getIP().equalsIgnoreCase(IP) && ServerList.get(ctr).getPort()==port )
			{
				for(int ctr2=1; ctr2<=NumberOfNodes; ctr2++)
				{
				int index1=(ctr+ctr2)%this.ServerList.size();
				replicationServ.add(this.ServerList.get(index1));
				}
				
			}
	  }
		return replicationServ;
	}
	public  ServerContainerModel  getNextNnodes(ServerModel Server, int NumberOfNodes )
	{ServerContainerModel replicationServ = new ServerContainerModel();
		//takes the Ip and port, and number as parameters to return a ServerContainerModel populated with the next N nodes, wraps around
		// parsing the host and Ip for node 3, and the NumberOfNodes as 2, would return nodes 4 and 5, doing so for 4 would return 5 and 1. doing it for 5 would return 1 and 2
		for(int ctr=0; ctr<this.ServerList.size();ctr++)
		{
			if(ServerList.get(ctr).getIP().equalsIgnoreCase(Server.getIP()) && ServerList.get(ctr).getPort()==Server.getPort() )
			{
				for(int ctr2=1; ctr2<=NumberOfNodes; ctr2++)
				{
				int index1=(ctr+ctr2)%this.ServerList.size();
				replicationServ.add(this.ServerList.get(index1));
				}
				
			}
	  }
		return replicationServ;
	}
	public int count()
	{
		return ServerList.size();
	}
	public String stringify()
	{StringBuilder builder = new StringBuilder();
	for (int ctr=0; ctr<this.ServerList.size(); ctr++)	
	{
		
		ServerModel Temp= ServerList.get(ctr);
		if(Temp.getName()!=null)
		{
		builder.append(Temp.getName());
		builder.append(",");
		}
		if(Temp.getIP()!=null)
		{
		builder.append(Temp.getIP());
		builder.append(",");
		}
		if(Temp.getPort()>0)
		{
		builder.append(Temp.getPort());
		builder.append(",");
		}
		if(Temp.getStartIndex()>=0)
		{
		builder.append(Temp.getStartIndex());
		builder.append(",");
		}
		if(Temp.getEndIndex()>=0)
		{
		builder.append(Temp.getEndIndex());
	
		}
        if(ctr<this.ServerList.size()-1)
        {
        		builder.append("\n");
        }
         
	}
	return builder.toString();
	}// converts the serverList object into a metadata format for passing to server, delimited with the specified delimiter
	public void sortHash()
	{
//	Collections.copy(preSortedServerList, ServerList);//TODO, check if this copy works fine due to size issues

		Collections.sort(this.ServerList, new Comparator<ServerModel>() {
	        @Override
	        public int compare(ServerModel Server2, ServerModel Server1)
	        {

	        	//Integer i = new Integer(Server1.getHashValue()); 
	            //return  i.compareTo(Server2.getHashValue());
	        	return Server2.getHashValue()-Server1.getHashValue();
	        }
	    });
	}//sorts the serverList as per the hash value

	private boolean isSorted()
	{Boolean response=true;
		for (int ctr=0; ctr<this.ServerList.size()-1; ctr++)	
		{
			if(this.ServerList.get(ctr).getHashValue()<this.ServerList.get(ctr+1).getHashValue())
			{}
			else
			{
				response=false;
			}
		}
		return response;
	}//returns true if the serverList is already sorted in ascending order

	public void prepareMetaData()
	{
	if(isSorted()==true)
	{
		for (int ctr=0; ctr<this.ServerList.size(); ctr++)	
		{
			int StartIndex=(ctr==0) ? 0 :this.ServerList.get(ctr-1).getHashValue();// assign 0 for first node, else assign the hash value of previous node
			int EndIndex=(ctr==this.ServerList.size()-1) ? Integer.MAX_VALUE :this.ServerList.get(ctr).getHashValue()-1;// assign maxval if last node, else assign the computed hash value
			
			this.ServerList.get(ctr).setStartIndex(StartIndex);
	
			this.ServerList.get(ctr).setEndIndex(EndIndex);
		}
    }//populates beginning index and end index, if isSORTED() function returns true;
	}
	//getters and setters for serverList
}
