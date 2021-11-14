package AntMe.SharedComponents.States;

import java.util.ArrayList;
import java.util.UUID;

public class ColonyState extends IndexBasedState {
    /// <summary>
    /// Constructor of colony-state
    /// </summary>
    /// <param name="id">id</param>
    public ColonyState(int id)
    {
    	super(id);
        antStates = new ArrayList<AntState>();
        anthillStates = new ArrayList<AnthillState>();
        markerStates = new ArrayList<MarkerState>();
        casteStates = new ArrayList<CasteState>();
    }

    /// <summary>
    /// Constructor of colony-state
    /// </summary>
    /// <param name="id">id of this colony</param>
    /// <param name="guid"><c>guid</c></param>
    /// <param name="colonyName">Name of this colony</param>
    /// <param name="playerName">Name of player</param>
    public ColonyState(int id, UUID guid, String colonyName, String playerName)
    {
    	super(id);
        this.guid = guid;
        this.colonyName = colonyName;
        this.playerName = playerName;
    }

    /// <summary>
    /// Gets a list of ants.
    /// </summary>
    private ArrayList<AntState> antStates;

    /// <summary>
    /// Gets a list of anthills.
    /// </summary>
    private ArrayList<AnthillState> anthillStates;

    /// <summary>
    /// Gets a list of markers.
    /// </summary>
    private ArrayList<MarkerState> markerStates;

    /// <summary>
    /// gets a list of castes.
    /// </summary>
    private ArrayList<CasteState> casteStates;

    /// <summary>
    /// Gets or sets the guid of the colony.
    /// </summary>
    private UUID guid;

    /// <summary>
    /// Gets or sets the name of this colony.
    /// </summary>
    private String colonyName;

    /// <summary>
    /// Gets or sets the name of the player.
    /// </summary>
    private String playerName;

    /// <summary>
    /// Gets or sets the count of starved ants.
    /// </summary>
    private int starvedAnts;

    /// <summary>
    /// Gets or sets the count of eaten ants.
    /// </summary>
    private int eatenAnts;

    /// <summary>
    /// Gets or sets the count of beaten ants.
    /// </summary>
    private int beatenAnts;

    /// <summary>
    /// Gets or sets the count of killed bugs.
    /// </summary>
    private int killedBugs;
    
    /// <summary>
    /// Gets or sets the count of killed enemies.
    /// </summary>
    private int killedEnemies;

    /// <summary>
    /// Gets or sets the amount of collected food.
    /// </summary>
    private int collectedFood;
 
    /// <summary>
    /// Gets or sets the amount of collected fruits.
    /// </summary>
    private int collectedFruits;

    /// <summary>
    /// Gets or sets the total points.
    /// </summary>
    private int points;

	public ArrayList<AntState> getAntStates() {
		return antStates;
	}

	public void setAntStates(ArrayList<AntState> antStates) {
		this.antStates = antStates;
	}

	public ArrayList<AnthillState> getAnthillStates() {
		return anthillStates;
	}

	public void setAnthillStates(ArrayList<AnthillState> anthillStates) {
		this.anthillStates = anthillStates;
	}

	public ArrayList<MarkerState> getMarkerStates() {
		return markerStates;
	}

	public void setMarkerStates(ArrayList<MarkerState> markerStates) {
		this.markerStates = markerStates;
	}

	public ArrayList<CasteState> getCasteStates() {
		return casteStates;
	}

	public void setCasteStates(ArrayList<CasteState> casteStates) {
		this.casteStates = casteStates;
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

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getStarvedAnts() {
		return starvedAnts;
	}

	public void setStarvedAnts(int starvedAnts) {
		this.starvedAnts = starvedAnts;
	}

	public int getEatenAnts() {
		return eatenAnts;
	}

	public void setEatenAnts(int eatenAnts) {
		this.eatenAnts = eatenAnts;
	}

	public int getBeatenAnts() {
		return beatenAnts;
	}

	public void setBeatenAnts(int beatenAnts) {
		this.beatenAnts = beatenAnts;
	}

	public int getKilledBugs() {
		return killedBugs;
	}

	public void setKilledBugs(int killedBugs) {
		this.killedBugs = killedBugs;
	}

	public int getKilledEnemies() {
		return killedEnemies;
	}

	public void setKilledEnemies(int killedEnemies) {
		this.killedEnemies = killedEnemies;
	}

	public int getCollectedFood() {
		return collectedFood;
	}

	public void setCollectedFood(int collectedFood) {
		this.collectedFood = collectedFood;
	}

	public int getCollectedFruits() {
		return collectedFruits;
	}

	public void setCollectedFruits(int collectedFruits) {
		this.collectedFruits = collectedFruits;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

}
