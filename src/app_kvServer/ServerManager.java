package app_kvServer;

public class ServerManager {

	private final static String LOG = "LOG:SRVRMNGR:";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(LOG + "initializing server...");
		KVServer kvServer = new KVServer(1234, 256, "LRU");
		
		System.out.println(LOG + "initializing server system...");
		ServerSystem.initializeSystem();
		
		System.out.println(LOG + "Spinning the server..");
		kvServer.spin();

	}

}
