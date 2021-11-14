package AntMe.Simulation;

import java.util.ArrayList;
import java.util.UUID;

import AntMe.PlayerManagement.PlayerLanguages;

public class PlayerInfo implements Cloneable {

    /// <summary>
    /// List of all castes.
    /// </summary>
    private ArrayList<CasteInfo> castes;

    /// <summary>
    /// Reference to the ai-assembly-file.
    /// </summary>
    private Assembly assembly;

    /// <summary>
    /// true, if the Ai gives some debug-information.
    /// </summary>
    private boolean hasDebugInformation;

    /// <summary>
    /// Guid of player.
    /// </summary>
    private UUID guid;

    /// <summary>
    /// Name of colony.
    /// </summary>
    private String colonyName;

    /// <summary>
    /// Complete Class-name of colony-class.
    /// </summary>
    private String className;

    /// <summary>
    /// true, if the colony needs access to a database.
    /// </summary>
    private boolean requestDatabaseAccess;

    /// <summary>
    /// true, if the colony needs access to files.
    /// </summary>
    private boolean requestFileAccess;

    /// <summary>
    /// true, if the colony needs access to the network.
    /// </summary>
    private boolean requestNetworkAccess;

    /// <summary>
    /// Additional information about access-requests.
    /// </summary>
    private String requestInformation;

    /// <summary>
    /// true, if the colony has references to other assemblies.
    /// </summary>
    private boolean requestReferences;

    /// <summary>
    /// true, if the colony needs access to user-interfaces.
    /// </summary>
    private boolean requestUserInterfaceAccess;

    /// <summary>
    /// Last name of colony-author.
    /// </summary>
    private String lastName;

    /// <summary>
    /// First name of colony-author.
    /// </summary>
    private String firstName;

    /// <summary>
    /// Language of colony.
    /// </summary>
    private PlayerLanguages language;

    /// <summary>
    /// true, if the colony uses any static types.
    /// </summary>
    private boolean staticTypes;

    /// <summary>
    /// Simulator-Version of this colony.
    /// </summary>
    private PlayerSimulationVersions simulationVersion;

    /// <summary>
    /// Creates a new instance of PlayerInfo.
    /// </summary>
    public PlayerInfo() {
        // Init default-values
        guid = UUID.randomUUID();
        colonyName = "";
        firstName = "";
        lastName = "";
        className = "";
        simulationVersion = PlayerSimulationVersions.Version_1_6;
        language = PlayerLanguages.Unknown;
        staticTypes = true;
        castes = new ArrayList<CasteInfo>();
        hasDebugInformation = false;
        requestUserInterfaceAccess = false;
        requestDatabaseAccess = false;
        requestFileAccess = false;
        requestReferences = false;
        requestNetworkAccess = false;
        requestInformation = "";
    }

    /// <summary>
    /// Creates a new instance of PlayerInfo.
    /// </summary>
    /// <param name="info">Base-info</param>
    public PlayerInfo(PlayerInfo info) {
        // Daten kopieren
        guid = info.getGuid();
        colonyName = info.getColonyName();
        firstName = info.getFirstName();
        lastName = info.getLastName();
        className = info.getClassName();
        simulationVersion = info.getSimulationVersion();
        language = info.getLanguage();
        staticTypes = info.isStaticTypes();
        castes = info.getCastes();
        hasDebugInformation = info.isHasDebugInformation();
        requestUserInterfaceAccess = info.isRequestUserInterfaceAccess();
        requestDatabaseAccess = info.isRequestDatabaseAccess();
        requestFileAccess = info.isRequestFileAccess();
        requestReferences = info.isRequestReferences();
        requestNetworkAccess = info.isRequestNetworkAccess();
        requestInformation = info.getRequestInformation();
    }

    /// <summary>
    /// Creates a new instance of PlayerInfo.
    /// </summary>
    /// <param name="guid">Guid of colony</param>
    /// <param name="colonyName">Name of colony</param>
    /// <param name="lastName">Last name of author</param>
    /// <param name="firstName">First name of author</param>
    /// <param name="className">Class-name of colony</param>
    /// <param name="staticVariables">Colony uses static types</param>
    /// <param name="simulationVersion">Version of simulator of this colony</param>
    /// <param name="language">Language of this colony</param>
    /// <param name="castes">List of castes</param>
    /// <param name="haveDebugInformation">Colony produces debug-information</param>
    /// <param name="requestUserinterfaceAccess">Needs access to user-interfaces</param>
    /// <param name="requestDatabaseAccess">Needs access to databases</param>
    /// <param name="requestFileAccess">Needs access to files</param>
    /// <param name="requestNetworkAccess">Needs access to network</param>
    /// <param name="requestReferences">Needs references to other assemblies</param>
    /// <param name="requestInformation">Additional information about security-requests</param>
    public PlayerInfo(
        UUID guid,
        String colonyName,
        String firstName,
        String lastName,
        String className,
        PlayerSimulationVersions simulationVersion,
        PlayerLanguages language,
        boolean staticVariables,
        ArrayList<CasteInfo> castes,
        boolean haveDebugInformation,
        boolean requestUserinterfaceAccess,
        boolean requestDatabaseAccess,
        boolean requestFileAccess,
        boolean requestNetworkAccess,
        boolean requestReferences,
        String requestInformation)
    {
        // Ameisenkasten �berpr�fen
        if (castes == null) {
            this.castes = new ArrayList<CasteInfo>();
        }
        else {
            this.castes = castes;
        }

        // Restliche Daten �bertragen
        this.guid = guid;
        this.colonyName = colonyName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.className = className;
        this.simulationVersion = simulationVersion;
        this.language = language;
        this.staticTypes = staticVariables;
        this.hasDebugInformation = haveDebugInformation;
        this.requestUserInterfaceAccess = requestUserinterfaceAccess;
        this.requestDatabaseAccess = requestDatabaseAccess;
        this.requestFileAccess = requestFileAccess;
        this.requestNetworkAccess = requestNetworkAccess;
        this.requestReferences = requestReferences;
        this.requestInformation = requestInformation;
    }


