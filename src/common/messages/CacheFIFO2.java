package common.messages;

import java.util.HashMap;
import java.util.Iterator;

public final class CacheFIFO2 extends GenericCache implements CacheInterface {
	private int __current = 0;
	private boolean[] __tryAgain;

	/**
	 * Creates a CacheFIFO2 instance with a given cache capacity.
	 * <p>
	 * @param capacity  The capacity of the cache.
	 */
	public CacheFIFO2(int capacity) { 
		super(capacity);

		__tryAgain = new boolean[_cache.length];
	}
	public String get(String Key)
	{
		return (getElement(Key)!=null)?getElement(Key).toString():null;
	}
	public void put(String Key, String Value)
	{
		addElement(Key, Value);
	}
	public CacheFIFO2(){
		this(GenericCache.DEFAULT_CAPACITY);
	}


	public synchronized Object getElement(Object key) { 
		Object obj;

		obj = _table.get(key);

		if(obj != null) {
			GenericCacheEntry entry;

			entry = (GenericCacheEntry)obj;

			__tryAgain[entry._index] = true;
			return entry._value;
		}

		return null;
	}


	/**
	 * Adds a value to the cache.  If the cache is full, when a new value
	 * is added to the cache, it replaces the first of the current values
	 * in the cache to have been added (i.e., FIFO2).
	 * <p>
	 * @param key   The key referencing the value added to the cache.
	 * @param value The value to add to the cache.
	 */
	public final synchronized void addElement(Object key, Object value) {
		int index;
		Object obj;

		obj = _table.get(key);

		if(obj != null) {
			GenericCacheEntry entry;

			// Just replace the value.  Technically this upsets the FIFO2 ordering,
			// but it's expedient.
			entry = (GenericCacheEntry)obj;
			entry._value = value;
			entry._key   = key;

			// Set the try again value to compensate.
			__tryAgain[entry._index] = true;

			return;
		}

		// If we haven't filled the cache yet, put it at the end.
		if(!isFull()) {
			index = _numEntries;
			++_numEntries;
		} else {
			// Otherwise, find the next slot that doesn't have a second chance.
			index = __current;

			while(__tryAgain[index]) {
				__tryAgain[index] = false;
				if(++index >= __tryAgain.length)
					index = 0;
			}

			__current = index + 1;
			if(__current >= _cache.length)
				__current = 0;

			_table.remove(_cache[index]._key);
		}

		_cache[index]._value = value;
		_cache[index]._key   = key;
		_table.put(key, _cache[index]);
	}

}

/*

 *
 * @version @version@
 * @since 1.0
 * @see Cache
 * @see CacheLRU
 * @see CacheFIFO
 * @see CacheFIFO2
 * @see CacheRandom
 */
abstract class GenericCache implements Cache, java.io.Serializable {
	/**
	 * The default capacity to be used by the GenericCache subclasses
	 * provided with this package.  Its value is 20.
	 */
	public static final int DEFAULT_CAPACITY = 20;

	int _numEntries;
	GenericCacheEntry[] _cache;
	HashMap _table;

	/**
	 * The primary constructor for GenericCache.  It has default
	 * access so it will only be used within the package.  It initializes
	 * _table to a Hashtable of capacity equal to the capacity argument,
	 * _cache to an array of size equal to the capacity argument, and
	 * _numEntries to 0.
	 * <p>
	 * @param capacity The maximum capacity of the cache.
	 */
	GenericCache(int capacity) {
		_numEntries = 0;
		_table    = new HashMap(capacity);
		_cache    = new GenericCacheEntry[capacity];

		while(--capacity >= 0)
			_cache[capacity] = new GenericCacheEntry(capacity);
	}

	public abstract void addElement(Object key, Object value);

	public synchronized Object getElement(Object key) { 
		Object obj;

		obj = _table.get(key);

		if(obj != null)
			return ((GenericCacheEntry)obj)._value;

		return null;
	}

	public final Iterator keys() {
		return _table.keySet().iterator();
	}

	/**
	 * Returns the number of elements in the cache, not to be confused with
	 * the {@link #capacity()} which returns the number
	 * of elements that can be held in the cache at one time.
	 * <p>
	 * @return  The current size of the cache (i.e., the number of elements
	 *          currently cached).
	 */
	public final int size() { return _numEntries; }

	/**
	 * Returns the maximum number of elements that can be cached at one time.
	 * <p>
	 * @return The maximum number of elements that can be cached at one time.
	 */
	public final int capacity() { return _cache.length; }

	public final boolean isFull() { return (_numEntries >= _cache.length); }
}


/**
 * An interface defining the basic functions of a cache.
 *
 * @version @version@
 * @since 1.0
 */
interface Cache {

	public void addElement(Object key, Object value);

	public Object getElement(Object key);

	/**
	 * Returns the number of elements in the cache, not to be confused with
	 * the {@link #capacity()} which returns the number
	 * of elements that can be held in the cache at one time.
	 * <p>
	 * @return  The current size of the cache (i.e., the number of elements
	 *          currently cached).
	 */
	public int size();


	/**
	 * Returns the maximum number of elements that can be cached at one time.
	 * <p>
	 * @return The maximum number of elements that can be cached at one time.
	 */
	public int capacity();

}

/**
 * A structure used to store values in a GenericCache.  It
 * is declared with default access to limit it to use only within the
 * package.
 *
 * @version @version@
 * @since 1.0
 */
final class GenericCacheEntry implements java.io.Serializable {
	/** The cache array index of the entry. */
	int _index;
	/** The value stored at this entry. */
	Object _value;
	/** The key used to store the value. */
	Object _key;

	GenericCacheEntry(int index) {
		_index = index;
		_value = null;
		_key   = null;
	}
}

