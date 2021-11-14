package AntMe.Gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Stopwatch;

import AntMe.SharedComponents.Plugin.PluginState;
import AntMe.SharedComponents.States.SimulationState;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

public class PluginManager {

    private final int FRAMERATE_SPAN = 10;



    // Pluginlists
    private Map<UUID, PluginItem> activeConsumers;
    private Map<UUID, PluginItem> consumerList;
    private Map<UUID, PluginItem> producerList;
    private PluginItem activeProducer;

    private Configuration config;
    private PluginItem visiblePlugin;
    private ArrayList<Exception> exceptions;
    private PluginState lastState = PluginState.NotReady;
    private boolean ignoreStateupdate = false;

    private Thread requestThread;

    private boolean speedLimiter;
    private float frameLimit;
    private float frameLimitMs;
    private SimulationState lastSimulationState;

    private float[] frameRateHistory;
    private boolean frameRateInvalidate;
    private float frameRateAverage;
    private int frameRatePosition;
    private ReentrantLock lockActiveConsumers = new ReentrantLock();
    private ReentrantLock lockActiveProducers = new ReentrantLock();

 //   private SynchronizationContext context = new SynchronizationContext.Current();
    ResourceBundle bundle = ResourceBundle.getBundle("package.Resource", Locale.getDefault());

    AppDirs appDirs = AppDirsFactory.getInstance();
    
    private final String configPath =  appDirs.getUserConfigDir("AntMe", "1.0", "ml.ernstpaf");

    private File applicationPath;
    private File pluginPath;


