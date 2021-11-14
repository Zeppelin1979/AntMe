package AntMe.SharedComponents.States;

public class FruitState extends IndexBasedState {
    /// <summary>
    /// Constructor of fruit-state.
    /// </summary>
    /// <param name="id">id</param>
    public FruitState(int id) {
    	super(id);
    }

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

	public byte getCarryingAnts() {
		return carryingAnts;
	}

	public void setCarryingAnts(byte carryingAnts) {
		this.carryingAnts = carryingAnts;
	}

	/// <summary>
    /// Gets or sets the amount of fruit.
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

    /// <summary>
    /// Gets or sets the number of carrying ants.
    /// </summary>
    private byte carryingAnts;

}
