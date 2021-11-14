package AntMe.SharedComponents.Plugin;

public enum PluginState {
    /// <summary>
    /// Indicates a not startable state.
    /// </summary>
    NotReady,

    /// <summary>
    /// Shows the ability to start. Also pause is a possible command.
    /// </summary>
    Ready,

    /// <summary>
    /// Indicates a running plugin. Plugin can pause and stop.
    /// </summary>
    Running,

    /// <summary>
    /// Plugin is suspended. It can resume again via start or stop complete.
    /// </summary>
    Paused

}
