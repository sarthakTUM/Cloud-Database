/**
 * @author Sarthak Gupta
 */

package app_kvClient;

import java.util.List;
import java.util.Set;

public class StateMachine {

	private final static String LOG = "LOG:STATMCHN:";
	private final List<Transition> transitions;
    private State current;

    /**
     * 
     * @param start: The starting state of the system.
     * @param transitions: The valid System Transitions.
     */
    public StateMachine(State start, List<Transition> transitions) {
    	System.out.println(LOG + "initializing state machine with : " + start.getState().toString());
        this.current = start;
        this.transitions = transitions;
    }

    /**
     * Applies the coniditions to the system and updates the new system state to the new 
     * based on conditions.
     * @param conditions
     */
    public void apply(Set<Condition> conditions) {
        current = getNextState(conditions);
        System.out.println(LOG + "updated system state: " + current.getState());
    }
    
    /**
     * 
     * @param conditions the set of conditions necessary for the next state
     * @return the next state based on the current state and conditions passed.
     */
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
