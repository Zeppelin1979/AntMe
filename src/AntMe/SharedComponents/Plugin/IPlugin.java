package AntMe.SharedComponents.Plugin;

import java.lang.Runtime.Version;
import java.util.UUID;

import AntMe.SharedComponents.States.SimulationState;
import javafx.scene.control.Control;

public abstract class IPlugin {
    /// <summary>
    /// Plugin-Description. Only called by UI-Thread.
    /// </summary>
    private String description;
    
    public String getDescription() {
    	return description;
    }

    /// <summary>
    /// Plugin-Guid. Only called by UI-Thread.
    /// </summary>
    private UUID guid;

    /// <summary>
    /// Plugin-Name. Only called by UI-Thread.
    /// </summary>
    private String name;

    /// <summary>
    /// Plugin-Version. Only called by UI-Thread.
    /// </summary>
    private Version version;

    /// <summary>
    /// Gets the current plugin-state.  Called by UI- and GameLoop-Thread.
    /// </summary>
    private PluginState state;

    /// <summary>
    /// Gets the plugin-user-control so show in main application or null, if there is no user-control. Only called by UI-Thread.
    /// </summary>
    private Control control;

    /// <summary>
    /// Gets or sets the settings for this plugin. usually a serialized configuration-class. Only called by UI-Thread.
    /// </summary>
    private byte[] settings;

    public byte[] getSettings() {
		return settings;
	}

	public void setSettings(byte[] settings) {
		this.settings = settings;
	}

	public UUID getGuid() {
		return guid;
	}

	public String getName() {
		return name;
	}

	public Version getVersion() {
		return version;
	}

	public PluginState getState() {
		return state;
	}

	public Control getControl() {
		return control;
	}

	/// <summary>
    /// Starts the plugin-activity or resumes activity, if paused. Only called by UI-Thread.
    /// </summary>
    public abstract void start();

    /// <summary>
    /// Stops the plugin-activity. Only called by UI-Thread.
    /// </summary>
    public abstract void stop();

    /// <summary>
    /// Pauses the activity or starts and pauses, if stopped. Only called by UI-Thread.
    /// </summary>
    public abstract void pause();

    /// <summary>
    /// Delivers the start-parameter from main application to this plugin. Only called by UI-Thread.
    /// </summary>
    /// <param name="parameter">start-parameter</param>
    public abstract void startupParameter(String[] parameter);

    /// <summary>
    /// Sets the state of visibility of plugins user-control. Only called by UI-Thread.
    /// </summary>
    /// <param name="visible">is user-control visible in main window</param>
    public abstract void setVisibility(boolean visible);

    /// <summary>
    /// Updates UI. Only called by UI-Thread.
    /// </summary>
    public abstract void updateUI(SimulationState state);

}
