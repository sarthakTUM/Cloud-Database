package app_kvClient;

import java.util.HashMap;

import app_kvClient.Response.ResponseResult;
import app_kvClient.Response.ResponseSource;

import common.messages.SystemStates;

public class CommandController {

	private CommandModel command;
	private ClientView view;
	private static final String LOG = "LOG:COMMCONT:";
	private static HashMap<String, String> validCommand = new HashMap<String, String>();
	private Response response = null;
	private ClientSystem clientSystem;
	
	public static void initializeCommands(){
		
		
		System.out.println(LOG + "initializing commands");
		validCommand.put("PUT", "SERVER");
		validCommand.put("CONNECT", "SERVER");
		validCommand.put("GET", "SERVER");
		validCommand.put("DISCONNECT", "SERVER");
		
		/*
		 * TODO if there are no exceptions, then update the ClientSystem variable.
		 */
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
	public CommandController(CommandModel command, ClientView view, ClientSystem clientSystem){
		
		System.out.println(LOG + "parameterized constructor called");
		this.command = command;
		this.view = view;
		this.clientSystem = clientSystem;
		if(command != null){
			System.out.println(LOG + "calling constructor with: " + command.getClass().getSimpleName() + " " + view.getClass().getSimpleName());
		}
		
	}
	
	public void initProcessing(){
		
		System.out.println(LOG + "in initProcessing()");
		System.out.println(LOG + "Type of command : " + this.command.getClass().getSimpleName());
		
		// check validity of command using polymorphic method
		response = this.command.checkValidity();
		if(response.getResponseResult() == ResponseResult.SUCCESS){
			
			System.out.println(LOG + "Command validity: " + response.getResponseResult());
			
			
			// check the system state validity for command.
			response = checkSystemState(command);
				
			if(response.getResponseResult() == ResponseResult.SUCCESS){
				System.out.println(LOG + "system is in valid state: " + clientSystem.getCurrState());
				
				// get message handler using polymorphism.
				Handler messageHandler = command.getHandler();
				System.out.println(LOG + "MessageHandler class : " + messageHandler.getClass().getSimpleName());
				
				// process command using handler.
				response = messageHandler.processCommand(command);
				
				/*
				 * TODO update system state, based on response.
				 */
				if(response.getResponseResult() == ResponseResult.SUCCESS){
					/*
					 * TODO update the updateState() function to accept a group 
					 * 		of conditions and flag dictating whether the group
					 * 		was successful or not.
					 */
					clientSystem.updateState();
				}

			}
			else{
				// TODO handle else case
				System.out.println(LOG + "system is not in valid state : " + clientSystem.getCurrState());
				response.setResponseMessage(response.getResponseMessage() + "\nCommand not executed.");
			}
		}
		else{
			System.out.println(LOG + "invalid command: " + command.getCommandInstruction());
			response.setResponseMessage(response.getResponseMessage() + " Please enter the command again.");
		}
			
		
	}
	
	public void updateView(){
		// TODO get KVMessage from handler and pass it to printResponse
		if(this.command == null){
			System.out.println(LOG + "invalid command entered");
			response = new Response(ResponseSource.CLIENT, ResponseResult.FAIL, "Invalid Command entered, please enter the command again");
		}
		view.printResponse(response);
		
		// TODO define printResponse(Response)
	}
	
	public static HashMap<String, String> getValidCommands(){
		return validCommand;
	}
	
	private Response checkSystemState(CommandModel command){
		
		response = new Response(ResponseSource.CLIENT, ResponseResult.SUCCESS, "System state check passed");
		switch(command.getCommandInstruction()){
		case "CONNECT":
			if(!clientSystem.isValidTransition(new State(SystemStates.CONNECTED))){
				response = new Response(ResponseSource.CLIENT, ResponseResult.FAIL, "The system is not running");
			}
			break;
		case "PUT":
			if(!clientSystem.isValidTransition(new State(SystemStates.TIMED_WAIT))){
				response = new Response(ResponseSource.CLIENT, ResponseResult.FAIL, "The system is not connected to any server. Please connect and try again");
			}
			break;
		case "GET":
			if(!clientSystem.isValidTransition(new State(SystemStates.TIMED_WAIT))){
				response = new Response(ResponseSource.CLIENT, ResponseResult.FAIL, "The system is not connected to any server. Please connect and try again");
			}
			break;
			default:
				
		}
		return response;
		
	}

	
	
}
