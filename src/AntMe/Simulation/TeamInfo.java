package AntMe.Simulation;
/// <summary>
/// Repräsentiert ein Team innerhalb der Simulationskonfiguration
/// </summary>

import java.util.ArrayList;
import java.util.UUID;

public class TeamInfo implements Cloneable{
    /// <summary>
    /// Guid des Teams
    /// </summary>
    private UUID guid;
    public UUID getGuid()
    {
        return guid;
    }

    /// <summary>
    /// Name des Teams
    /// </summary>
    private String name;
    public String getName() {
        return name;
    }

    /// <summary>
    /// Liste der enthaltenen Spieler
    /// </summary>
    private ArrayList<PlayerInfo> player;

    /// <summary>
    /// Konstruktor des Teams
    /// </summary>
    public TeamInfo()
    {
        guid = UUID.randomUUID();
        player = new ArrayList<PlayerInfo>();
    }

    /// <summary>
    /// Konstruktor des Teams
    /// </summary>
    /// <param name="player">Liste der Spieler</param>
    public TeamInfo(ArrayList<PlayerInfo> player)
    {
        this();
        this.player = player;
    }

    /// <summary>
    /// Konstruktor des Teams
    /// </summary>
    /// <param name="guid">Guid des Teams</param>
    /// <param name="player">Liste der Spieler</param>
    public TeamInfo(UUID guid, ArrayList<PlayerInfo> player)
    {
        this(player);
        this.guid = guid;
    }

    /// <summary>
    /// Konstruktor des Teams
    /// </summary>
    /// <param name="name">Name des Teams</param>
    /// <param name="player">Liste der Spieler</param>
    public TeamInfo(String name, ArrayList<PlayerInfo> player)
    {
        this(player);
        this.name = name;
    }

    /// <summary>
    /// Konstruktor des Teams
    /// </summary>
    /// <param name="guid">Guid des Teams</param>
    /// <param name="name">Name des Teams</param>
    /// <param name="player">Liste der Spieler</param>
    public TeamInfo(UUID guid, String name, ArrayList<PlayerInfo> player)
   {
        this(player);
        this.guid = guid;
        this.name = name;
    }

    /// <summary>
    /// Liste der spieler dieses Teams
    /// </summary>
    public ArrayList<PlayerInfo> getPlayer()
    {
        return player;
    }

    /// <summary>
    /// Prüft, ob das Team regelkonform aufgebaut ist
    /// </summary>
    public void rulecheck()
    {
        // Menge der Spieler prüfen
        if (player == null || player.size() < 1)
        {
            // TODO: Name der Resource ist Mist
            throw new InvalidOperationException(Resource.SimulationCoreTeamInfoNoName);
        }

        // Regelcheck bei den enthaltenen Spielern
        for (PlayerInfo info : player)
        {
            info.ruleCheck();
        }
    }

    /// <summary>
    /// Erstellt eine Kopie des Teams
    /// </summary>
    /// <returns>Kopie des Teams</returns>
    public Object clone()
    {
        TeamInfo output = new TeamInfo();
        output.guid = guid;
        output.name = name;
        output.player = new ArrayList<PlayerInfo>(player.size());
        for (PlayerInfo info : player)
        {
            output.player.add((PlayerInfo)info.clone());
        }
        return output;
    }
}
