package app_kvServer;


public class DataStoreWrapperFactory {

	public enum DataStore{
		XMLStore
	}
	private static final DataStoreWrapper XMLStore = new XMLStore();
	
	public static DataStoreWrapper getDataStore(DataStore dataStore){
		DataStoreWrapper dataStoreWrapper = null;
		switch(dataStore){
		case XMLStore:
			dataStoreWrapper = XMLStore;
		}
		return dataStoreWrapper;
	}
}

