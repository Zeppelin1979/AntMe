package AntMe.Simulation;

import java.util.ArrayList;

import AntMe.SharedComponents.States.FruitState;

/// <summary>
/// Represents fruit.
/// </summary>

public class CoreFruit extends CoreFood {
    /// <summary>
    /// Liste der tragenden Ameisen.
    /// </summary>
    private ArrayList<CoreInsect> tragendeInsekten = new ArrayList<CoreInsect>();
    ArrayList<CoreInsect> getTragendeInsekten() {
        return tragendeInsekten;
    }

    /// <summary>
    /// Erzeugt eine neue Instanz der Obst-Klasse.
    /// </summary>
    /// <param name="x">Die X-Position des Obststücks auf dem Spielfeld.</param>
    /// <param name="y">Die Y-Position des Obststücks auf dem Spielfeld.</param>
    /// <param name="menge">Die Anzahl an Nahrungspunkten.</param>
    public CoreFruit(int x, int y, int menge) {
        super(x, y, menge);
    }

    /// <summary>
    /// Die verbleibende Menge an Nahrungspunkten.
    /// </summary>
    @Override
    public void setMenge(int value)
    {
        menge = value;
        koordinate.setRadius((int)(SimulationSettings.getCustom().FruitRadiusMultiplier *
                                        Math.sqrt(menge / Math.PI) * SimulationEnvironment.PLAYGROUND_UNIT));
    }

    /// <summary>
    /// Bestimmt, ob das Stück Obst für das angegebene Volk noch tragende
    /// Insekten benötigt, um die maximale Geschwindigkeit beim Tragen zu
    /// erreichen.
    /// </summary>
    /// <param name="colony">Das Volk.</param>
    public boolean brauchtNochTraeger(CoreColony colony)
    {
        int last = 0;
        for (CoreInsect insekt : tragendeInsekten)
        {
            if (insekt.getColony() == colony)
            {
                last += insekt.getAktuelleLastBase();
            }
        }
        return last * SimulationSettings.getCustom().FruitLoadMultiplier < menge;
    }

    /// <summary>
    /// Erzeugt ein ObstZustand-Objekt mit dem Daten Zustand des Obststücks.
    /// </summary>
    public FruitState erzeugeInfo()
    {
        FruitState info = new FruitState(getId());
        info.setPositionX((koordinate.getX() / SimulationEnvironment.PLAYGROUND_UNIT));
        info.setPositionY((koordinate.getY() / SimulationEnvironment.PLAYGROUND_UNIT));
        info.setRadius((koordinate.getRadius() / SimulationEnvironment.PLAYGROUND_UNIT));
        info.setAmount(menge);
        info.setCarryingAnts((byte)tragendeInsekten.size());
        return info;
    }
}
