package AntMe.SharedComponents.States;

public class CasteState extends ColonyBasedState {

    /// <summary>
    /// Constructor of caste-state
    /// </summary>
    /// <param name="colonyId">Id of colony</paraparam>
    /// <param name="id">Id of caste</param>
    public CasteState(int colonyId, int id) {
    	super(colonyId, id);
    }


    /// <summary>
    /// Gets or sets the attack-modificator.
    /// </summary>
    private byte attackModificator;

    /// <summary>
    /// Gets or sets the load-modificator.
    /// </summary>
    private byte loadModificator;

    /// <summary>
    /// Gets or sets the name of this caste.
    /// </summary>
    private String name;

    /// <summary>
    /// Gets or sets the range-modificator.
    /// </summary>
    private byte rangeModificator;

    /// <summary>
    /// Gets or sets the rotation-speed-modificator.
    /// </summary>
    private byte rotationSpeedModificator;

    /// <summary>
    /// Gets or sets the speed-modificator.
    /// </summary>
    private byte speedModificator;

    /// <summary>
    /// Gets or sets the view-range-modificator.
    /// </summary>
    private byte viewRangeModificator;

    /// <summary>
    /// Gets or sets the vitality-modificator.
    /// </summary>
    private byte vitalityModificator;

	public byte getAttackModificator() {
		return attackModificator;
	}

	public void setAttackModificator(byte attackModificator) {
		this.attackModificator = attackModificator;
	}

	public byte getLoadModificator() {
		return loadModificator;
	}

	public void setLoadModificator(byte loadModificator) {
		this.loadModificator = loadModificator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getRangeModificator() {
		return rangeModificator;
	}

	public void setRangeModificator(byte rangeModificator) {
		this.rangeModificator = rangeModificator;
	}

	public byte getRotationSpeedModificator() {
		return rotationSpeedModificator;
	}

	public void setRotationSpeedModificator(byte rotationSpeedModificator) {
		this.rotationSpeedModificator = rotationSpeedModificator;
	}

	public byte getSpeedModificator() {
		return speedModificator;
	}

	public void setSpeedModificator(byte speedModificator) {
		this.speedModificator = speedModificator;
	}

	public byte getViewRangeModificator() {
		return viewRangeModificator;
	}

	public void setViewRangeModificator(byte viewRangeModificator) {
		this.viewRangeModificator = viewRangeModificator;
	}

	public byte getVitalityModificator() {
		return vitalityModificator;
	}

	public void setVitalityModificator(byte vitalityModificator) {
		this.vitalityModificator = vitalityModificator;
	}
}
