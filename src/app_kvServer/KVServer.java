package app_kvServer;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.io.IOException;

//import logging.LogSetup;



//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;



/**
 * Represents a simple Echo Server implementation.
 */
public class KVServer extends Thread {
	public static boolean serveractivecheck = false;  
	public static String metadata;
	//Private static Logger log = Logger.getRootLogger ();
	public static HashMap<String, Node> cacheMemory = new HashMap<String, Node>();
	private int port;
    private ServerSocket serverSocket;
    private boolean running;
    
    /**
     * Constructs a (Echo-) Server object which listens to connection attempts 
     * at the given port.
     * 
     * @param port a port number which the Server is listening to in order to 
     * 		establish a socket connection to a client. The port number should 
     * 		reside in the range of dynamic ports, i.e 49152 – 65535.
     */
    public KVServer(int port){
        this.port = port;
    }

    /**
     * Initializes and starts the server. 
     * Loops until the the server should be closed.
     */
    public void run() {
        
    	running = initializeServer();
        
        if(serverSocket != null) {
	        while(isRunning()){
	            try {
	                Socket client = serverSocket.accept();                
	                ClientConnection connection = 
	                		new ClientConnection(client);
	                new Thread(connection).start();
	                
	                /*logger.info("Connected to " 
	                		+ client.getInetAddress().getHostName() 
	                		+  " on port " + client.getPort());*/
	            } catch (IOException e) {
	            	/*logger.error("Error! " +
	            			"Unable to establish connection. \n", e);*/
	            }
	        }
        }
        //logger.info("Server stopped.");
    }
    
    private boolean isRunning() {
        return this.running;
    }

    /**
     * Stops the server insofar that it won't listen at the given port any more.
     */
    public void stopServer(){
        running = false;
        try {
			serverSocket.close();
		} catch (IOException e) {
			/*logger.error("Error! " +
					"Unable to close socket on port: " + port, e);*/
		}
    }

    private boolean initializeServer() {
    	//logger.info("Initialize server ...");
    	try {
            serverSocket = new ServerSocket(port);
           /* logger.info("Server listening on port: " 
            		+ serverSocket.getLocalPort());  */  
            return true;
        
        } catch (IOException e) {
        	//logger.error("Error! Cannot open server socket:");
            if(e instanceof BindException){
            	e.printStackTrace();//logger.error("Port " + port + " is already bound!");
            }
            return false;
        }
    }
    
    /**
     * Main entry point for the echo server application. 
     * @param args contains the port number at args[0].
     */
    public static void main(String[] args) {
    	try {
			//new LogSetup("logs/server.log", Level.ALL);
			//if(args.length != 1) {
				//System.out.println("Error! Invalid number of arguments!");
				//System.out.println("Usage: Server <port>!");
			//} else {
				//int port = Integer.parseInt(args[0]);
			int k=Integer.parseInt(args[0]);
				System.out.println(k);
    		new KVServer(k).start();
			//}
		} catch (NumberFormatException nfe) {
			System.out.println("Error! Invalid argument <port>! Not a number!");
			System.out.println("Usage: Server <port>!");
			System.exit(1);
		}
    }
    
    public static int getCacheSize(){
    	
    	return 12800;
    }
    public static boolean isServerActiveCheck() {
		return serveractivecheck;
	}

   // public static void  (boolean serveractivecheck) {
		//serveractivecheck = serveractivecheck;
	//}
	
	//public static boolean serveractivecheck = false;
	public static String getMetadata() {
		return metadata;
	}
	public static void setMetadata(String metadata) {
		//Manager.metadata = metadata;
	}
}
