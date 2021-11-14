package AntMe.SharedComponents.Plugin;

import AntMe.SharedComponents.States.SimulationState;

public abstract class IConsumerPlugin extends IPlugin {
    /// <summary>
    /// Allows a plugin to signal an interrupt. Only called by GameLoop-Thread.
    /// </summary>
    private boolean interrupt;
    
    public boolean getInterrupt() {
    	return interrupt;
    }

    /// <summary>
    /// Sends the empty state to push some custom fields to control the simulation. Only called by GameLoop-Thread.
    /// </summary>
    /// <param name="state">empty state</param>
    public abstract void createState(SimulationState state);

    /// <summary>
    /// Sends the filled state to push some custom fields to control the other consumers. Only called by GameLoop-Thread.
    /// </summary>
    /// <param name="state">filled state</param>
    public abstract void creatingState(SimulationState state);

    /// <summary>
    /// Sends the complete filled state to consume. Only called by GameLoop-Thread.
    /// </summary>
    /// <param name="state">complete state</param>
    public abstract void createdState(SimulationState state);

}
