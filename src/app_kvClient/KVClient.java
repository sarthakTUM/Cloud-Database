/**
 * @author Sarthak Gupta
 */

package app_kvClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import common.messages.SystemStates;

/*
 * @author Sarthak
 * KVClient application is responsible for taking in input from the command line and
 * passing it to controller for further processing.
 */

public class KVClient {

	ClientSystem clientSystem = new ClientSystem();
	private boolean stop = false;
	private static final String PROMPT = "EchoClient> ";
	private BufferedReader stdin;
	private ClientView clientView = new ClientView();
	private final static String LOG = "LOG:KVCLIENT:";
	
	
	private void run(){
		clientSystem.initialize();
		if(clientSystem.isValidTransition(new State(SystemStates.READY))){
			System.out.println(LOG + "system is in valid state for transition to ready");
			CommandController.initializeCommands();
			clientSystem.updateState();
			System.out.println(LOG + "system state after init commands: " + clientSystem.getCurrState());
		}
		
		
		while(!stop){
			stdin = new BufferedReader(new InputStreamReader(System.in));
			System.out.print(PROMPT);
			CommandModel command = buildCommand(stdin);
			if(command != null){
				System.out.println(LOG + "class of command after building to check polymorphism: " + command.getClass().getSimpleName());
			}
				
			// TODO spawning a new thread for each command is better?
			CommandController cmdController = new CommandController(command, clientView, clientSystem);
			if(command != null){
				cmdController.initProcessing();
			}
			cmdController.updateView();		
		}
	}
	
	/**
	 * @return the polymorphic Command Model that is a representation of
	 * 			particular type of commands, Ex: ServerCommand, ClientCommand.
	 */
	private CommandModel buildCommand(BufferedReader stdin){
		CommandModel command = null;
		try {
			String cmdLine = stdin.readLine();
			System.out.println(LOG + "cmdLine : " + cmdLine);
			command = CommandController.buildCommand(cmdLine);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return command;
		
	}
	public static void main(String[] args){
		KVClient clientApp = new KVClient();
		
		System.out.println(LOG + "Running Client Application");
		clientApp.run();
	
	}
}
