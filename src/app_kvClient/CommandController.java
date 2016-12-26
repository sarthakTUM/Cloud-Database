package app_kvClient;

import java.util.HashMap;

import app_kvClient.Response.ResponseResult;
import app_kvClient.Response.ResponseSource;

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
	
	public static CommandModel buildCommand(String cmdLine){
		
		CommandModel commandModel = null;
		CommandFactory commandFactory = new CommandFactory();
		
		// tokenize the cmdLine
		String[] tokens = cmdLine.split("\\s+");
		
		// if the command is available in valid commands
		if(validCommand.containsKey(tokens[0].toUpperCase())){
			commandModel = commandFactory.getCommand(tokens[0].toUpperCase(), validCommand.get(tokens[0].toUpperCase()), tokens);
			System.out.println(LOG + "class of commandModel returned by command factory: " + commandModel.getClass().getSimpleName());
		}
		// else case is handled in updateView()
		return commandModel;
	}
	
	public CommandController(){
		System.out.println(LOG + "default constructor");
	}
	public CommandController(CommandModel command, ClientView view){
		
		this.command = command;
		this.view = view;
		if(command != null){
			System.out.println(LOG + "calling constructor with: " + command.getClass().getSimpleName() + " " + view.getClass().getSimpleName());
		}
		
	}
	
	public void initProcessing(){
		// check validity of command using polymorphic method
		System.out.println(LOG + "in initProcessing()");
		System.out.println(LOG + "Type of command : " + this.command.getClass().getSimpleName());
		
		/*
		 * TODO implement checkSystem() function to verify whether the system state is
		 * suitable for completing the command.
		 */
		//response = checkSystemState(command);
		
		response = this.command.checkValidity();
		System.out.println(LOG + "Command validity: " + response.getResponseResult());
		
		// get its handler using polymorphic method
		if(response.getResponseResult() == ResponseResult.SUCCESS){
			Handler messageHandler = command.getHandler();
			System.out.println(LOG + "MessageHandler class : " + messageHandler.getClass().getSimpleName());
			
			// process command using handler.
			response = messageHandler.processCommand(command);
			
			// if command is processed correctly, receive message from client's input stream.
			/*if(response.getResponseResult() == ResponseResult.SUCCESS){
				//response = messageHandler.receieveMessage();
			}*/
			
			/*
			 * TODO if the response of receieve is success, process it.
			 */
		}
		else{
			// TODO handle else case
			response.setResponseMessage(response.getResponseMessage() + " Please enter the command again.");
		}
	}
	
	public void updateView(){
		// TODO get KVMessage from handler and pass it to printResponse
		if(this.command == null){
			response = new Response(ResponseSource.CLIENT, ResponseResult.FAIL, "Invalid Command entered, please enter the command again");
		}
		view.printResponse(response);
		
		// TODO define printResponse(Response)
	}
	
	public static HashMap<String, String> getValidCommands(){
		return validCommand;
	}

	
	
}
