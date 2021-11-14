package AntMe.SharedComponents.States;

public class SugarState extends IndexBasedState {
    /// <summary>
    /// Constructor of sugar-state
    /// </summary>
    /// <param name="id">id</param>
    public SugarState(int id) {
    	super(id);
    }

    /// <summary>
    /// Gets or sets the load of sugar.
    /// </summary>
    private int amount;

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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
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

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

}
