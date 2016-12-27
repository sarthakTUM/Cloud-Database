package app_kvClient;

import app_kvClient.ClientSystem.SystemState;

public class State {

	private final SystemState state;

	public State(SystemState systemState){
		this.state = systemState;
	}

    @Override
    public String toString() {
        return "State [state=" + state + "]";
    }

    public SystemState getState() {
        return state;
    }
}