    public PluginManager()
    {
        producerList = new HashMap<UUID, PluginItem>();
        consumerList = new HashMap<UUID, PluginItem>();
        activeConsumers = new HashMap<UUID, PluginItem>();
        exceptions = new ArrayList<Exception>();
        config = new Configuration();

        frameRateHistory = new float[FRAMERATE_SPAN];

        // Default Speed
        setSpeedLimit(true, 15.0f);

        try {
            applicationPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        pluginPath = new File(applicationPath, bundle.getString("PluginSearchFolder"));
    }


    /// <summary>
    /// Search in default folders for a new plugin.
    /// </summary>
    public void searchForPlugins()
    {
        // List dlls from Exe-Path
        ArrayList<File> files = new ArrayList<File>();
        files.addAll(Arrays.asList(applicationPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(bundle.getString("PluginSearchFilter"));
            }
        })));

        // List dlls from "plugin"-Folder
        if (pluginPath.exists()) files.addAll(Arrays.asList(pluginPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(bundle.getString("PluginSearchFilter"));
            }
        })));

        // Load root Plugins
        for (int i = 0; i < files.size(); i++)
            checkForPlugin(files.get(i));

        // Check known Plugins
        for (String knownFile: config.knownPluginFiles)
        {
            File external = new File(knownFile);

            // Skip some files
            if (!external.exists() ||
                files.contains(external) ||
                external.getParentFile().getAbsolutePath().equalsIgnoreCase(applicationPath.getAbsolutePath()) ||
                (pluginPath != null && external.getParentFile().getAbsolutePath().equalsIgnoreCase(pluginPath.getAbsolutePath())))
            {
                // Drop from list
                config.knownPluginFiles.remove(knownFile);
                continue;
            }

            // Try to load
            if (!checkForPlugin(external))
            {
                config.knownPluginFiles.remove(knownFile);
                continue;
            }
        }
    }

    /// <summary>
    /// Checks a single file for a new plugin.
    /// </summary>
    /// <param name="file">filename</param>
    /// <returns>true, if there are valid plugins inside</returns>
    public boolean checkForPlugin(File file)
    {
        try
        {
            URLClassLoader assembly = new URLClassLoader(new URL[] {file.toURI().toURL()}, this.getClass().getClassLoader());
            if (addPlugin(assembly))
            {
                if (
                    !file.getParentFile().getAbsolutePath().equalsIgnoreCase(applicationPath.getAbsolutePath())&& 
                    !file.getParentFile().getAbsolutePath().equalsIgnoreCase(pluginPath.getAbsolutePath()) &&
                    !config.knownPluginFiles.contains(file.getAbsolutePath().toLowerCase()))
                {
                    config.knownPluginFiles.add(file.getAbsolutePath().toLowerCase());
                }
                    
                return true;
            }
        }
/*        catch (TargetInvocationException ex)
        {
            // missing references
            exceptions.add(
                new Exception(
                    String.format(bundle.getString("PluginManagerMissingReferences"), file),
                    ex));
        }
        catch (ReflectionTypeLoadException ex)
        {
            // missing references
            exceptions.add(
                new Exception(
                    String.format(bundle.getString("PluginManagerMissingReferences"), file),
                    ex));
        }*/
        catch (Exception ex)
        {
            // unknown exception
            exceptions.add(ex);
        }
        return false;
    }

    /// <summary>
    /// search in given assembly for a new plugin
    /// </summary>
    /// <param name="assembly">assembly to search in</param>
    /// <returns>true, if there are valid plugins inside</returns>
    private boolean addPlugin(URLClassLoader assembly)
    {
        boolean hit = false;
/*
        ClassPath cp = ClassPath.from(assembly);
        
        // Get all includes Types
        for (ClassPath.ClassInfo type: cp.getTopLevelClassesRecursive("AntMe.SharedComponents.Plugin"))
//        foreach (Type type in assembly.GetExportedTypes())
        {
            // Find the attribute
            ArrayList<CustomStateItem> readCustomStates = new ArrayList<CustomStateItem>();
            ArrayList<CustomStateItem> writeCustomStates = new ArrayList<CustomStateItem>();

            for (Field field : type.getClass().getDeclaredFields()) {
                String name;
                String dataType;
                String description;
                if (field.getType() == ReadCustomStateAttribute.class) {
                    Attribute attribClass = fieldc# ;
                }
                switch (field.getType()) {
                    case ReadCustomStateAttribute.class:
                        name = "";
                        dataType = "";
                        description = "";
                        for (field.)
                }
            }

            foreach (CustomAttributeData customAttribute in CustomAttributeData.GetCustomAttributes(type))
            {
                string name;
                string dataType;
                string description;
                switch (customAttribute.Constructor.ReflectedType.FullName)
                {
                    case "AntMe.SharedComponents.Plugin.ReadCustomStateAttribute":
                        name = string.Empty;
                        dataType = string.Empty;
                        description = string.Empty;
                        foreach (CustomAttributeNamedArgument argument in customAttribute.NamedArguments)
                        {
                            switch (argument.MemberInfo.Name)
                            {
                                case "Name":
                                    name = (string)argument.TypedValue.Value;
                                    break;
                                case "Type":
                                    dataType = (string)argument.TypedValue.Value;
                                    break;
                                case "Description":
                                    description = (string)argument.TypedValue.Value;
                                    break;
                            }
                        }
                        readCustomStates.Add(new CustomStateItem(name, dataType, description));
                        break;
                    case "AntMe.SharedComponents.Plugin.WriteCustomStateAttribute":
                        name = string.Empty;
                        dataType = string.Empty;
                        description = string.Empty;
                        foreach (CustomAttributeNamedArgument argument in customAttribute.NamedArguments)
                        {
                            switch (argument.MemberInfo.Name)
                            {
                                case "Name":
                                    name = (string)argument.TypedValue.Value;
                                    break;
                                case "Type":
                                    dataType = (string)argument.TypedValue.Value;
                                    break;
                                case "Description":
                                    description = (string)argument.TypedValue.Value;
                                    break;
                            }
                        }
                        writeCustomStates.Add(new CustomStateItem(name, dataType, description));
                        break;
                }
            }

            // If type has an attribute, search for the interfaces
            foreach (Type plugin in type.GetInterfaces())
            {
                // Producer found
                if (plugin == typeof(IProducerPlugin))
                {
                    // Create an instance of plugin and add to list
                    PluginItem item = null;
                    try
                    {
                        IProducerPlugin producerPlugin =
                            (IProducerPlugin)Activator.CreateInstance(type, false);
                        item =
                            new PluginItem(producerPlugin, writeCustomStates.ToArray(), readCustomStates.ToArray());
                        hit = true;
                    }
                    catch (Exception ex)
                    {
                        exceptions.Add(
                            new Exception(
                                string.Format(
                                    bundle.getString("PluginManagerProducerPluginCommonProblems,
                                    type.FullName,
                                    assembly.GetFiles()[0].Name),
                                ex));
                    }

                    // Warnings, of there is another Version of that plugin
                    if (item != null && producerList.ContainsKey(item.Guid))
                    {
                        if (producerList[item.Guid].Version > item.Version)
                        {
                            exceptions.Add(
                                new Exception(
                                    string.Format(
                                        bundle.getString("PluginManagerProducerPluginNewerVersionLoaded,
                                        item.Name,
                                        item.Version)));
                            item = null;
                        }
                        else if (producerList[item.Guid].Version < item.Version)
                        {
                            exceptions.Add(
                                new Exception(
                                    string.Format(
                                        bundle.getString("PluginManagerProducerPluginNewerVersion,
                                        item.Name,
                                        item.Version)));
                            DeactivateProducer(item.Guid);
                            producerList.Remove(item.Guid);
                        }
                        else
                        {
                            // Samle plugin still loaded
                            item = null;
                        }
                    }

                    // add to list
                    if (item != null)
                    {
                        // Check, if plugin is preselected or saved as selected
                        producerList.Add(item.Guid, item);
                        if (config.selectedPlugins.Contains(item.Guid) ||
                            (!config.loaded &&
                             type.GetCustomAttributes(typeof(PreselectedAttribute), false).Length > 0))
                        {
                            ActivateProducer(item.Guid);
                        }

                        // Load Settings
                        if (File.Exists(configPath + item.Guid + Resources.PluginSettingsFileExtension))
                        {
                            try
                            {
                                item.Producer.Settings =
                                    File.ReadAllBytes(
                                        configPath + item.Guid + Resources.PluginSettingsFileExtension);
                            }
                            catch (Exception ex)
                            {
                                exceptions.Add(
                                    new Exception(
                                        string.Format(
                                            bundle.getString("PluginManagerProducerPluginSettingsLoadFailed,
                                            item.Name,
                                            item.Version),
                                        ex));
                            }
                        }
                    }
                }

                    // Consumer found
                else if (plugin == typeof(IConsumerPlugin))
                {
                    // Create an instance of plugin and add to list
                    PluginItem item = null;
                    try
                    {
                        IConsumerPlugin consumerPlugin =
                            (IConsumerPlugin)Activator.CreateInstance(type, false);
                        item =
                            new PluginItem(consumerPlugin, writeCustomStates.ToArray(), readCustomStates.ToArray());
                        hit = true;
                    }
                    catch (Exception ex)
                    {
                        exceptions.Add(
                            new Exception(
                                string.Format(
                                    bundle.getString("PluginManagerConsumerPluginCommonProblems,
                                    type.FullName,
                                    assembly.GetFiles()[0].Name),
                                ex));
                    }

                    // Warnings, of there is another Version of that plugin
                    if (item != null && consumerList.ContainsKey(item.Guid))
                    {
                        if (consumerList[item.Guid].Version > item.Version)
                        {
                            exceptions.Add(
                                new Exception(
                                    string.Format(
                                        bundle.getString("PluginManagerConsumerPluginNewerVersionLoaded,
                                        item.Name,
                                        item.Version)));
                            item = null;
                        }
                        else if (consumerList[item.Guid].Version < item.Version)
                        {
                            exceptions.Add(
                                new Exception(
                                    string.Format(
                                        bundle.getString("PluginManagerConsumerPluginNewerVersion,
                                        item.Name,
                                        item.Version)));
                            DeactivateConsumer(item.Guid);
                            consumerList.Remove(item.Guid);
                        }
                        else
                        {
                            // Same plugin still loaded
                            item = null;
                        }
                    }

                    // add to list
                    if (item != null)
                    {
                        consumerList.Add(item.Guid, item);

                        // Check, if plugin is preselected or saved as selected
                        if (config.selectedPlugins.Contains(item.Guid) ||
                            (!config.loaded &&
                             type.GetCustomAttributes(typeof(PreselectedAttribute), false).Length > 0))
                        {
                            ActivateConsumer(item.Guid);
                        }

                        // Load Settings
                        if (File.Exists(configPath + item.Guid + Resources.PluginSettingsFileExtension))
                        {
                            try
                            {
                                item.Consumer.Settings =
                                    File.ReadAllBytes(
                                        configPath + item.Guid + Resources.PluginSettingsFileExtension);
                            }
                            catch (Exception ex)
                            {
                                exceptions.Add(
                                    new Exception(
                                        string.Format(
                                            bundle.getString("PluginManagerConsumerPluginSettingsLoadFailed,
                                            item.Name,
                                            item.Version),
                                        ex));
                            }
                        }
                    }
                }
            }
        }*/
        return hit;
    }

    /// <summary>
    /// Gives a list of available producer-plugins.
    /// </summary>
    public PluginItem[] getProducerPlugins()
    {
            return (PluginItem[]) producerList.values().toArray();
    }

    /// <summary>
    /// Gives a list of available consumer-plugins.
    /// </summary>
    public PluginItem[] getConsumerPlugins()
    {
        return (PluginItem[]) consumerList.values().toArray();
    }

    /// <summary>
    /// Gives a list of activated consumer-plugins.
    /// </summary>
    public PluginItem[] getActiveConsumerPlugins()
    {
        return (PluginItem[]) activeConsumers.values().toArray();
    }

    /// <summary>
    /// Returns a list of exceptions that happened during the last call
    /// </summary>
    public ArrayList<Exception> getExceptions()
    {
        return exceptions;
    }

    /// <summary>
    /// Gives the active producer-plugin or null, if no plugin is active.
    /// </summary>
    public PluginItem getActiveProducerPlugin()
    {
        return activeProducer;
    }

    /// <summary>
    /// Is manager ready to start.
    /// </summary>
    public boolean canStart()
    {
        return (getState() == PluginState.Ready || getState() == PluginState.Paused);
    }

    /// <summary>
    /// Is manger ready for pause-mode.
    /// </summary>
    public boolean canPause()
    {
        return (getState() == PluginState.Running || getState() == PluginState.Ready);
    }

    /// <summary>
    /// Is manager still running and can stop.
    /// </summary>
    public boolean canStop()
    {
        return (getState() == PluginState.Running || getState() == PluginState.Paused);
    }

    /// <summary>
    /// Returns the current state of this manager.
    /// </summary>
    public PluginState getState()
    {
            if (!ignoreStateupdate)
            {
                PluginState output = PluginState.NotReady;

                // capture producerstate
                if (activeProducer != null)
                {
                    output = activeProducer.getProducer().getState();
                }

                // check for changes
                switch (output)
                {
                    case NotReady:
                    case Ready:
                        if (lastState == PluginState.Running ||
                            lastState == PluginState.Paused)
                        {
                            // Producer switched from running/paused to notReady or ready
                            // All running consumer have to stop
                            for (PluginItem item: activeConsumers.values())
                            {
                                if (item.getConsumer().getState() == PluginState.Running ||
                                    item.getConsumer().getState() == PluginState.Paused)
                                {
                                    item.getConsumer().stop();
                                }
                            }
                        }
                        break;
                    case Running:
                        if (lastState == PluginState.NotReady ||
                            lastState == PluginState.Ready ||
                            lastState == PluginState.Paused)
                        {
                            // Producer switched from somewhere to running
                            // All ready or paused consumer have to start
                            for (PluginItem item : activeConsumers.values())
                            {
                                if (item.getConsumer().getState() == PluginState.Paused ||
                                    item.getConsumer().getState() == PluginState.Ready)
                                {
                                    item.getConsumer().start();
                                }
                            }
                        }
                        break;
                    case Paused:
                        if (lastState == PluginState.Running ||
                            lastState == PluginState.Ready)
                        {
                            // Producer switched to pause-mode
                            // All ready or running consumer have to pause
                            for (PluginItem item : activeConsumers.values())
                            {
                                if (item.getConsumer().getState() == PluginState.Running ||
                                    item.getConsumer().getState() == PluginState.Ready)
                                {
                                    item.getConsumer().pause();
                                }
                            }
                        }
                        break;
                }

                // Start requestLoop, if needed
                if ((output == PluginState.Running || output == PluginState.Paused) && requestThread == null)
                {
                    requestThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                requestLoop();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }                            
                        }
                        
                    });
                    requestThread.setDaemon(true);
                    requestThread.setPriority(Thread.NORM_PRIORITY);
                    requestThread.start();
                }

                // return
                lastState = output;
                return output;
            }
            else
            {
                return lastState;
            }
    }

    /// <summary>
    /// Gives the number of current simulation-round.
    /// </summary>
    public int getCurrentRound()
    {
            if (lastSimulationState != null)
            {
                return lastSimulationState.getCurrentRound();
            }
            return 0;
    }

    /// <summary>
    /// Gives the number of total rounds for the current simulation.
    /// </summary>
    public int getTotalRounds()
    {
           if (lastSimulationState != null)
            {
                return Math.max(lastSimulationState.getCurrentRound(), lastSimulationState.getTotalRounds());
            }
            return 0;
    }

    /// <summary>
    /// Gives the current frame-rate for this simulation.
    /// </summary>
    public float getFrameRate()
    {
            // calculate new average
            if (frameRateInvalidate)
            {
                frameRateAverage = 0;
                for (int i = 0; i < FRAMERATE_SPAN; i++)
                {
                    frameRateAverage += frameRateHistory[i];
                }
                frameRateAverage /= FRAMERATE_SPAN;
                frameRateInvalidate = false;
            }

            // deliver
            return frameRateAverage;
    }

	private void setFrameRate(float value) {
        frameRateInvalidate = true;
        frameRateHistory[frameRatePosition++ % FRAMERATE_SPAN] = value;
	}

    /// <summary>
    /// Gives the current frame-rate-limit, if limiter is enabled.
    /// </summary>
    public float getFrameLimit()
    {
            if (isFrameLimiterEnabled())
            {
                return frameLimit;
            }
            return 0.0f;
    }

    /// <summary>
    /// Gives the state of the frame-rate-limiter.
    /// </summary>
    public boolean isFrameLimiterEnabled()
    {
        return speedLimiter;
    }

    /// <summary>
    /// Gives the current configuration.
    /// </summary>
    public Configuration getConfiguration()
    {
        return config;
    }

    /// <summary>
    /// drops a consumer from active list.
    /// </summary>
    /// <param name="guid"><c>guid</c> of plugin to drop</param>
    public void deactivateConsumer(UUID guid)
    {
        // Check, if plugin exists
        if (!consumerList.containsKey(guid))
        {
            throw new IllegalArgumentException(bundle.getString("PluginManagerDeactivateConsumerNotInList"));
        }

        // Drop from active list
        
        lockActiveConsumers.lock();
        try
        {
            if (activeConsumers.containsKey(guid))
            {
                PluginItem plugin = activeConsumers.get(guid);

                activeConsumers.remove(guid);
                config.selectedPlugins.remove(guid);

                // Stop, if still running
                if (plugin.getConsumer().getState() == PluginState.Running ||
                    plugin.getConsumer().getState() == PluginState.Paused)
                {
                    plugin.getConsumer().stop();
                }
            }
        }
        finally {
            lockActiveConsumers.unlock();
        }
    }

    /// <summary>
    /// Adds a plugin to active list
    /// </summary>
    /// <param name="guid"><c>guid</c> of new plugin</param>
    public void activateConsumer(UUID guid)
    {
        // Check, if plugin exists
        if (!consumerList.containsKey(guid))
        {
            throw new IllegalArgumentException(bundle.getString("PluginManagerActivateConsumerNotInList"));
        }

        // Add to list
        lockActiveConsumers.lock();
        try
        {
            if (!activeConsumers.containsKey(guid))
            {
                PluginItem plugin = consumerList.get(guid);

                // Activate, if simulation still running
                if (getState() == PluginState.Running)
                {
                    plugin.getConsumer().start();
                }
                else if (getState() == PluginState.Paused)
                {
                    plugin.getConsumer().pause();
                }

                activeConsumers.put(guid, plugin);

                // mark as selected in config
                if (!config.selectedPlugins.contains(guid))
                {
                    config.selectedPlugins.add(guid);
                }
            }
        }
        finally {
            lockActiveConsumers.unlock();
        }
    }

    /// <summary>
    /// Deactivates the given producer
    /// </summary>
    /// <param name="guid"><c>guid</c> of producer</param>
    public void deactivateProducer(UUID guid)
    {
        ignoreStateupdate = true;

        if (activeProducer != null && activeProducer.getGuid() == guid)
        {
            // unhook producer
            stop();
            activeProducer = null;
            config.selectedPlugins.remove(guid);
        }
        ignoreStateupdate = false;
    }

    /// <summary>
    /// Changes the active Producer
    /// </summary>
    /// <param name="guid"><c>guid</c> of new producer</param>
    public void activateProducer(UUID guid)
    {
        ignoreStateupdate = true;
        // check, if plugin with that guid exists
        if (!producerList.containsKey(guid))
        {
            throw new IllegalArgumentException(bundle.getString("PluginManagerActivateProducerNotInList"));
        }

        // check, if same plugin is still active
        if (activeProducer == null || activeProducer.getGuid() != guid)
        {
            // unhook old producer 
            if (activeProducer != null)
            {
                deactivateProducer(activeProducer.getGuid());
            }

            // hook the new one
            activeProducer = producerList.get(guid);

            if (!config.selectedPlugins.contains(guid))
            {
                config.selectedPlugins.add(guid);
            }
        }
        ignoreStateupdate = false;
    }

    /// <summary>
    /// Starts the manager, of its ready
    /// </summary>
    public void start()
    {
        ignoreStateupdate = true;

        // Start the producer
        lockActiveProducers.lock();
        try
        {
            if (activeProducer != null &&
                (activeProducer.getProducer().getState() == PluginState.Ready ||
                 activeProducer.getProducer().getState() == PluginState.Paused))
            {
                activeProducer.getProducer().start();
            }
        }
        finally {
            lockActiveProducers.unlock();
        }

        ignoreStateupdate = false;
    }

    /// <summary>
    /// pause the manager
    /// </summary>
    public void pause()
    {
        ignoreStateupdate = true;

        // Suspend the producer
        lockActiveProducers.lock();
        try
        {
            if (activeProducer != null &&
                (activeProducer.getProducer().getState() == PluginState.Running ||
                 activeProducer.getProducer().getState() == PluginState.Ready))
            {
                activeProducer.getProducer().pause();
            }
        }
        finally {
            lockActiveProducers.unlock();
        }

        ignoreStateupdate = false;
    }

    /// <summary>
    ///  Stops the manager
    /// </summary>
    public void stop()
    {
        ignoreStateupdate = true;

        lockActiveProducers.lock();
        try
        {
            if (activeProducer != null &&
                (activeProducer.getProducer().getState() == PluginState.Running ||
                 activeProducer.getProducer().getState() == PluginState.Paused))
            {
                activeProducer.getProducer().stop();
            }
        }
        finally {
            lockActiveProducers.unlock();
        }

        ignoreStateupdate = false;
    }

    /// <summary>
    /// Loads the settings to configuration
    /// </summary>
    public void loadSettings() throws Exception
    {
        ignoreStateupdate = true;
        // check, if configfile exists
        if (new File(configPath + bundle.getString("GlobalSettingsFileName")).exists())
        {
            // read configfile
            ObjectInputStream in = null;
 
            try
            {
                in = new ObjectInputStream(new FileInputStream(configPath + bundle.getString("GlobalSettingsFileName")));
                config = (Configuration) in.readObject();
                config.loaded = true;
                setSpeedLimit(config.speedLimitEnabled, config.speedLimit);
            }
            catch (Exception ex)
            {
                throw new Exception(bundle.getString("PluginManagerSettingsLoadFailed"), ex);
            }
            finally
            {
                if(in != null) in.close();
            }
        }
        ignoreStateupdate = false;
    }

    /// <summary>
    /// Saves the settings of plugin-manager to configuration-file.
    /// </summary>
    public void saveSettings()
    {
        ignoreStateupdate = true;
        if (!new File(configPath).exists())
        {
            new File(configPath).mkdir();
        }

        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream(configPath + bundle.getString("GlobalSettingsFileName")));
            out.writeObject(config);
            ignoreStateupdate = false;
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // Save also plugin-Settings
        // Producer-Stuff
        for (PluginItem plugin : producerList.values())
        {
            try
            {
                byte[] temp = plugin.getProducer().getSettings();
                if (temp != null && temp.length > 0) {
                    FileOutputStream pluginout = new FileOutputStream(configPath + plugin.getGuid() + bundle.getString("PluginSettingsFileExtension"));
                    pluginout.write(temp);
                    pluginout.close();
                }
            }
            catch (Exception ex)
            {
                exceptions.add(
                    new Exception(
                        String.format(
                            bundle.getString("PluginManagerProducerPluginSettingsSaveFailed"), plugin.getName(), plugin.getVersion()),
                        ex));
            }
        }

        // Consumer-Stuff
        for (PluginItem plugin : consumerList.values())
        {
            try
            {
                byte[] temp = plugin.getConsumer().getSettings();
                if (temp != null && temp.length > 0) {
                    FileOutputStream pluginout = new FileOutputStream(configPath + plugin.getGuid() + bundle.getString("PluginSettingsFileExtension"));
                    pluginout.write(temp);
                    pluginout.close();
               }
            }
            catch (Exception ex)
            {
                exceptions.add(
                    new Exception(
                        String.format(
                            bundle.getString("PluginManagerConsumerPluginSettingsSaveFailed"), plugin.getName(), plugin.getVersion()),
                        ex));
            }
        }
    }

    /// <summary>
    /// Sets the current visible plugin
    /// </summary>
    /// <param name="guid">visible Plugin</param>
    public void setVisiblePlugin(UUID guid)
    {
        ignoreStateupdate = true;
        // Set old plugin to invisible
        if (visiblePlugin != null)
        {
            if (visiblePlugin.isConsumer())
            {
                try
                {
                    visiblePlugin.getConsumer().setVisibility(false);
                }
                catch (Exception ex)
                {
                    exceptions.add(
                        new Exception(
                            String.format(
                                bundle.getString("PluginManagerProducerVisibilitySetFailed"),
                                visiblePlugin.getName(),
                                visiblePlugin.getVersion()),
                            ex));
                }
            }
            else
            {
                try
                {
                    visiblePlugin.getProducer().setVisibility(false);
                }
                catch (Exception ex)
                {
                    exceptions.add(
                        new Exception(
                            String.format(
                                bundle.getString("PluginManagerConsumerVisibilitySetFailed"),
                                visiblePlugin.getName(),
                                visiblePlugin.getVersion()),
                            ex));
                }
            }
        }

        // Set new plugin to visible
        if (producerList.containsKey(guid))
        {
            visiblePlugin = producerList.get(guid);
            try
            {
                visiblePlugin.getProducer().setVisibility(true);
            }
            catch (Exception ex)
            {
                exceptions.add(
                    new Exception(
                        String.format(
                            bundle.getString("PluginManagerProducerVisibilitySetFailed"),
                            visiblePlugin.getName(),
                            visiblePlugin.getVersion()),
                        ex));
            }
        }
        else if (consumerList.containsKey(guid))
        {
            visiblePlugin = consumerList.get(guid);
            try
            {
                visiblePlugin.getConsumer().setVisibility(true);
            }
            catch (Exception ex)
            {
                exceptions.add(
                    new Exception(
                        String.format(
                            bundle.getString("PluginManagerConsumerVisibilitySetFailed"),
                            visiblePlugin.getName(),
                            visiblePlugin.getVersion()),
                        ex));
            }
        }
        else
        {
            visiblePlugin = null;
        }
        ignoreStateupdate = false;
    }

    /// <summary>
    /// Sets the speed-limitation for running simulations.
    /// </summary>
    /// <param name="enabled">sets the limitation to enabled</param>
    /// <param name="framesPerSecond">limits the speed to a specific frame-rate</param>
    public void setSpeedLimit(boolean enabled, float framesPerSecond)
    {
        if (enabled)
        {
            // Check for supported value
            if (framesPerSecond <= 0.0f)
            {
                throw new IllegalArgumentException(
                    "framesPerSecond " + framesPerSecond + " " + bundle.getString("PluginManagerFrameRateTooLow"));
            }

            frameLimit = framesPerSecond;
            frameLimitMs = 1000 / framesPerSecond;
            speedLimiter = true;

            config.speedLimit = frameLimit;
            config.speedLimitEnabled = true;
        }
        else
        {
            frameLimit = 0.0f;
            frameLimitMs = 0.0f;
            speedLimiter = false;

            config.speedLimit = 0.0f;
            config.speedLimitEnabled = false;
        }
    }

    /// <summary>
    /// The game-loop. Runs, until state is set to Ready or notReady
    /// </summary>
    private void requestLoop() throws InterruptedException
    {
        // Limiter- and frame-rate-handling
        Stopwatch watch = Stopwatch.createStarted();

        // Interrupt-Handling
        boolean interrupt = false;

        // Mainloop
        while (activeProducer != null &&
               (activeProducer.getProducer().getState() == PluginState.Running ||
                activeProducer.getProducer().getState() == PluginState.Paused))
        {

            // request Simulationstate, if loop is not paused
            if (activeProducer != null && activeProducer.getProducer().getState() == PluginState.Running)
            {
                watch.reset();
                watch.start();

                // Create new Simulationstate
                SimulationState simulationState = new SimulationState();

                // Request all consumers with CreateState-Method
                lockActiveConsumers.lock();
                try
                {
                    for (PluginItem item:activeConsumers.values())
                    {
                        try
                        {
                            item.lock();
                            try
                            {
                                if (item.getConsumer().getState() == PluginState.Running)
                                {
                                    item.getConsumer().createState(simulationState);
                                }
                            }
                            finally
                            {
                                item.unlock();
                            }
                        }
                        catch (Exception ex)
                        {
                            exceptions.add(
                                new Exception(
                                    String.format(
                                        bundle.getString("PluginManagerLoopConsumer1Failed"), item.getName(), item.getVersion()),
                                    ex));
                            interrupt = true;
                            break;
                        }
                    }
                }
                finally {
                    lockActiveConsumers.unlock();
                }

                // Break, if there was an interrupt
                if (interrupt)
                {
                    break;
                }

                // Request producers Simulationstate
                lockActiveProducers.lock();
                try
                {
                    try
                    {
                        if (activeProducer != null && activeProducer.getProducer().getState() == PluginState.Running)
                        {
                            activeProducer.getProducer().createState(simulationState);
                        }
                    }
                    catch (Exception ex)
                    {
                        exceptions.add(
                            new Exception(
                                String.format(
                                    bundle.getString("PluginManagerLoopProducerFailed"),
                                    activeProducer.getName(),
                                    activeProducer.getVersion()),
                                ex));
                        interrupt = true;
                        break;
                    }
                }
                finally {
                    lockActiveProducers.unlock();
                }

                // Request all consumers with CreatingState-Method
                lockActiveConsumers.lock();
                try
                {
                    for (PluginItem item : activeConsumers.values())
                    {
                        try
                        {
                            item.lock();
                            try
                            {
                                if (item.getConsumer().getState() == PluginState.Running)
                                {
                                    item.getConsumer().creatingState(simulationState);
                                }
                            }
                            finally
                            {
                                item.unlock();
                            }
                        }
                        catch (Exception ex)
                        {
                            exceptions.add(
                                new Exception(
                                    String.format(
                                        bundle.getString("PluginManagerLoopConsumer2Failed"), item.getName(), item.getVersion()),
                                    ex));
                            interrupt = true;
                            break;
                        }
                    }
                }
                finally {
                    lockActiveConsumers.unlock();
                }

                // On interrupt stop loop
                if (interrupt)
                {
                    break;
                }

                // Request all consumers with CreatedState-Method and also check for interrupt-Request
                lockActiveConsumers.lock();
                try
                {
                    for (PluginItem item : activeConsumers.values())
                    {
                        try
                        {
                            item.lock();
                            try
                            {
                                if (item.getConsumer().getState() == PluginState.Running)
                                {
                                    item.getConsumer().createdState(simulationState);
                                }
                            }
                            finally {
                                item.unlock();
                            }
                        }
                        catch (Exception ex)
                        {
                            exceptions.add(
                                new Exception(
                                    String.format(
                                        bundle.getString("PluginManagerLoopConsumer3Failed"), item.getName(), item.getVersion()),
                                    ex));
                            interrupt = true;
                            break;
                        }
                    }
                }
                finally {
                    lockActiveConsumers.unlock();
                }

                // On interrupt stop loop
                if (interrupt)
                {
                    break;
                }

                // Update UI
                try
                {
                    if (activeProducer != null && activeProducer.getProducer().getState() == PluginState.Running)
                    {
                        activeProducer.getProducer().updateUI(simulationState); //synchronized(this)
                    }
                }
                catch (Exception ex)
                {
                    exceptions.add(
                        new Exception(
                            String.format(
                                bundle.getString("PluginManagerLoopProducerUIFailed"),
                                activeProducer.getName(),
                                activeProducer.getVersion()),
                            ex));
                    interrupt = true;
                    break;
                }

                for (PluginItem item : activeConsumers.values())
                {
                    try
                    {
                        if (item.getConsumer().getState() == PluginState.Running)
                        {
                            item.getConsumer().updateUI(simulationState); //synchronized(this)
                            interrupt |= item.getConsumer().getInterrupt();
                        }
                    }
                    catch (Exception ex)
                    {
                        exceptions.add(
                            new Exception(
                                String.format(
                                    bundle.getString("PluginManagerLoopConsumerUIFailed"), item.getName(), item.getVersion()),
                                ex));
                        interrupt = true;
                        break;
                    }
                }

                // On interrupt stop loop
                if (interrupt)
                {
                    break;
                }

                // Save state for statistics
                lastSimulationState = simulationState;

                // Framelimiter
                if (isFrameLimiterEnabled())
                {
                    while (watch.elapsed(TimeUnit.MILLISECONDS) < frameLimitMs)
                    {
                        Thread.sleep(1);
                    }
                }

                // calculation of frame-time
                setFrameRate(watch.elapsed(TimeUnit.MILLISECONDS) > 0 ? 1000 / watch.elapsed(TimeUnit.MILLISECONDS) : 0);
            }
            else
            {
                Thread.sleep(20);
            }
        }

        // Interrupt
        if (interrupt)
        {
            try
            {
                activeProducer.getProducer().stop(); // syncronized(this)
            }
            catch (Exception ex)
            {
                exceptions.add(
                    new Exception(
                        String.format(
                            bundle.getString("PluginManagerLoopInterruptFailed"),
                            activeProducer.getName(),
                            activeProducer.getVersion()),
                        ex));
            }
        }

        requestThread = null;

        watch.reset();
    }
}
