package app_kvClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ClientSystem {

	private static final String LOG = "LOG:CLNTSYST:";

	public enum SystemState {
		CLOSED,
		RUNNING,
		INITIALIZED,
		READY,
		CONNECTED,
		TIMED_WAIT,
		WAIT,
		DISCONNECTED	
	}
	private StateMachine stateMachine;
	public static boolean isInit;
	public static boolean isCmdInit;
	

	State closed = new State(SystemState.CLOSED);
	State running = new State(SystemState.RUNNING);
	State initialized = new State(SystemState.INITIALIZED);
	State ready = new State(SystemState.READY);
	State connected = new State(SystemState.CONNECTED);
	State timedWait = new State(SystemState.TIMED_WAIT);
	State wait = new State(SystemState.WAIT);
	State disconnected = new State(SystemState.DISCONNECTED);

	Condition run = new Condition("runTrue");
	Condition init = new Condition("initTrue");
	Condition cmdReady = new Condition("cmdInit");
	Condition connect = new Condition("connectionSuccess");
	Condition request = new Condition("requestSent");


	public void initialize(){
		System.out.println(LOG + "initializing system");
		List<Transition> transitions = new ArrayList<>();
		transitions.add(new Transition(closed, new HashSet<>(Arrays.asList(run)), running));
		transitions.add(new Transition(running, new HashSet<>(Arrays.asList(init)), initialized));
		transitions.add(new Transition(initialized, new HashSet<>(Arrays.asList(cmdReady)), ready));
		transitions.add(new Transition(ready, new HashSet<>(Arrays.asList(connect)), connected));
		transitions.add(new Transition(connected, new HashSet<>(Arrays.asList(request)), timedWait));

		stateMachine = new StateMachine(initialized, transitions);
		
		//this.isInit = true;

	}
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
			valid = (curr.getState() == SystemState.READY);
			break;
		case DISCONNECTED:
			break;
		case INITIALIZED:
			valid = (curr.getState() == SystemState.RUNNING);
			break;
		case READY:
			valid = (curr.getState() == SystemState.INITIALIZED);
			break;
		case RUNNING:
			valid = (curr.getState() == SystemState.CLOSED);
			break;
		case TIMED_WAIT:
			valid = (curr.getState() == SystemState.CONNECTED);
			break;
		case WAIT:
			break;
		default:
			break;

		}
		return valid;
	}
	
	public void updateState(){
		State curr = stateMachine.getCurrent();
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
	
	public String getCurrState(){
		return stateMachine.getCurrent().getState().toString();
	}

}
