/**
 * 
 * @author Sarthak Gupta
 */
package app_kvClient;

import common.messages.SystemStates;

public class State {

	private final SystemStates state;

	public State(SystemStates systemState){
		this.state = systemState;
	}

    @Override
    public String toString() {
        return "State [state=" + state + "]";
    }

    public SystemStates getState() {
        return state;
    }
}
