package AntMe.Simulation;

import AntMe.SharedComponents.States.MarkerState;

/// <summary>
/// Eine Duft-Markierung die eine Information enth�lt.
/// </summary>
/// <author>Wolfgang Gallo (wolfgang@antme.net)</author>
public final class CoreMarker implements ICoordinate {
    // Die Id der n�chsten erzeugten Markierung.
    private static int neueId = 0;

    /// <summary>
    /// Die Id die die Markierung w�hrend eines Spiels eindeutig identifiziert.
    /// </summary>
    private int id;
    public int getId() {
        return id;
    }

    private int colonyId;
    private CoreCoordinate koordinate;

    private int age = 0;
    private int totalAge;
    private int endgroesse;
    private int information;


    /// <summary>
    /// Erzeugt eine neue Instanz der Markierung-Klasse.
    /// </summary>
    /// <param name="koordinate">Die Koordinate der Markierung.</param>
    /// <param name="endgr��e">Die Ausbreitung der Markierung in Schritten.
    /// </param>
    /// <param name="colonyId">ID des Volkes</param>
    CoreMarker(CoreCoordinate koordinate, int endgroessee, int colonyId)
    {
        this.colonyId = colonyId;
        id = neueId++;
        this.koordinate = koordinate;

        // Volumen der kleinsten Markierung (r� * PI/2) ermitteln (Halbkugel)
        double baseVolume = Math.pow(SimulationSettings.getCustom().MarkerSizeMinimum, 3) * (Math.PI / 2);

        // Korrektur f�r gr��ere Markierungen
        baseVolume *= 10f;

        // Gesamtvolumen �ber die volle Zeit ermitteln
        double totalvolume = baseVolume * SimulationSettings.getCustom().MarkerMaximumAge;

        // Maximale Markergr��e ermitteln
        int maxSize = (int)Math.pow(4 * ((totalvolume - baseVolume) / Math.PI), 1f / 3f);

        // Gew�nschte Zielgr��e limitieren (Min Markersize, Max MaxSize)
        this.endgroesse = Math.max(SimulationSettings.getCustom().MarkerSizeMinimum, Math.min(maxSize, endgroesse));

        // Volumen f�r die gr��te Markierung ermitteln
        int diffRadius = this.endgroesse - SimulationSettings.getCustom().MarkerSizeMinimum;
        double diffVolume = Math.pow(diffRadius, 3) * (Math.PI / 4);

        // Lebenszeit bei angestrebter Gesamtgr��e ermitteln
        totalAge = (int)Math.floor(totalvolume / (baseVolume + diffVolume));
        aktualisieren();
    }

    /// <summary>
    /// Die gespeicherte Information.
    /// </summary>
    public int getInformation() {
        return information;
    }
    void setInformation(int value) {
        information = value;
    }

    /// <summary>
    /// Bestimmt ob die Markierung ihre maximales Alter noch nicht �berschritten
    /// hat.
    /// </summary>
    public boolean isActive()
    {
        return age <= totalAge;
    }

        /// <summary>
    /// Die Position der Markierung auf dem Spielfeld.
    /// </summary>
    public CoreCoordinate getCoordinateBase()
    {
        return koordinate;
    }

        /// <summary>
    /// Erh�ht das Alter der Markierung und passt ihren Radius an.
    /// </summary>
    void aktualisieren()
    {
        age++;
        if (isActive())
        {
            koordinate.setRadius((int)(
                SimulationSettings.getCustom().MarkerSizeMinimum +
                endgroesse * ((float)age / totalAge)) * SimulationEnvironment.PLAYGROUND_UNIT);
        }
    }

    /// <summary>
    /// Erzeugt ein MarkierungZustand-Objekt mit den aktuellen Daten der
    /// Markierung.
    /// </summary>
    MarkerState erzeugeInfo()
    {
        MarkerState info = new MarkerState(colonyId, id);
        info.setPositionX(koordinate.getX() / SimulationEnvironment.PLAYGROUND_UNIT);
        info.setPositionY(koordinate.getY() / SimulationEnvironment.PLAYGROUND_UNIT);
        info.setRadius(koordinate.getRadius() / SimulationEnvironment.PLAYGROUND_UNIT);
        info.setDirection(koordinate.getRichtung());
        return info;
    }

}
