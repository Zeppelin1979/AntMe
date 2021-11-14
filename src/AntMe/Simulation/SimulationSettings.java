package AntMe.Simulation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

/// <summary>
/// Simulation-Settings from application-configuration.
/// </summary>
public class SimulationSettings {

    /// <summary>
    /// Sets or gets the name of this settings-set.
    /// </summary>
    public String SettingsName;

    /// <summary>
    /// Gets or sets Guid of that settings-set.
    /// </summary>
    public UUID Guid;

    /// <summary>
    /// Gets the size of the playground in SquareAntSteps.
    /// </summary>
    public int PlayGroundBaseSize;

    /// <summary>
    /// Gets the multiplier of additional playground-size per player.
    /// </summary>
    public float PlayGroundSizePlayerMultiplier;

    /// <summary>
    /// Gets the radius of anthills.
    /// </summary>
    public int AntHillRadius;

    /// <summary>
    /// Minimum Battle-Distance in steps between two insects.
    /// </summary>
    public int BattleRange;

    /// <summary>
    /// Displacement of the anthill form the circle point.
    /// </summary>
    public float AntHillRandomDisplacement;

    /// <summary>
    /// Size of the spwancells.
    /// </summary>
    public int SpawnCellSize;

    /// <summary>
    /// Radius of the restrictedzone around the anthill.
    /// </summary>
    public int RestrictedZoneRadius;

    /// <summary>
    /// Max. distance from the farthest anthill.
    /// </summary>
    public int FarZoneRadius;

    /// <summary>
    /// Decrease value for the neighbor cells if food spawns.
    /// </summary>
    public float DecreaseValue;

    /// <summary>
    /// Value to regenerate all cells at a food spawn.
    /// </summary>
    public float RegenerationValue;

    /// <summary>
    /// Gets the maximum count of ants simultaneous on playground.
    /// </summary>
    public int AntSimultaneousCount;

    /// <summary>
    /// Gets the maximum count of bugs simultaneous on playground.
    /// </summary>
    public int BugSimultaneousCount;

    /// <summary>
    /// Gets the maximum count of sugar simultaneous on playground.
    /// </summary>
    public int SugarSimultaneousCount;

    /// <summary>
    /// Gets the maximum count of fruit simultaneous on playground.
    /// </summary>
    public int FruitSimultaneousCount;

    /// <summary>
    /// Gets the multiplier for maximum count of bugs simultaneous on playground per player.
    /// </summary>
    public float BugCountPlayerMultiplier;

    /// <summary>
    /// Gets the multiplier for maximum count of sugar simultaneous on playground per player.
    /// </summary>
    public float SugarCountPlayerMultiplier;

    /// <summary>
    /// Gets the multiplier for maximum count of fruit simultaneous on playground per player.
    /// </summary>
    public float FruitCountPlayerMultiplier;

    /// <summary>
    /// Gets the multiplier for maximum count of ants simultaneous on playground per player.
    /// </summary>
    public float AntCountPlayerMultiplier;

    /// <summary>
    /// Gets the maximum count of ants in the whole simulation.
    /// </summary>
    public int AntTotalCount;

    /// <summary>
    /// Gets the maximum count of bugs in the whole simulation.
    /// </summary>
    public int BugTotalCount;

    /// <summary>
    /// Gets the maximum count of sugar in the whole simulation.
    /// </summary>
    public int SugarTotalCount;

    /// <summary>
    /// Gets the maximum count of fruit in the whole simulation.
    /// </summary>
    public int FruitTotalCount;

    /// <summary>
    /// Gets the multiplier for maximum count of ants per player in the whole simulation.
    /// </summary>
    public float AntTotalCountPlayerMultiplier;

    /// <summary>
    /// Gets the multiplier for maximum count of bugs per player in the whole simulation.
    /// </summary>
    public float BugTotalCountPlayerMultiplier;

    /// <summary>
    /// Gets the multiplier for maximum count of sugar per player in the whole simulation.
    /// </summary>
    public float SugarTotalCountPlayerMultiplier;

