package AntMe.SharedComponents.States;

public class AntState extends ColonyBasedState {

    /// <summary>
    /// Constructor of ant-state
    /// </summary>
    /// <param name="colonyId">Colony-id</param>
    /// <param name="id">id</param>
    public AntState(int colonyId, int id)
    {
    	super(colonyId,id);
        loadType = LoadType.None;
        targetType = TargetType.None;
    }

    /// <summary>
    /// Gets or sets the id of the caste.
    /// </summary>
    private int casteId;

    /// <summary>
    /// Gets or sets the direction.
    /// </summary>
    private int direction;

    /// <summary>
    /// Gets or sets the load.
    /// </summary>
    private int load;

    /// <summary>
    /// Gets or sets the type of load.
    /// </summary>
    private LoadType loadType;

    /// <summary>
    /// Gets or sets the x-part of position.
    /// </summary>
    private int positionX;

    public int getCasteId() {
		return casteId;
	}

	public void setCasteId(int casteId) {
		this.casteId = casteId;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getLoad() {
		return load;
	}

	public void setLoad(int load) {
		this.load = load;
	}

	public LoadType getLoadType() {
		return loadType;
	}

	public void setLoadType(LoadType loadType) {
		this.loadType = loadType;
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public int getTargetPositionX() {
		return targetPositionX;
	}

	public void setTargetPositionX(int targetPositionX) {
		this.targetPositionX = targetPositionX;
	}

	public int getTargetPositionY() {
		return targetPositionY;
	}

	public void setTargetPositionY(int targetPositionY) {
		this.targetPositionY = targetPositionY;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public int getVitality() {
		return vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
	}

	public int getViewRange() {
		return viewRange;
	}

	public void setViewRange(int viewRange) {
		this.viewRange = viewRange;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	/// <summary>
    /// Gets or sets the kind of target.
    /// </summary>
	private TargetType targetType;

    /// <summary>
    /// Gets or sets the x-part of the target position.
    /// </summary>
    private int targetPositionX;

    /// <summary>
    /// Gets or sets the y-part of the target position.
    /// </summary>
    private int targetPositionY;

    /// <summary>
    /// Gets or sets the y-part of position.
    /// </summary>
    private int positionY;

    /// <summary>
    /// Gets or sets the vitality.
    /// </summary>
    private int vitality;

    /// <summary>
    /// View Range
    /// </summary>
    private int viewRange;

    /// <summary>
    /// Debug Message
    /// </summary>
    private String debugMessage;
}
