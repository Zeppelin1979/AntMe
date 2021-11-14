package AntMe.Simulation;

import java.util.HashMap;
import java.util.Random;

import AntMe.SharedComponents.States.BugState;

/// <summary>
/// Eine Wanze
/// </summary>
/// <author>Wolfgang Gallo (wolfgang@antme.net)</author>

final class CoreBug extends CoreInsect {
    /// <summary>
    /// Gibt an, ob die Wanze sich in der aktuellen Runde noch bewegen kann.
    /// </summary>
    private boolean kannSichNochBewegen = true;
    boolean getKannSichNochBewegen() {
        return kannSichNochBewegen;
    }

    @Override
    void init(CoreColony colony, Random random, HashMap<String, Integer> vorhandeneInsekten)
    {
        super.init(colony, random, vorhandeneInsekten);
        getKoordinate().setRadius(4);
        setAktuelleEnergieBase(colony.getEnergie()[0]);
        aktuelleGeschwindigkeitI = colony.getGeschwindigkeitI()[0];
        setAngriffBase(colony.getAngriff()[0]);
    }

    /// <summary>
    /// Erzeugt ein BugState-Objekt mit dem aktuellen Daten der Wanzen.
    /// </summary>
    /// <returns></returns>
    BugState erzeugeInfo()
    {
        BugState info = new BugState(getId());
        info.setPositionX((int)(getCoordinateBase().getX() / SimulationEnvironment.PLAYGROUND_UNIT));
        info.setPositionY((int)(getCoordinateBase().getY() / SimulationEnvironment.PLAYGROUND_UNIT));
        info.setDirection(getCoordinateBase().getRichtung());
        info.setVitality(getAktuelleEnergieBase());
        return info;
    }
}