    /// <summary>
    /// Gets the multiplier for maximum count of fruit per player in the whole simulation.
    /// </summary>
    public float FruitTotalCountPlayerMultiplier;

    /// <summary>
    /// Gets the delay for ant before next respawn in rounds. 
    /// </summary>
    public int AntRespawnDelay;

    /// <summary>
    /// Gets the delay for bugs before next respawn in rounds. 
    /// </summary>
    public int BugRespawnDelay;

    /// <summary>
    /// Gets the delay for sugar before next respawn in rounds. 
    /// </summary>
    public int SugarRespawnDelay;

    /// <summary>
    /// Gets the delay for fruits before next respawn in rounds. 
    /// </summary>
    public int FruitRespawnDelay;

    /// <summary>
    /// Gets the attack-value of bugs.
    /// </summary>
    public int BugAttack;

    /// <summary>
    /// Gets the rotation speed of bugs.
    /// </summary>
    public int BugRotationSpeed;

    /// <summary>
    /// Gets the energy of bugs.
    /// </summary>
    public int BugEnergy;

    /// <summary>
    /// Gets the speed of bugs.
    /// </summary>
    public int BugSpeed;

    /// <summary>
    /// Gets the attack-range of bugs.
    /// </summary>
    public int BugRadius;

    /// <summary>
    /// Gets the regeneration-value of bugs.
    /// </summary>
    public int BugRegenerationValue;

    /// <summary>
    /// Gets the delay in rounds between the regeneration-steps of bugs.
    /// </summary>
    public int BugRegenerationDelay;

    /// <summary>
    /// Gets the minimal amount of food in sugar-hills.
    /// </summary>
    public int SugarAmountMinimum;

    /// <summary>
    /// Gets the maximum amount of food in sugar-hills.
    /// </summary>
    public int SugarAmountMaximum;

    /// <summary>
    /// Gets the minimal amount of food in fruits.
    /// </summary>
    public int FruitAmountMinimum;

    /// <summary>
    /// Gets the maximum amount of food in fruits.
    /// </summary>
    public int FruitAmountMaximum;

    /// <summary>
    /// Gets the multiplier for fruits between load and amount of food.
    /// </summary>
    public float FruitLoadMultiplier;

    /// <summary>
    /// Gets the multiplier for fruits between radius and amount of food.
    /// </summary>
    public float FruitRadiusMultiplier;

    /// <summary>
    /// Gets the minimal size of a marker.
    /// </summary>
    public int MarkerSizeMinimum;

    /// <summary>
    /// Gets the minimal allowed distance between two marker.
    /// </summary>
    public int MarkerDistance;

    /// <summary>
    /// Gets the maximum age in rounds of a marker.
    /// </summary>
    public int MarkerMaximumAge;

    /// <summary>
    /// Multiplier for the calculation from food to points.
    /// </summary>
    public float PointsForFoodMultiplier;

    /// <summary>
    /// Gets the amount of points for collected fruits.
    /// </summary>
    public int PointsForFruits;

    /// <summary>
    /// Gets the amount of points for killed bugs.
    /// </summary>
    public int PointsForBug;

    /// <summary>
    /// Gets the amount of points for killed foreign ants.
    /// </summary>
    public int PointsForForeignAnt;

    /// <summary>
    /// Gets the amount of points for own dead ants killed by bugs.
    /// </summary>
    public int PointsForEatenAnts;

    /// <summary>
    /// Gets the amount of points for own dead ants killed by foreign ants.
    /// </summary>
    public int PointsForBeatenAnts;

    /// <summary>
    /// Gets the amount of points for own dead starved ants.
    /// </summary>
    public int PointsForStarvedAnts;

    /// <summary>
    /// Gives the caste-Settings.
    /// </summary>
    public SimulationCasteSettings CasteSettings;

    private ResourceBundle bundle = ResourceBundle.getBundle("package.Resource", Locale.getDefault());

    private static SimulationSettings defaultSettings = new SimulationSettings();
    private static SimulationSettings customSettings = new SimulationSettings();
    private static boolean initDefault;
    private static boolean initCustom;

