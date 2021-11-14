package AntMe.Gui;

public class CustomStateItem {
    private String name;
    private String type;
    private String description;

    /// <summary>
    /// Creates a new instance of custom state.
    /// </summary>
    /// <param name="name">Name of custom state</param>
    /// <param name="type">Type of custom state</param>
    /// <param name="description">Description of state-access</param>
    public CustomStateItem(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

}
