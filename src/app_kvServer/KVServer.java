package app_kvServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class KVServer extends Thread {
	
	private int port;
	private int cacheSize;
	private String strategy;
	private boolean isRunning;
	private ServerSocket serverSocket;
	private final static String LOG = "LOG:KVSERVER:";
  /**
	 * Start KV Server at given port
	 * @param port given port for storage server to operate
	 * @param cacheSize specifies how many key-value pairs the server is allowed 
	 *           to keep in-memory
	 * @param strategy specifies the cache replacement strategy in case the cache 
	 *           is full and there is a GET- or PUT-request on a key that is 
	 *           currently not contained in the cache. Options are "FIFO", "LRU", 
	 *           and "LFU".
	 */
	public KVServer(int port, int cacheSize, String strategy) {
		this.port = port;
		this.cacheSize = cacheSize;
		this.strategy = strategy;
		this.isRunning = false;
		System.out.println(LOG + "init server with parameter : " + this.port + " "  + this.cacheSize + " " + this.strategy);
		
	}
	
	public void spin(){
		try {
			serverSocket = new ServerSocket(this.port);
			System.out.println(LOG + "server socket : " + serverSocket);
			this.isRunning = true;
			
			System.out.println(LOG + "starting server thread");
			this.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		System.out.println("server thread running..");
		if(serverSocket != null){
			while(isRunning){
				try {
					Socket client = serverSocket.accept();
					ClientConnection clientConnection = new ClientConnection(client);
					new Thread(clientConnection).start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	

}
