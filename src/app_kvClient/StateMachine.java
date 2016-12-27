package app_kvClient;

import java.util.List;
import java.util.Set;

public class StateMachine {

	private final static String LOG = "LOG:STATMCHN:";
	private final List<Transition> transitions;
    private State current;

    public StateMachine(State start, List<Transition> transitions) {
    	System.out.println(LOG + "initializing state machine with : " + start.getState().toString());
        this.current = start;
        this.transitions = transitions;
    }

    public void apply(Set<Condition> conditions) {
        current = getNextState(conditions);
    }

    State getNextState(Set<Condition> conditions) {
        for (Transition transition : transitions) {
            boolean currentStateMatches = transition.from().equals(current);
            boolean conditionsMatch = transition.getConditions().equals(conditions);
            if (currentStateMatches && conditionsMatch) {
                return transition.to();
            }
        }
        return current;
    }

    public State getCurrent() {
        return current;
    }
}
