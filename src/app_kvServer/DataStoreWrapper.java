package app_kvServer;

public interface DataStoreWrapper {

	/*
	 * TODO features of the datastore.
	 * 
	 */
	public enum DBCommand{
		GET,
		PUT
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
	
}
