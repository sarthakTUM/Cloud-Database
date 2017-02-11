/**
 * @author Sarthak Gupta
 */

package app_kvServer;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javafx.util.Pair;

import org.w3c.dom.DOMException;

public interface DataStoreWrapper {

	public enum DBCommand{
		GET,			/* Gets the data from the database */
		PUT,			/* Puts the data to the database */
		SYNC			/* Syncs the file to the copy at database */
	}
	
	public enum DBRequestResult{
		FAIL,			/* The request succeeded */
		SUCCESS			/* The request failed */
	}

	/**
	 * 
	 * @param key The key to be retrieved
	 * @param value to be put
	 * @return database Response 
	 */
	DatabaseResponse put(String key, String value);
	
	/**
	 * 
	 * @param key to be retrieved
	 * @return response whether succeeded or not.
	 */
	DatabaseResponse get(String key);
	
	/**
	 * range query for querying the key-value pairs between start and end range
	 * @param startRange
	 * @param endRange
	 * @return List of key:value pairs
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws DOMException
	 */
	List<Pair<String,String>> get(int startRange, int endRange) throws NoSuchAlgorithmException, UnsupportedEncodingException, DOMException;
	
}
