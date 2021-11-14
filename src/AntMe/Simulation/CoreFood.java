package AntMe.Simulation;

/// <summary>
/// Die abstrakte Basisklasse f�r Nahrung.
/// </summary>
public class CoreFood implements ICoordinate {
    // Die Id der n�chsten erzeugten Nahrung.
    private static int neueId = 0;

    /// <summary>
    /// Die Id die die Nahrung w�hrend eines Spiels eindeutig identifiziert.
    /// </summary>
    private int id;
    public int getId() {
        return id;
    }

    /// <summary>
    /// Die Position der Nahrung auf dem Spielfeld.
    /// </summary>
    protected CoreCoordinate koordinate;

    /// <summary>
    /// Die verbleibende Menge an Nahrungspunkten.
    /// </summary>
    protected int menge;

    /// <summary>
    /// Der abstrakte Nahrung-Basiskonstruktor.
    /// </summary>
    /// <param name="x">Die X-Position der Koordinate auf dem Spielfeld.</param>
    /// <param name="y">Die Y-Position der Koordinate auf dem Spielfeld.</param>
    /// <param name="menge">Die Anzahl der Nahrungspunkte.</param>
    public CoreFood(int x, int y, int menge)
    {
        id = neueId++;
        koordinate = new CoreCoordinate(x, y);
        this.menge = menge;
    }

    /// <summary>
    /// Die verbleibende Menge an Nahrungspunkten.
    /// </summary>
    public int getMenge() {
        return menge;
    }
    public void setMenge(int value) {
        menge = value;
        koordinate.setRadius((int)(Math.round(Math.sqrt(menge / Math.PI) * SimulationEnvironment.PLAYGROUND_UNIT)));
    }

    /// <summary>
    /// Die Position der Nahrung auf dem Spielfeld.
    /// </summary>
    public CoreCoordinate getCoordinateBase()
    {
        return koordinate;
    }
 
    public void setCoordinateBase(CoreCoordinate value) {
        koordinate = value;
    }

}
