package app_kvClient;

public class ServerMessageHandler extends Handler{

	private static final String LOG = "LOG:SMHANDLR:";
    /*
	 * TODO implement switch case for handling various commands
	 *
	 * and call appropriate KVStore method which implements our
	 * communication interface.
	 */
	
	public void processCommand(CommandModel serverCommand){
		System.out.println(LOG + "Command Model Class: " + serverCommand.getClass().getSimpleName());
		switch(serverCommand.getCommandInstruction()){
		case "CONNECT":
			// TODO call KVStore  connect method
			
			break;
			default:
				// TODO error message
		}
	}
	
}
