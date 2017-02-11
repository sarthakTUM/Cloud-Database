/**
 * @author Sarthak Gupta
 * 
 * Factory Class for constructing the commands based on string command line.
 */
package app_kvClient;

public class CommandFactory {

	private static final String LOG = "LOG:COMDFCTY:";
	
	/**
	 * 
	 * @param command : the string command line input
	 * @param commandType: the type of command - Server, Client
	 * @param tokens - tokens of the string input.
	 * @return the Command Model pertaining to relevant command type.
	 */
	public CommandModel getCommand(String command, String commandType, String[] tokens){

		CommandModel commandModel = null;
		System.out.println(LOG + "command factory building with: " + command + " " + commandType + " " + tokens.length);

		switch(commandType){
		case "SERVER":
			ServerCommand serverCommand = new ServerCommand();
			serverCommand.setCommandInstruction(command.toUpperCase());
			serverCommand.setMessageType(commandType.toUpperCase());
			serverCommand.setHandler(new ServerMessageHandler());
			serverCommand.setCommandAttributes(tokens);
			serverCommand.setMessageSource("CLIENT");
			commandModel = serverCommand;
			break;
		}


		return commandModel;
	}
}
