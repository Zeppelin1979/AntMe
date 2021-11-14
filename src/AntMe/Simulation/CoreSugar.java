package AntMe.Simulation;
/// <summary>
/// Represents a sugar-Hill.
/// </summary>

import AntMe.SharedComponents.States.SugarState;

public final class CoreSugar extends CoreFood {
    /// <summary>
    /// Creates an instance of CoreSugar.
    /// </summary>
    /// <param name="x">The x-position of sugar on playground.</param>
    /// <param name="y">The y-position of sugar on playground.</param>
    /// <param name="amount">The amount of food.</param>
    CoreSugar(int x, int y, int amount) {
        super(x, y, amount);
    }

    /// <summary>
    /// Creates a sugar-state of this sugar-hill.
    /// </summary>
    /// <returns>current state of that sugar-hill.</returns>
    SugarState CreateState() {
        SugarState state = new SugarState(getId());
        state.setPositionX(koordinate.getX()/SimulationEnvironment.PLAYGROUND_UNIT);
        state.setPositionY(koordinate.getY()/SimulationEnvironment.PLAYGROUND_UNIT);
        state.setRadius(koordinate.getRadius()/SimulationEnvironment.PLAYGROUND_UNIT);
        state.setAmount(menge);
        return state;
    }

}