    /// <summary>
    /// Checks the rules.
    /// </summary>
    /// <throws><see cref="RuleViolationException"/></throws>
    public void ruleCheck() {
        
        // Invalidate colonies without a name
        if (colonyName.isEmpty()) {
            throw new RuleViolationException(
                string.Format(Resource.SimulationCorePlayerRuleNoName, ClassName));
        }

        // Check included castes
        for (CasteInfo caste : castes) {
            caste.rulecheck(className);
        }
    }

     /// <summary>
    /// Delivers the list of castes.
    /// </summary>
    public ArrayList<CasteInfo> getCastes() {
        return castes;
    }

    /// <summary>
    /// Clones the whole object
    /// </summary>
    /// <returns>clone</returns>
    public Object clone() {
        return new PlayerInfo(this);
    }

    @Override
    public String toString()
    {
        
        if (!colonyName.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            sb.append(colonyName);
            if (!firstName.isEmpty() || !lastName.isEmpty())
            {
                sb.append(" (");
                if (!firstName.isEmpty())
                {
                    sb.append(firstName);
                    if (!lastName.isEmpty()))
                    {
                        sb.append(" ");
                        sb.append(lastName);
                    }
                }
                else sb.append(lastName);
                sb.append(")");
            }
            return sb.toString();
        }
        else
        {
            return className;
        }
    }

    public void setCastes(ArrayList<CasteInfo> castes) {
        this.castes = castes;
    }

    public Assembly getAssembly() {
        return assembly;
    }

    public void setAssembly(Assembly assembly) {
        this.assembly = assembly;
    }

    public boolean isHasDebugInformation() {
        return hasDebugInformation;
    }

    public void setHasDebugInformation(boolean hasDebugInformation) {
        this.hasDebugInformation = hasDebugInformation;
    }

    public UUID getGuid() {
        return guid;
    }

    public void setGuid(UUID guid) {
        this.guid = guid;
    }

    public String getColonyName() {
        return colonyName;
    }

    public void setColonyName(String colonyName) {
        this.colonyName = colonyName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isRequestDatabaseAccess() {
        return requestDatabaseAccess;
    }

    public void setRequestDatabaseAccess(boolean requestDatabaseAccess) {
        this.requestDatabaseAccess = requestDatabaseAccess;
    }

    public boolean isRequestFileAccess() {
        return requestFileAccess;
    }

    public void setRequestFileAccess(boolean requestFileAccess) {
        this.requestFileAccess = requestFileAccess;
    }

    public boolean isRequestNetworkAccess() {
        return requestNetworkAccess;
    }

    public void setRequestNetworkAccess(boolean requestNetworkAccess) {
        this.requestNetworkAccess = requestNetworkAccess;
    }

    public String getRequestInformation() {
        return requestInformation;
    }

    public void setRequestInformation(String requestInformation) {
        this.requestInformation = requestInformation;
    }

    public boolean isRequestReferences() {
        return requestReferences;
    }

    public void setRequestReferences(boolean requestReferences) {
        this.requestReferences = requestReferences;
    }

    public boolean isRequestUserInterfaceAccess() {
        return requestUserInterfaceAccess;
    }

    public void setRequestUserInterfaceAccess(boolean requestUserInterfaceAccess) {
        this.requestUserInterfaceAccess = requestUserInterfaceAccess;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public PlayerLanguages getLanguage() {
        return language;
    }

    public void setLanguage(PlayerLanguages language) {
        this.language = language;
    }

    public boolean isStaticTypes() {
        return staticTypes;
    }

    public void setStaticTypes(boolean staticTypes) {
        this.staticTypes = staticTypes;
    }

    public PlayerSimulationVersions getSimulationVersion() {
        return simulationVersion;
    }

    public void setSimulationVersion(PlayerSimulationVersions simulationVersion) {
        this.simulationVersion = simulationVersion;
    }

}
