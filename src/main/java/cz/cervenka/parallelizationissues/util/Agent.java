package cz.cervenka.parallelizationissues.util;

/**
 * The Agent class represents an entity that can perform an action and track whether it's acting.
 * The class provides synchronized methods to perform an action (toggle the state) and check if the agent is currently acting.
 */
public class Agent {

    private boolean action;  // Indicates if the agent is currently acting

    /**
     * Toggles the agent's action state.
     * If the agent is not acting, it starts acting; otherwise, it stops acting.
     * This method is synchronized to ensure thread safety when modifying the action state.
     */
    public synchronized void act() {
        this.action = !this.action;
    }

    /**
     * Returns whether the agent is currently not acting.
     * The state is negated from the `action` field to represent the opposite of the current action state.
     * This method is synchronized to ensure thread safety when reading the action state.
     *
     * @return true if the agent is not acting; false otherwise.
     */
    public synchronized boolean isActing() {
        return this.action;
    }
}
