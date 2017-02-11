/**
 * @author Sarthak Gupta
 */

package app_kvClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import app_kvServer.ServerContainerModel;

import common.messages.SystemStates;

public class ClientSystem {

	private static final String LOG = "LOG:CLNTSYST:";
	public static final String clientSyncFolderName = "ClientSyncFiles";
	File clientFolder = new File(clientSyncFolderName);
	

	private static ServerContainerModel metadata = new ServerContainerModel();

	private StateMachine stateMachine;
	public static boolean isInit;
	public static boolean isCmdInit;
	

	State closed = new State(SystemStates.CLOSED);
	State running = new State(SystemStates.RUNNING);
	State initialized = new State(SystemStates.INITIALIZED);
	State ready = new State(SystemStates.READY);
	State connected = new State(SystemStates.CONNECTED);
	State timedWait = new State(SystemStates.TIMED_WAIT);
	State wait = new State(SystemStates.WAIT);
	State disconnected = new State(SystemStates.DISCONNECTED);

	Condition run = new Condition("runTrue");
	Condition init = new Condition("initTrue");
	Condition cmdReady = new Condition("cmdInit");
	Condition connect = new Condition("connectionSuccess");
	Condition request = new Condition("requestSent");

	/**
	 * Initialized the Client System with the following:
	 * 1. State Machine responsible for maintaing the Finite State Machine attached
	 * 		to Client Application.
	 */

	public void initialize(){
		System.out.println(LOG + "initializing system");
		List<Transition> transitions = new ArrayList<>();
		transitions.add(new Transition(closed, new HashSet<>(Arrays.asList(run)), running));
		transitions.add(new Transition(running, new HashSet<>(Arrays.asList(init)), initialized));
		transitions.add(new Transition(initialized, new HashSet<>(Arrays.asList(cmdReady)), ready));
		transitions.add(new Transition(ready, new HashSet<>(Arrays.asList(connect)), connected));
		transitions.add(new Transition(connected, new HashSet<>(Arrays.asList(request)), connected));

		stateMachine = new StateMachine(initialized, transitions);
		
		if(!clientFolder.exists()){
			clientFolder.mkdir();
		}

	}
	
	/**
	 * 
	 * @param toState: the next state to which system wants to make transition to.
	 * @return whether the transition is valid or not
	 */
	public boolean isValidTransition(State toState){

		/*
		 * TODO check if moving from current state to TOSTATE is valid.
		 */
		boolean valid = false;
		State curr = stateMachine.getCurrent();
		switch(toState.getState()){
		case CLOSED:
			break;
		case CONNECTED:
			valid = (curr.getState() == SystemStates.READY);
			break;
		case DISCONNECTED:
			break;
		case INITIALIZED:
			valid = (curr.getState() == SystemStates.RUNNING);
			break;
		case READY:
			valid = (curr.getState() == SystemStates.INITIALIZED);
			break;
		case RUNNING:
			valid = (curr.getState() == SystemStates.CLOSED);
			break;
		case TIMED_WAIT:
			valid = (curr.getState() == SystemStates.CONNECTED);
			break;
		case WAIT:
			break;
		default:
			break;

		}
		return valid;
	}
	
	/**
	 * updates the system state based on current system state and set of Conditions.
	 */
	public void updateState(){
		State curr = stateMachine.getCurrent();
		System.out.println(LOG + "current system state: " + curr.getState());
		switch(curr.getState()){
		case CLOSED:
			break;
		case CONNECTED:
			stateMachine.apply(new HashSet<>(Arrays.asList(request)));
			break;
		case DISCONNECTED:
			break;
		case INITIALIZED:
			stateMachine.apply(new HashSet<>(Arrays.asList(cmdReady)));
			break;
		case READY:
			stateMachine.apply(new HashSet<>(Arrays.asList(connect)));
			break;
		case RUNNING:
			break;
		case TIMED_WAIT:
			break;
		case WAIT:
			break;
		default:
			break;
	
		}
	}
	
	
	/**
	 * 
	 * @return the current System state.
	 */
	public String getCurrState(){
		return stateMachine.getCurrent().getState().toString();
	}
	
	public static ServerContainerModel getMetadata(){
		return metadata;
		
	}

}