    /// <summary>
    /// Gets the default settings.
    /// </summary>
    public static SimulationSettings getDefault()
    {
        if (!initDefault)
        {
            defaultSettings.setDefaults();
            initDefault = true;
        }
        return defaultSettings;
    }

    /// <summary>
    /// Gives the current simulation-settings
    /// </summary>
    public static SimulationSettings getCustom()
    {
        if (!initCustom)
        {
            return getDefault();
        }
        return customSettings;
    }

    /// <summary>
    /// Sets a custom set of settings.
    /// </summary>
    /// <param name="settings">custom settings</param>
    public static void setCustomSettings(SimulationSettings settings) throws ConfigurationErrorsException
    {
        settings.ruleCheck();
        customSettings = settings;
        initCustom = true;
    }

    /// <summary>
    /// Resets the values to the default settings.
    /// </summary>
    public void setDefaults()
    {
        SettingsName = bundle.getString("SettingsDefaultName");

        // Guid
        Guid = UUID.fromString("{C010EC26-0F4C-442c-8C36-0D6A71842A41}");//
    
        // Playground
        PlayGroundBaseSize = 550000;
        PlayGroundSizePlayerMultiplier = 1;
        AntHillRadius = 32;
        BattleRange = 5;

        AntHillRandomDisplacement = 0.5f;
        SpawnCellSize = 100;
        RestrictedZoneRadius = 300;
        FarZoneRadius = 1500;
        DecreaseValue = 2f;
        RegenerationValue = 0.1f;

        // Livetime and Respawn
        AntSimultaneousCount = 100;
        BugSimultaneousCount = 5;
        SugarSimultaneousCount = 1;
        FruitSimultaneousCount = 2;

        BugCountPlayerMultiplier = 1f;
        SugarCountPlayerMultiplier = 1f;
        FruitCountPlayerMultiplier = 1f;
        AntCountPlayerMultiplier = 0f;

        AntTotalCount = 999999;
        BugTotalCount = 999999;
        SugarTotalCount = 999999;
        FruitTotalCount = 999999;

        AntTotalCountPlayerMultiplier = 0f;
        BugTotalCountPlayerMultiplier = 0f;
        SugarTotalCountPlayerMultiplier = 0f;
        FruitTotalCountPlayerMultiplier = 0f;

        AntRespawnDelay = 15;
        BugRespawnDelay = 75;
        SugarRespawnDelay = 150;
        FruitRespawnDelay = 225;

        // Bugsettings
        BugAttack = 50;
        BugRotationSpeed = 3;
        BugEnergy = 1000;
        BugSpeed = 3;
        BugRadius = 4;
        BugRegenerationValue = 1;
        BugRegenerationDelay = 5;

        // Foodstuff
        SugarAmountMinimum = 1000;
        SugarAmountMaximum = 1000;
        FruitAmountMinimum = 250;
        FruitAmountMaximum = 250;
        FruitLoadMultiplier = 5;
        FruitRadiusMultiplier = 1;

        // Marker
        MarkerSizeMinimum = 20;
        MarkerDistance = 13;
        MarkerMaximumAge = 150;

        // Points
        PointsForFoodMultiplier = 1;
        PointsForFruits = 0;
        PointsForBug = 150;
        PointsForForeignAnt = 5;
        PointsForEatenAnts = 0;
        PointsForBeatenAnts = -5;
        PointsForStarvedAnts = 0;

        // Castes
        CasteSettings = new SimulationCasteSettings();
        CasteSettings.Offset = -1;
        CasteSettings.createCasteSettingsColumn(4);

        CasteSettings.get(0).Attack = 0;
        CasteSettings.get(0).Energy = 50;
        CasteSettings.get(0).Load = 4;
        CasteSettings.get(0).Range = 1800;
        CasteSettings.get(0).RotationSpeed = 6;
        CasteSettings.get(0).Speed = 3;
        CasteSettings.get(0).ViewRange = 45;

        CasteSettings.get(1).Attack = 10;
        CasteSettings.get(1).Energy = 100;
        CasteSettings.get(1).Load = 5;
        CasteSettings.get(1).Range = 2250;
        CasteSettings.get(1).RotationSpeed = 8;
        CasteSettings.get(1).Speed = 4;
        CasteSettings.get(1).ViewRange = 60;

        CasteSettings.get(2).Attack = 20;
        CasteSettings.get(2).Energy = 175;
        CasteSettings.get(2).Load = 7;
        CasteSettings.get(2).Range = 3400;
        CasteSettings.get(2).RotationSpeed = 12;
        CasteSettings.get(2).Speed = 5;
        CasteSettings.get(2).ViewRange = 75;

        CasteSettings.get(3).Attack = 30;
        CasteSettings.get(3).Energy = 250;
        CasteSettings.get(3).Load = 10;
        CasteSettings.get(3).Range = 4500;
        CasteSettings.get(3).RotationSpeed = 16;
        CasteSettings.get(3).Speed = 6;
        CasteSettings.get(3).ViewRange = 90;
    }

