package AntMe.Simulation;
/// <summary>
/// Klasse zur Haltung aller relevanten Konfigurationsdaten einer Simulation.
/// </summary>

import java.util.ArrayList;

public class SimulatorConfiguration implements Cloneable {
    /// <summary>
    /// Maximum count of players per Simulation.
    /// </summary>
    public final int PLAYERLIMIT = 8;

    /// <summary>
    /// Minimum count of rounds for a valid simulation.
    /// </summary>
    public final int ROUNDSMIN = 1;

    /// <summary>
    /// Maximum count of rounds for a valid simulation.
    /// </summary>
    public final int ROUNDSMAX = Integer.MAX_VALUE;

    /// <summary>
    /// Minimum count of loops for a valid simulation.
    /// </summary>
    public final int LOOPSMIN = 1;
    /// <summary>
    /// 
    /// Maximum count of loops for a valid simulation.
    /// </summary>
    public final int LOOPSMAX = Integer.MAX_VALUE;

    /// <summary>
    /// Minimum value for round-timeouts.
    /// </summary>
    public final int ROUNDTIMEOUTMIN = 1;

    /// <summary>
    /// Minimum value for loop-timeouts.
    /// </summary>
    public final int LOOPTIMEOUTMIN = 1;

    // Runden- und Spielereinstellungen
    private int loopCount;
    private int roundCount;
    private boolean allowDebuginformation;
    private int loopTimeout;

    // Zus�tzliche Rechte anforderbar
    private boolean allowDatabaseAccess;
    private boolean allowFileAccess;
    private boolean allowReferences;
    private boolean allowUserinterfaceAccess;
    private boolean allowNetworkAccess;

    /// <summary>
    /// Timeout-Handling
    /// </summary>
    private boolean ignoreTimeouts;

    private int roundTimeout;
    private ArrayList<TeamInfo> teams;

    // Sonstiges
    private int mapInitialValue;
    private SimulationSettings settings;

    /// <summary>
    /// Initialisiert eine leere Spielerliste
    /// </summary>
    public SimulatorConfiguration()
    {
        roundCount = 5000;
        loopCount = 1;
        teams = new ArrayList<TeamInfo>();

        ignoreTimeouts = true;
        roundTimeout = 1000;
        loopTimeout = 6000;

        allowDatabaseAccess = false;
        allowReferences = false;
        allowUserinterfaceAccess = false;
        allowFileAccess = false;
        allowNetworkAccess = false;

        allowDebuginformation = false;
        mapInitialValue = 0;

        settings.setDefaults();
    }

    /// <summary>
    /// Initialsiert mit den �bergebenen Werten
    /// </summary>
    /// <param name="loops">Anzahl Durchl�ufe</param>
    /// <param name="rounds">Anzahl Runden</param>
    /// <param name="teams">Teamliste</param>
    public SimulatorConfiguration(int loops, int rounds, ArrayList<TeamInfo> teams)
    {
        this();
        if (teams != null)
        {
            this.teams = teams;
        }
        roundCount = rounds;
        loopCount = loops;
        ignoreTimeouts = false;
    }

    /// <summary>
    /// Ermittelt, ob die Angaben der Konfiguration simulationsf�hig sind
    /// </summary>
    /// <returns>Regelkonformer Konfigurationsinhalt</returns>
    public void rulecheck()
    {
        // Rundenanzahl pr�fen
        if (roundCount < ROUNDSMIN)
        {
            throw new ConfigurationErrorsException(Resource.SimulationCoreConfigurationRoundCountTooSmall);
        }
        if (roundCount > ROUNDSMAX)
        {
            throw new ConfigurationErrorsException(
                String.format(Resource.SimulationCoreConfigurationRoundCountTooBig, ROUNDSMAX));
        }

        // Durchlaufmenge pr�fen
        if (loopCount < LOOPSMIN)
        {
            throw new ConfigurationErrorsException(Resource.SimulationCoreConfigurationLoopCountTooSmall);
        }
        if (loopCount > LOOPSMAX)
        {
            throw new ConfigurationErrorsException(
                String.format(Resource.SimulationCoreConfigurationLoopCountTooBig, LOOPSMAX));
        }

        // Timeoutwerte
        if (!ignoreTimeouts)
        {
            if (loopTimeout < LOOPTIMEOUTMIN)
            {
                throw new ConfigurationErrorsException(
                    Resource.SimulationCoreConfigurationLoopTimeoutTooSmall);
            }
            if (roundTimeout < ROUNDTIMEOUTMIN)
            {
                throw new ConfigurationErrorsException(
                    Resource.SimulationCoreConfigurationRoundTimeoutTooSmall);
            }
        }

        // Teams checken
        if (teams == null || teams.Count < 0)
        {
            throw new ConfigurationErrorsException(
                Resource.SimulationCoreConfigurationNoTeams);
        }

        // Regelcheck der Teams
        int playerCount = 0;
        for (TeamInfo team : teams)
        {
            team.rulecheck();
            playerCount += team.getPlayer().size();
        }

        if (playerCount > PLAYERLIMIT)
        {
            // TODO: Put string into res-file
            throw new ConfigurationErrorsException("Too many players");
        }

        // Regeln f�r die kern-Einstellungen
        settings.ruleCheck();
    }


