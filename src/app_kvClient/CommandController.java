package app_kvClient;

import java.util.HashMap;

import app_kvClient.Response.ResponseResult;

public class CommandController {

	private CommandModel command;
	private ClientView view;
	private static final String LOG = "LOG:COMMCONT:";
	private static HashMap<String, String> validCommand = new HashMap<String, String>();
	private Response response = null;
	
	public static void initializeCommands(){
		
		System.out.println(LOG + "initializing commands");
		validCommand.put("PUT", "SERVER");
		validCommand.put("CONNECT", "SERVER");
		validCommand.put("GET", "SERVER");
		validCommand.put("DISCONNECT", "SERVER");
	}
	
	public CommandModel buildCommand(String cmdLine){
		
		CommandModel commandModel = null;
		CommandFactory commandFactory = new CommandFactory();
		
		// tokenize the cmdLine
		String[] tokens = cmdLine.split("\\s+");
		
		// if the command is available in valid commands
		if(validCommand.containsKey(tokens[0].toUpperCase())){
			commandModel = commandFactory.getCommand(tokens[0].toUpperCase(), validCommand.get(tokens[0].toUpperCase()), tokens);
		}
		else{
			// TODO handle else case, if command is not presnt in the validCommands.
		}
		
		System.out.println(LOG + "class of commandModel returned by command factory: " + commandModel.getClass().getSimpleName());
		return commandModel;
	}
	
	public CommandController(){
		System.out.println(LOG + "default constructor");
	}
	public CommandController(CommandModel command, ClientView view){
		
		this.command = command;
		this.view = view;
		System.out.println(LOG + "calling constructor with: " + command.getClass().getSimpleName() + " " + view.getClass().getSimpleName());
		
	}
	
	public void initProcessing(){
		// check validity of command using polymorphic method
		System.out.println(LOG + "in initProcessing()");
		System.out.println(LOG + "Type of command : " + this.command.getClass().getSimpleName());
		response = this.command.checkValidity();
		System.out.println(LOG + "Command validity: " + response.getResponseResult());
		
		// get its handler using polymorphic method
		if(response.getResponseResult() == ResponseResult.SUCCESS){
			Handler messageHandler = command.getHandler();
			System.out.println(LOG + "MessageHandler class : " + messageHandler.getClass().getSimpleName());
			
			// process command using handler.
			messageHandler.processCommand(command);
		}
		else{
			// TODO handle else case
			response.setResponseMessage(response.getResponseMessage() + " Please enter the command again.");
		}
	}
	
	public void updateView(){
		// TODO get KVMessage from handler and pass it to printResponse
		view.printResponse(response);
		
		// TODO define printResponse(Response)
	}
	
	public static HashMap<String, String> getValidCommands(){
		return validCommand;
	}

	
	
}