    /// <summary>
    /// Checks the value-ranges of all properties.
    /// </summary>
    public void ruleCheck() throws ConfigurationErrorsException
    {

        // TODO: Strings into res-files

        // Playground
        if (PlayGroundBaseSize < 100000)
        {
            throw new ConfigurationErrorsException("Grundgröße des Spielfeldes muss größer 100.000 sein");
        }

        if (PlayGroundSizePlayerMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Playground Playermultiplikator darf nicht kleiner 0 sein");
        }

        if (AntHillRadius < 0)
        {
            throw new ConfigurationErrorsException("Ameisenbau braucht einen Radius >= 0");
        }

        if (BattleRange < 0)
        {
            throw new ConfigurationErrorsException("Angriffsradius der Wanze darf nicht kleiner 0 sein");
        }

        if (AntHillRandomDisplacement < 0f || AntHillRandomDisplacement > 1f)
        {
            throw new ConfigurationErrorsException("Der Wert der Zufälligen verschiebung vom Kreispunkt muss zwischen 0.0 (0%) und 1.0 (100%) liegen.");
        }

        if (SpawnCellSize < 1 && SpawnCellSize != 0)
        {
            throw new ConfigurationErrorsException("Die Größe der Spawnzelle darf nicht kleiner 1 sein.");
        }

        //überprüfen ob genug Spawnzellen da sind
        int cellsX = (int)Math.ceil((PlayGroundBaseSize * (4f / 3f)) / SpawnCellSize) - 2;
        int cellsY = (int)Math.ceil((PlayGroundBaseSize * (3f / 4f)) / SpawnCellSize) - 2;

        if (cellsX * cellsY < SugarSimultaneousCount + FruitSimultaneousCount)
        {
            throw new ConfigurationErrorsException("Die Größe der Spawnzellen ist zu groß, so das es nicht gewährleistet ist, dass genug Spawnzellen für alle Nahrung vorhanden sind.");
        }

        if (RestrictedZoneRadius < 0)
        {
            throw new ConfigurationErrorsException("Der Radius der gesperrten Zone um den Ameisenbau darf nicht kleiner 0 sein.");
        }

        if (FarZoneRadius < 0)
        {
            throw new ConfigurationErrorsException("Der Radius der zu weit entfernten Zone darf nicht kleiner 0 sein.");
        }

        if (DecreaseValue < 0)
        {
            throw new ConfigurationErrorsException("Der verringerungs Wert für Nachbarzellen darf nicht kleiner 0 sein");
        }

        if (RegenerationValue < 0)
        {
            throw new ConfigurationErrorsException("Der regenerirungs Wert aller Zellen darf nicht kleiner 0 sein");
        }

        // Livetime and Respawn
        if (AntSimultaneousCount < 0)
        {
            throw new ConfigurationErrorsException("Weniger als 0 simultane Ameisen sind nicht möglich");
        }

        if (BugSimultaneousCount < 0)
        {
            throw new ConfigurationErrorsException("Weniger als 0 simultane Wanzen sind nicht möglich");
        }

        if (SugarSimultaneousCount < 0)
        {
            throw new ConfigurationErrorsException("Weniger als 0 simultane Zuckerberge sind nicht möglich");
        }

        if (FruitSimultaneousCount < 0)
        {
            throw new ConfigurationErrorsException("Weniger als 0 simultanes Obst sind nicht möglich");
        }

        if (BugCountPlayerMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Negative Spielermuliplikatoren bei Wanzen ist nicht zulässig");
        }

        if (SugarCountPlayerMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Negative Spielermuliplikatoren bei Zucker ist nicht zulässig");
        }

        if (FruitCountPlayerMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Negative Spielermuliplikatoren bei Obst ist nicht zulässig");
        }

        if (AntCountPlayerMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Negative Spielermuliplikatoren bei Ameisen ist nicht zulässig");
        }

        if (AntTotalCount < 0)
        {
            throw new ConfigurationErrorsException("Negative Gesamtmenge bei Ameisen ist nicht zulässig");
        }

        if (BugTotalCount < 0)
        {
            throw new ConfigurationErrorsException("Negative Gesamtmenge bei Wanzen ist nicht zulässig");
        }

        if (SugarTotalCount < 0)
        {
            throw new ConfigurationErrorsException("Negative Gesamtmenge bei Zucker ist nicht zulässig");
        }

        if (FruitTotalCount < 0)
        {
            throw new ConfigurationErrorsException("Negative Gesamtmenge bei Obst ist nicht zulässig");
        }

        if (AntTotalCountPlayerMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Negative Spielermuliplikatoren bei Ameisen ist nicht zulässig");
        }

        if (BugTotalCountPlayerMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Negative Spielermuliplikatoren bei Wanzen ist nicht zulässig");
        }

        if (SugarTotalCountPlayerMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Negative Spielermuliplikatoren bei Zucker ist nicht zulässig");
        }

        if (FruitTotalCountPlayerMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Negative Spielermuliplikatoren bei Obst ist nicht zulässig");
        }

        if (AntRespawnDelay < 0)
        {
            throw new ConfigurationErrorsException("Negative Respawnzeit bei Ameisen ist nicht zulässig");
        }

        if (BugRespawnDelay < 0)
        {
            throw new ConfigurationErrorsException("Negative Respawnzeit bei Wanzen ist nicht zulässig");
        }

        if (SugarRespawnDelay < 0)
        {
            throw new ConfigurationErrorsException("Negative Respawnzeit bei Zucker ist nicht zulässig");
        }

        if (FruitRespawnDelay < 0)
        {
            throw new ConfigurationErrorsException("Negative Respawnzeit bei Obst ist nicht zulässig");
        }

        // Bugsettings
        if (BugAttack < 0)
        {
            throw new ConfigurationErrorsException("Negativer Angriffswert für Wanzen ist nicht zulässig");
        }

        if (BugRotationSpeed < 0)
        {
            throw new ConfigurationErrorsException("Negative Rotationsgeschwindigkeit für Wanzen ist nicht zulässig");
        }

        if (BugEnergy < 0)
        {
            throw new ConfigurationErrorsException("Negativer Energiewert für Wanzen ist nicht zulässig");
        }

        if (BugSpeed < 0)
        {
            throw new ConfigurationErrorsException("Negativer Geschwindigkeitswert für Wanzen ist nicht zulässig");
        }

        if (BugRadius < 0)
        {
            throw new ConfigurationErrorsException("Negativer Radius für Wanzen ist nicht zulässig");
        }

        if (BugRegenerationValue < 0)
        {
            throw new ConfigurationErrorsException("Negativer Regenerationswert für Wanzen ist nicht zulässig");
        }

        if (BugRegenerationDelay < 0)
        {
            throw new ConfigurationErrorsException("Negativer Regenerationsdelay für Wanzen ist nicht zulässig");
        }

        // Foodstuff
        if (SugarAmountMinimum < 0)
        {
            throw new ConfigurationErrorsException("Negativer Nahrungswert bei Zucker ist nicht zulässig");
        }

        if (SugarAmountMaximum < 0)
        {
            throw new ConfigurationErrorsException("Negativer Nahrungswert bei Zucker ist nicht zulässig");
        }

        if (FruitAmountMinimum < 0)
        {
            throw new ConfigurationErrorsException("Negativer Nahrungswert bei Obst ist nicht zulässig");
        }

        if (FruitAmountMaximum < 0)
        {
            throw new ConfigurationErrorsException("Negativer Nahrungswert bei Obst ist nicht zulässig");
        }

        if (FruitLoadMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Negativer Loadmultiplikator bei Obst ist nicht zulässig");
        }

        if (FruitRadiusMultiplier < 0)
        {
            throw new ConfigurationErrorsException("Negativer Radiusmultiplikator bei Obst ist nicht zulässig");
        }

        // Marker

        if (MarkerSizeMinimum < 0)
        {
            throw new ConfigurationErrorsException("Negative Minimalgröße bei Markierung ist nicht zulässig");
        }

        if (MarkerDistance < 0)
        {
            throw new ConfigurationErrorsException("Negative Mindestdistanz bei Markierung ist nicht zulässig");
        }

        if (MarkerMaximumAge < 0)
        {
            throw new ConfigurationErrorsException("Negative maximallebensdauer bei Markierungen ist nicht zulässig");
        }

        // Castes
        CasteSettings.ruleCheck();
    }

