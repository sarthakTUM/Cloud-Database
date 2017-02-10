package app_kvServer;

public class ServerManager {

	private final static String LOG = "LOG:SRVRMNGR:";
	
	public static void main(String[] args) {
		
		ServerSystem.setRunning(true);
		// TODO Auto-generated method stub
		System.out.println(LOG + "initializing server...");
		//KVServer kvServer = new KVServer((int)Integer.valueOf(args[0]), (int)Integer.valueOf(args[1]), args[2]);
		KVServer kvServer = new KVServer(1234, 10, "LIFO");
		
		System.out.println(LOG + "initializing server system...");
		//ServerSystem.initializeSystem("127.0.0.1",(int)Integer.valueOf(args[0]));
		ServerSystem.initializeSystem("127.0.0.1", 1234);
		
		System.out.println(LOG + "Spinning the server..");
		kvServer.spin();

	}

}
