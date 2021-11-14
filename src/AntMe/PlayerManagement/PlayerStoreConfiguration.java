package AntMe.PlayerManagement;

import java.util.ArrayList;

public class PlayerStoreConfiguration {
    
    public PlayerStoreConfiguration() {
        setKnownFiles(new ArrayList<String>());
    }

    public ArrayList<String> getKnownFiles() {
        return knownFiles;
    }

    public void setKnownFiles(ArrayList<String> knownFiles) {
        this.knownFiles = knownFiles;
    }

    /// <summary>
    /// List of all known Player
    /// </summary>
    private ArrayList<String> knownFiles;

}
