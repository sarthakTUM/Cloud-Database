package app_kvClient;

public class CommandFactory {

	/*
	 * TODO handle the remaining CASES depending on type of messages available.
	 */
	private static final String LOG = "LOG:COMDFCTY:";
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
			commandModel = serverCommand;
			break;
		}


		return commandModel;
	}
}
