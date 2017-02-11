/**
 * @author Sarthak Gupta
 * This Class returns the Database that the server is using.
 */

package app_kvServer;


public class DataStoreWrapperFactory {

	public enum DataStore{
		XMLStore			/* The typical JSON based XML Store */
	}
	private static final DataStoreWrapper XMLStore = new XMLStore();
	
	
	/**
	 * 
	 * @param dataStore the type of datastore requested.
	 * @return the database instance used by the server.
	 */
	public static DataStoreWrapper getDataStore(DataStore dataStore){
		DataStoreWrapper dataStoreWrapper = null;
		switch(dataStore){
		case XMLStore:
			dataStoreWrapper = XMLStore;
		}
		return dataStoreWrapper;
	}
}

