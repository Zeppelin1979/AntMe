package AntMe.Gui;

import java.lang.Runtime.Version;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import AntMe.SharedComponents.Plugin.IConsumerPlugin;
import AntMe.SharedComponents.Plugin.IPlugin;
import AntMe.SharedComponents.Plugin.IProducerPlugin;

public class PluginItem {
	private IConsumerPlugin consumer;
	private IProducerPlugin producer;
	private String name;
	private String description;
	private java.lang.Runtime.Version version;
	private UUID guid;
	private CustomStateItem[] writeCustomStates;
	private CustomStateItem[] readCustomStates;
    private ReentrantLock lock;


    /// <summary>
    /// Creates an instance of Plugin-Item, based on a producer.
    /// </summary>
    /// <param name="plugin">Producer-Plugin</param>
    /// <param name="writeCustomStates">List of custom states for write-access</param>
    /// <param name="readCustomStates">List of custom states for read-access</param>
    public PluginItem(IProducerPlugin plugin, CustomStateItem[] writeCustomStates, CustomStateItem[] readCustomStates) {
    	this(writeCustomStates, readCustomStates, plugin);
        // Check for null
        if (plugin == null) {
        	ResourceBundle bundle = ResourceBundle.getBundle("package.Resource", Locale.getDefault());
            throw new IllegalArgumentException(bundle.getString("PluginItemConstructorPluginIsNull"));
        }

        producer = plugin;
    }

    /// <summary>
    /// Creates an instance of Plugin-Item, based on a producer.
    /// </summary>
    /// <param name="plugin">Consumer-Plugin</param>
    /// <param name="writeCustomStates">List of custom states for write-access</param>
    /// <param name="readCustomStates">List of custom states for read-access</param>
    public PluginItem(IConsumerPlugin plugin, CustomStateItem[] writeCustomStates, CustomStateItem[] readCustomStates) {
    	this(writeCustomStates, readCustomStates, plugin);
    	// Prüfen, ob Plugin angegeben wurde
        if (plugin == null) {
        	ResourceBundle bundle = ResourceBundle.getBundle("package.Resource", Locale.getDefault());
            throw new IllegalArgumentException(bundle.getString("PluginItemConstructorPluginIsNull"));
        }

        consumer = plugin;
    }

    /// <summary>
    /// Private constructor for a common way to handle attributes.
    /// </summary>
    /// <param name="plugin">Plugin</param>
    /// <param name="writeCustomStates">List of custom states for write-access</param>
    /// <param name="readCustomStates">List of custom states for read-access</param>
    private PluginItem(CustomStateItem[] writeCustomStates, CustomStateItem[] readCustomStates, IPlugin plugin)
    {
        
    	ResourceBundle bundle = ResourceBundle.getBundle("package.Resource", Locale.getDefault());
        // Check for null
        if (plugin == null) {
            throw new IllegalArgumentException(bundle.getString("PluginItemConstructorPluginIsNull"));
        }

        // Check for valid name
        if (plugin.getName() .isEmpty()) {
            throw new IllegalArgumentException(bundle.getString("PluginItemConstructorPluginHasNoName"));
        }

        name = plugin.getName();
        description = plugin.getDescription();
        guid = plugin.getGuid();
        version = plugin.getVersion();

        // Custom states
        this.writeCustomStates = writeCustomStates;
        if (this.writeCustomStates == null) {
            this.writeCustomStates = new CustomStateItem[0]; 
        }
        this.readCustomStates = readCustomStates;
        if (this.readCustomStates == null) {
            this.readCustomStates = new CustomStateItem[0];
        }
    }

    /// <summary>
    /// Gets the consumer-plugin or null, if its a producer-plugin.
    /// </summary>
    public IConsumerPlugin getConsumer() {
    	return consumer;
    }

    /// <summary>
    /// Gets the producer-plugin or null, if its a consumer-plugin.
    /// </summary>
    public IProducerPlugin getProducer() {
        return producer;
    }

    /// <summary>
    /// True, if its a consumer-plugin, false in case of a producer-plugin.
    /// </summary>
    public boolean isConsumer() {
        return consumer != null;
    }

    /// <summary>
    /// Gets the name of the Plugin.
    /// </summary>
    public String getName() {
        return name;
    }

    /// <summary>
    /// Gets a short description of this Plugin.
    /// </summary>
    public String getDescription() {
        return description;
    }

    /// <summary>
    /// Gets the plugin-version.
    /// </summary>
    public Version getVersion() {
        return version;
    }

    /// <summary>
    /// Gets the plugin-<see cref="guid"/>.
    /// </summary>
    public UUID getGuid() {
        return guid;
    }

    /// <summary>
    /// Compares two instances of <see cref="PluginItem"/>.
    /// </summary>
    /// <param name="obj">other instance of <see cref="PluginItem"/></param>
    /// <returns>true, if equal</returns>
    @Override
    public boolean equals(Object obj) {

        // Check for right datatype
        if (!(obj.getClass() == PluginItem.class)) {
            return false;
        }

        PluginItem other = (PluginItem) obj;

        // compare guid
        if (other.guid != guid) {
            return false;
        }

        // compare version
        if (other.version != version) {
            return false;
        }

        // seams to be equal
        return true;
    }

    /// <summary>
    /// Generates a hash for this instance.
    /// </summary>
    /// <returns></returns>
    @Override
    public int hashCode() {
        return guid.hashCode();
    }

    /// <summary>
    /// Gives the name of this plugin.
    /// </summary>
    /// <returns>Name of plugin</returns>
	@Override
    public String toString() {
		return name;
	}

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
