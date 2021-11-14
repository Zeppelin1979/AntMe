package AntMe.SharedComponents.States;

public class MarkerState extends ColonyBasedState {
    /// <summary>
    /// Constructor of marker-state.
    /// </summary>
    /// <param name="colonyId">Colony-id</param>
    /// <param name="id">id</param>
    public MarkerState(int colonyId, int id) {
    	super(colonyId, id);
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
    /// Gets or sets the radius.
    /// </summary>
    private int radius;

    /// <summary>
    /// Gets or sets the direction.
    /// </summary>
    private int direction;

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

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
}
