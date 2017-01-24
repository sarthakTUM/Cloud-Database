package app_kvServer;

public class CacheSys {

cacheInterface selectedCache;
public CacheSys(){
	this("FIFO",10);
}
public CacheSys(String cacheStrategy, int cacheSize)
{// take cacheStrategy(LIFO,FIFO,LRU) and size and Instantiate selectedCache accordingly
	switch(cacheStrategy.toUpperCase())
	{
	case "LRU":
		selectedCache= new LRUCache(cacheSize);
	break;
	case "LFU":
		selectedCache= new LFU(cacheSize);
	break;
	case "FIFO":
		selectedCache= new CacheFIFO2(cacheSize);
	break;
	default:
		//defaultcache=FIFO
		selectedCache= new CacheFIFO2(cacheSize);
	break;
	}
}
public String get(String key)
{
	return(selectedCache.get(key));
}
public void put(String key, String value)
{
	selectedCache.put(key, value);
}

}
