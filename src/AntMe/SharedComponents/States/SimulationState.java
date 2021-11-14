package AntMe.SharedComponents.States;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class SimulationState {

    /// <summary>
    /// Constructor to initialize the lists.
    /// </summary>
    public SimulationState()
    {
        timeStamp = new Date();
        bugStates = new ArrayList<BugState>();
        fruitStates = new ArrayList<FruitState>();
        teamStates = new ArrayList<TeamState>();
        sugarStates = new ArrayList<SugarState>();
        customFields = new CustomState();
    }

    /// <summary>
    /// Constructor to initialize the lists and set the basic parameters.
    /// </summary>
    /// <param name="width">width of the playground</param>
    /// <param name="height">height of the playground</param>
    /// <param name="round">the current round</param>
    /// <param name="rounds">the number of total rounds</param>
    public SimulationState(int width, int height, int round, int rounds)
    {
    	this();
    	playgroundWidth = width;
        playgroundHeight = height;
        currentRound = round;
        totalRounds = rounds;
    }

    /// <summary>
    /// Constructor to initialize the lists and set the basic parameters.
    /// </summary>
    /// <param name="width">width of the playground</param>
    /// <param name="height">height of the playground</param>
    /// <param name="round">the current round</param>
    /// <param name="rounds">the number of total rounds</param>
    /// <param name="time">the time-stamp of this simulation-state</param>
    public SimulationState(int width, int height, int round, int rounds, Date time)
    {
        this(width, height, round, rounds);
        timeStamp = time;
    }


    /// <summary>
    /// Gets a list of bugs.
    /// </summary>
    private ArrayList<BugState> bugStates; // { get; set; }

    /// <summary>
    /// Gets a list of fruits.
    /// </summary>
    private ArrayList<FruitState> fruitStates; // { get; set; }

	private ArrayList<ColonyState> colonyStates;
   
	public ArrayList<ColonyState> getColonyStates()
	{
	   ArrayList<ColonyState> colonies = new ArrayList<ColonyState>();
	   for (TeamState team: teamStates) {
		   	for (ColonyState colony: team.getColonyStates()) {
		   		colonies.add(colony);
		   	}
		   
	   }
       return colonies;
	}

    public ArrayList<BugState> getBugStates() {
		return bugStates;
	}

	public void setBugStates(ArrayList<BugState> bugStates) {
		this.bugStates = bugStates;
	}

	public ArrayList<FruitState> getFruitStates() {
		return fruitStates;
	}

	public void setFruitStates(ArrayList<FruitState> fruitStates) {
		this.fruitStates = fruitStates;
	}

	public ArrayList<TeamState> getTeamStates() {
		return teamStates;
	}

	public void setTeamStates(ArrayList<TeamState> teamStates) {
		this.teamStates = teamStates;
	}

	public ArrayList<SugarState> getSugarStates() {
		return sugarStates;
	}

	public void setSugarStates(ArrayList<SugarState> sugarStates) {
		this.sugarStates = sugarStates;
	}

	public CustomState getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomState customFields) {
		this.customFields = customFields;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getTotalRounds() {
		return totalRounds;
	}

	public void setTotalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	public int getPlaygroundWidth() {
		return playgroundWidth;
	}

	public void setPlaygroundWidth(int playgroundWidth) {
		this.playgroundWidth = playgroundWidth;
	}

	public int getPlaygroundHeight() {
		return playgroundHeight;
	}

	public void setPlaygroundHeight(int playgroundHeight) {
		this.playgroundHeight = playgroundHeight;
	}


	/// <summary>
    /// Gets a list of teams.
    /// </summary>
    private ArrayList<TeamState> teamStates; //{ get; set; }

    /// <summary>
    /// Gets a list of sugar.
    /// </summary>
    private ArrayList<SugarState> sugarStates;// { get; set; }

    /// <summary>
    /// Gets the list of custom fields.
    /// </summary>
    private CustomState customFields;// { get; set; }

    /// <summary>
    /// Gets or sets the time-stamp of this simulation-state.
    /// </summary>
    private Date timeStamp;// { get; set; }

    /// <summary>
    /// Gets or sets the number of total rounds.
    /// </summary>
    private int totalRounds;// { get; set; }

    /// <summary>
    /// Gets or sets the number of current round.
    /// </summary>
    private int currentRound;// { get; set; }

    /// <summary>
    /// Gets or sets the width of the playground.
    /// </summary>
    private int playgroundWidth;// { get; set; }

    /// <summary>
    /// Gets or sets the height of the playground.
    /// </summary>
    private int playgroundHeight;// { get; set; }

}
