package AntMe.SharedComponents.Plugin;

import AntMe.SharedComponents.States.SimulationState;

public abstract class IProducerPlugin extends IPlugin {

    /// <summary>
    /// Sends the filled state from consumers to the producer to put in the <see cref="SimulationState"/>. Only called by GameLoop-Thread.
    /// </summary>
    /// <param name="state">filled state</param>
    public abstract void createState(SimulationState state);
}
