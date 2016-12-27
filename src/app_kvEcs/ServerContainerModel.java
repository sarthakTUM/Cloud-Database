
package app_kvEcs;
import java.util.*;

public class ServerContainerModel {
	private List<ServerModel> ServerList = new ArrayList<ServerModel>();
	
	String Delimiter;//the delimiter of the metadata
	
	public void add(ServerModel NewNode)
	{
		this.ServerList.add(NewNode);
	}// appends a node to the serverList array
	public void remove(int Index)
	{
		this.ServerList.remove(Index);
	}//remove a node at an arbitary position, 
	public ServerModel getServerByIndex(int Index)
	{
		return this.ServerList.get(Index);
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
		if(Temp.getStartIndex()>0)
		{
		builder.append(Temp.getStartIndex());
		builder.append(",");
		}
		if(Temp.getEndIndex()>0)
		{
		builder.append(Temp.getEndIndex());
		builder.append(",");
		}
         builder.append("\n");
         
	}
	return builder.toString();
	}// converts the serverList object into a metadata format for passing to server, delimited with the specified delimiter
	public void sortHash()
	{
		Collections.sort(this.ServerList, new Comparator<ServerModel>() {
	        @Override
	        public int compare(ServerModel Server2, ServerModel Server1)
	        {

Integer i = new Integer(Server1.getHashValue()); 
	            return  i.compareTo(Server2.getHashValue());
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
	{if(isSorted()==true)
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
