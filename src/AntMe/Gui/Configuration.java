package AntMe.Gui;

import java.util.ArrayList;
import java.util.UUID;
import com.google.gson.annotations.*;

public class Configuration {
        /// <summary>
        /// List of known files, that containing plugins.
        /// </summary>
        @Expose
        public ArrayList<String> knownPluginFiles = new ArrayList<String>();

        /// <summary>
        /// Lists all active plugins.
        /// </summary>
        @Expose
        public ArrayList<UUID> selectedPlugins = new ArrayList<UUID>();

        /// <summary>
        /// Gives the limit for frame-rate.
        /// </summary>
        @Expose
        public float speedLimit;

        /// <summary>
        /// Gives the state of frame-rate limiter.
        /// </summary>
        @Expose
        public boolean speedLimitEnabled;

        /// <summary>
        /// Indicated, if the configuration was loaded.
        /// </summary>
        public boolean loaded;

}
