package AntMe.SharedComponents.States;

public abstract class IndexBasedState implements Comparable<IndexBasedState> {
    /// <summary>
    /// Constructor of this state.
    /// </summary>
    /// <param name="id"></param>
    public IndexBasedState(int id) {
        this.id = id;
    }

    /// <summary>
    /// Gets the id of this state.
    /// </summary>
    private Integer id;

    /// <summary>
    /// Compares two IndexBasedStates
    /// </summary>
    /// <param name="other">other state</param>
    /// <returns>compare-result</returns>
    public int compareTo(IndexBasedState other)
    {
        return id.compareTo(other.getId());
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