    /// <summary>
    /// Erstellt eine Kopie der Konfiguration
    /// </summary>
    /// <returns>Kopie der aktuellen Konfiguration</returns>
    @Override
    public Object clone()
    {
        // Kopie erstellen und Spielerliste kopieren
        SimulatorConfiguration output = new SimulatorConfiguration();
        output.allowDatabaseAccess = allowDatabaseAccess;
        output.allowDebuginformation = allowDebuginformation;
        output.allowFileAccess = allowFileAccess;
        output.allowNetworkAccess = allowNetworkAccess;
        output.allowReferences = allowReferences;
        output.allowUserinterfaceAccess = allowUserinterfaceAccess;
        output.ignoreTimeouts = ignoreTimeouts;
        output.loopCount = loopCount;
        output.loopTimeout = loopTimeout;
        output.mapInitialValue = mapInitialValue;
        output.roundCount = roundCount;
        output.roundTimeout = roundTimeout;
        output.settings = settings;

        output.teams = new ArrayList<TeamInfo>(teams.size());
        for (TeamInfo team : teams)
        {
            output.teams.add((TeamInfo)team.clone());
        }
        return output;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }

    public boolean isAllowDebuginformation() {
        return allowDebuginformation;
    }

    public void setAllowDebuginformation(boolean allowDebuginformation) {
        this.allowDebuginformation = allowDebuginformation;
    }

    public int getLoopTimeout() {
        return loopTimeout;
    }

    public void setLoopTimeout(int loopTimeout) {
        this.loopTimeout = loopTimeout;
    }

    public boolean isAllowDatabaseAccess() {
        return allowDatabaseAccess;
    }

    public void setAllowDatabaseAccess(boolean allowDatabaseAccess) {
        this.allowDatabaseAccess = allowDatabaseAccess;
    }

    public boolean isAllowFileAccess() {
        return allowFileAccess;
    }

    public void setAllowFileAccess(boolean allowFileAccess) {
        this.allowFileAccess = allowFileAccess;
    }

    public boolean isAllowReferences() {
        return allowReferences;
    }

    public void setAllowReferences(boolean allowReferences) {
        this.allowReferences = allowReferences;
    }

    public boolean isAllowUserinterfaceAccess() {
        return allowUserinterfaceAccess;
    }

    public void setAllowUserinterfaceAccess(boolean allowUserinterfaceAccess) {
        this.allowUserinterfaceAccess = allowUserinterfaceAccess;
    }

    public boolean isAllowNetworkAccess() {
        return allowNetworkAccess;
    }

    public void setAllowNetworkAccess(boolean allowNetworkAccess) {
        this.allowNetworkAccess = allowNetworkAccess;
    }

    public boolean isIgnoreTimeouts() {
        return ignoreTimeouts;
    }

    public void setIgnoreTimeouts(boolean ignoreTimeouts) {
        this.ignoreTimeouts = ignoreTimeouts;
    }

    public int getRoundTimeout() {
        return roundTimeout;
    }

    public void setRoundTimeout(int roundTimeout) {
        this.roundTimeout = roundTimeout;
    }

    public ArrayList<TeamInfo> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<TeamInfo> teams) {
        this.teams = teams;
    }

    public int getMapInitialValue() {
        return mapInitialValue;
    }

    public void setMapInitialValue(int mapInitialValue) {
        this.mapInitialValue = mapInitialValue;
    }

    public SimulationSettings getSettings() {
        return settings;
    }

    public void setSettings(SimulationSettings settings) {
        this.settings = settings;
    }

}
