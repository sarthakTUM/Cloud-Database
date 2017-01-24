package app_kvServer;
import java.util.LinkedHashMap;
import java.util.Map;


public class LFU implements cacheInterface{

	class CacheEntry
	{
	    private String data;
	    private int frequency;

	    // default constructor
	    private CacheEntry()
	    {}

	    public String getData() {
	        return data;
	    }
	    public void setData(String data) {
	        this.data = data;
	    }

	    public int getFrequency() {
	        return frequency;
	    }
	    public void setFrequency(int frequency) {
	        this.frequency = frequency;
	    }       

	}

	private static int initialCapacity = 10;

	private static LinkedHashMap<String, CacheEntry> cacheMap = new LinkedHashMap<String, CacheEntry>();
	/* LinkedHashMap is used because it has features of both HashMap and LinkedList. 
	 * Thus, we can get an entry in O(1) and also, we can iterate over it easily.
	 * */

	public LFU(int initialCapacity)
	{
	    this.initialCapacity = initialCapacity;
	}

	public void put(String key, String data)
	{
	    if(!isFull())
	    {
	        CacheEntry temp = new CacheEntry();
	        temp.setData(data);
	        temp.setFrequency(0);

	        cacheMap.put(key, temp);
	    }
	    else
	    {
	        String entryKeyToBeRemoved = getLFUKey();
	        cacheMap.remove(entryKeyToBeRemoved);

	        CacheEntry temp = new CacheEntry();
	        temp.setData(data);
	        temp.setFrequency(0);

	        cacheMap.put(key, temp);
	    }
	}

	public String getLFUKey()
	{
	    String key = "";
	    int minFreq = Integer.MAX_VALUE;

	    for(Map.Entry<String, CacheEntry> entry : cacheMap.entrySet())
	    {
	        if(minFreq > entry.getValue().frequency)
	        {
	            key = entry.getKey();
	            minFreq = entry.getValue().frequency;
	        }           
	    }

	    return key;
	}

	public String get(String key)
	{
	    if(cacheMap.containsKey(key))  // cache hit
	    {
	        CacheEntry temp = cacheMap.get(key);
	        temp.frequency++;
	        cacheMap.put(key, temp);
	        return temp.data;
	    }
	    return null; // cache miss
	}

	public static boolean isFull()
	{
	    if(cacheMap.size() == initialCapacity)
	        return true;

	    return false;
	}

}
