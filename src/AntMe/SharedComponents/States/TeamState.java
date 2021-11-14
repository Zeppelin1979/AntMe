package AntMe.SharedComponents.States;

import java.util.ArrayList;
import java.util.UUID;

public class TeamState extends IndexBasedState {
    /// <summary>
    /// Constructor of team-state
    /// </summary>
    /// <param name="id">id</param>
    public TeamState(int id)
    {
    	super(id);
        colonyStates = new ArrayList<ColonyState>();
    }

    /// <summary>
    /// Constructor of team-state
    /// </summary>
    /// <param name="id">id of this team</param>
    /// <param name="guid"><c>guid</c></param>
    /// <param name="name">Name of this team</param>
    public TeamState(int id, UUID guid, String name)
    {
    	super(id);
        this.guid = guid;
        this.name = name;
    }


    /// <summary>
    /// gets a list of castes.
    /// </summary>
    private ArrayList<ColonyState> colonyStates;

    /// <summary>
    /// Gets or sets the <c>guid</c> of the team.
    /// </summary>
    private UUID guid;

    /// <summary>
    /// Gets or sets the name of the team.
    /// </summary>
    private String name;

	public ArrayList<ColonyState> getColonyStates() {
		return colonyStates;
	}

	public void setColonyStates(ArrayList<ColonyState> colonyStates) {
		this.colonyStates = colonyStates;
	}

	public UUID getGuid() {
		return guid;
	}

	public void setGuid(UUID guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
