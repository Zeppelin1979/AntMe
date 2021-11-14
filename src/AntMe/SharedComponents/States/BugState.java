package AntMe.SharedComponents.States;

public class BugState extends IndexBasedState {

	/// <summary>
    /// Constructor of bugstate.
    /// </summary>
    /// <param name="id">id</param>
    public BugState(int id) {
    	super(id);
    }

    public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getVitality() {
		return vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
	}

	/// <summary>
    /// Gets or sets the x-part of the position.
    /// </summary>
    private int positionX;
    
    /// <summary>
    /// Gets or sets the y-part of the position.
    /// </summary>
    private int positionY;

    /// <summary>
    /// Gets or sets the direction.
    /// </summary>
    private int direction;

    /// <summary>
    /// Gets or sets the vitality.
    /// </summary>
    private int vitality;

}
