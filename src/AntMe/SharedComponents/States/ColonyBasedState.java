package AntMe.SharedComponents.States;

public abstract class ColonyBasedState extends IndexBasedState {
    /// <summary>
    /// Constructor of this state.
    /// </summary>
    /// <param name="colonyId">colony-id</param>
    /// <param name="id">id</param>
    public ColonyBasedState(int colonyId, int id)
    {
    	super(id);
        this.setColonyId(colonyId);
    }

    public int getColonyId() {
		return colonyId;
	}

	public void setColonyId(int colonyId) {
		this.colonyId = colonyId;
	}

	/// <summary>
    /// Gets the colony-id of this state.
    /// </summary>
    private int colonyId;

}
