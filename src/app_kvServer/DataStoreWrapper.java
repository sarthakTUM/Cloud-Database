package app_kvServer;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javafx.util.Pair;

import org.w3c.dom.DOMException;

public interface DataStoreWrapper {

	/*
	 * TODO features of the datastore.
	 * 
	 */
	public enum DBCommand{
		GET,
		PUT,
		SYNC
	}
	
	public enum DBRequestResult{
		FAIL,
		SUCCESS
	}
	/*public enum DBRequestStatus{
		GET_ERROR,
		GET_SUCCESS,
		PUT_ERROR,
		PUT_SUCCESS,
		PUT_UPDATE
	}*/

	DatabaseResponse put(String key, String value);
	DatabaseResponse get(String key);
	List<Pair<String,String>> get(int startRange, int endRange) throws NoSuchAlgorithmException, UnsupportedEncodingException, DOMException;
	
}
