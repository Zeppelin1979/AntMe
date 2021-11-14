package AntMe.PlayerManagement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import AntMe.Simulation.PlayerInfo;
import AntMe.Simulation.PlayerInfoFilename;
import javafx.print.Collation;

public final class PlayerStore {
    private static PlayerStore instance;
    private ReentrantLock lock = new ReentrantLock();

    public static PlayerStore getInstance()
    {
        if (instance == null) instance = new PlayerStore();
        return instance;
    }

    private boolean init = false;
    private String path = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + "/AntMe/Player17.conf";
    private PlayerStoreConfiguration configuration = new PlayerStoreConfiguration();
    private ArrayList<String> scannedFiles = new ArrayList<String>();
    private ArrayList<PlayerInfoFilename> knownPlayer = new ArrayList<PlayerInfoFilename>();

    private PlayerStore()
    {
    }

    private void init()
    {
        lock.lock();
        try
        {
            if (init) return;

            // Load Config
            loadConfiguration();

            // Load local files
            DirectoryInfo root = new FileInfo(Assembly.GetExecutingAssembly().Location).Directory;
            for (File file : root.GetFiles("*.dll", SearchOption.TopDirectoryOnly))
            {
                try
                {
                    scanFile(file.getAbsolutePath());
                }
                catch (Exception ex) { }
            }

            // Load Files from Config
            for (String file : configuration.getKnownFiles())
            {
                try
                {
                    scanFile(file);
                }
                catch (Exception ex)
                {
                    unregisterFile(file);
                }
            }

            init = true;
        }
        finally {
            lock.unlock();
        }
    }

    /// <summary>
    /// Lists all known Player.
    /// </summary>
    public List<PlayerInfoFilename> getKnownPlayer() {
        return List.of(knownPlayer.toArray(new PlayerInfoFilename[]{}));
    }

    /// <summary>
    /// Registeres a new File to the Storage.
    /// </summary>
    /// <param name="file"></param>
    public void registerFile(String file)
    {
        lock.lock();
        try
        {
            if (scannedFiles.contains(file.toLowerCase()))
                return;

            // Kick den alten
            if (configuration.getKnownFiles().contains(file.toLowerCase()))
                unregisterFile(file);

            ArrayList<PlayerInfo> result = AiAnalysis.analyse(file, true);

            if (result.size() > 0)
            {
                configuration.getKnownFiles().add(file.toLowerCase());
                for (var player : result)
                {
                    var playerInfo = new PlayerInfoFilename(player, file.toLowerCase());
                    knownPlayer.add(playerInfo);
                }
            }

            saveConfiguration();
        }
        finally {
            lock.unlock();
        }
    }

    /// <summary>
    /// Unregisteres a known File from the Storage.
    /// </summary>
    /// <param name="file"></param>
    public void unregisterFile(String file)
    {
        lock.lock();
        try
        {
            if (configuration.getKnownFiles().contains(file.toLowerCase()))
            {
                scannedFiles.remove(file.toLowerCase());
                configuration.getKnownFiles().remove(file.toLowerCase());
                knownPlayer.removeAll((p) => String.compare(((PlayerInfoFilename)p).File, file.toLowerCase(), true) == 0);
                saveConfiguration();
            }
        } finally {
            lock.unlock();
        }
    }

    /// <summary>
    /// Adds all included Player without adding to the registered files.
    /// </summary>
    /// <param name="file"></param>
    private void scanFile(String file)
    {
        try
        {
            ArrayList<PlayerInfo> result = AiAnalysis.analyse(file.toLowerCase(), false);
            for (PlayerInfo player : result)
                knownPlayer.add(new PlayerInfoFilename(player, file));

            scannedFiles.add(file.toLowerCase());
        }
        catch (Exception ex) { throw ex; }
    }

    private void saveConfiguration()
    {
        try
        {
            
            using (Stream stream = File.Open(path, FileMode.Create))
            {
                XmlSerializer serializer = new XmlSerializer(typeof(PlayerStoreConfiguration));
                serializer.Serialize(stream, configuration);
            }
        }
        catch (Exception ex) { }
    }

    private void loadConfiguration()
    {
        try
        {
            using (Stream stream = File.Open(path, FileMode.Open))
            {
                XmlSerializer serializer = new XmlSerializer(typeof(PlayerStoreConfiguration));
                var config = (PlayerStoreConfiguration)serializer.Deserialize(stream);
                if (config != null)
                    configuration = config;
            }
        }
        catch (Exception ex) { }

    }
}