    /// <summary>
    /// Gets the maximal Speed of an insect.
    /// </summary>
    public int getMaximumSpeed()
    {
        int maxValue = BugSpeed;
        for (int i = 0; i < CasteSettings.size(); i++)
        {
            maxValue = Math.max(maxValue, CasteSettings.get(i).Speed);
        }
        return maxValue;
    }

    /// <summary>
    /// Gets the maximum size of a Marker.
    /// </summary>
    public int getMaximumMarkerSize()
    {
        // Maximalgröße für Marker ermitteln
        double baseMarkerVolume = Math.pow(SimulationSettings.getCustom().MarkerSizeMinimum, 3) * (Math.PI / 2);
        baseMarkerVolume *= 10f; // Größenkorrektur, weil die Basisparameter zu kleine maximalgröße Liefern

        double totalMarkerVolume = baseMarkerVolume * SimulationSettings.getCustom().MarkerMaximumAge;
        return (int)Math.pow(4 * ((totalMarkerVolume - baseMarkerVolume) / Math.PI), 1f / 3f);
    }

    /// <summary>
    /// Saves the settings to a setting-file.
    /// </summary>
    /// <param name="settings">settings to save</param>
    /// <param name="filename">filename of target file</param>
    public static void saveSettings(SimulationSettings settings, String filename)
    {
        // Serialize
        FileOutputStream target = null;
        ObjectOutputStream out = null;
        try {
            target = new FileOutputStream(filename);
            out = new ObjectOutputStream(target);
            out.writeObject(settings);
            out.close();
            target.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (target != null)
                try {
                    target.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }

    /// <summary>
    /// Loads settings from a setting-file.
    /// </summary>
    /// <param name="filename">filename of target file</param>
    /// <returns>Loaded settings</returns>
    public static SimulationSettings loadSettings(String filename)
    {
        FileInputStream source = null;
        try {
            source = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Open File
        return loadSettings(source);
    }

    public static SimulationSettings loadSettings(FileInputStream stream)
    {
        ObjectInputStream in = null;
        SimulationSettings setting = null;
        try {
            in = new ObjectInputStream(stream);
            setting = (SimulationSettings) in.readObject();
            in.close();
            stream.close();
            } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return setting;
    }

    /// <summary>
    /// Gives the name of the settings.
    /// </summary>
    /// <returns>Name of Settings</returns>
    @Override
    public String toString()
    {
        return SettingsName;
    }

    /// <summary>
    /// Checks, if two different simulation-sets are equal.
    /// </summary>
    /// <param name="obj"></param>
    /// <returns></returns>
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj.getClass() == SimulationSettings.class))
        {
            return false;
        }

        return Guid.equals(((SimulationSettings)obj).Guid);
    }

    /// <summary>
    /// Generates a Hash-Code for that object.
    /// </summary>
    /// <returns>Hash-Code</returns>
    @Override
    public int hashCode()
    {
        return Guid.hashCode();
    }
}
