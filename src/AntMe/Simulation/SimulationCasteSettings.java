package AntMe.Simulation;

import java.util.Locale;
import java.util.ResourceBundle;

/// <summary>
    /// Holds the set of caste-Settings.
    /// </summary>
public class SimulationCasteSettings {
    /// <summary>
    /// Offset to shift the array-index.
    /// </summary>
    public int Offset;

    /// <summary>
    /// Sum of all points.
    /// </summary>
    public int Sum;

    /// <summary>
    /// List of caste-setting-columns.
    /// </summary>
    private SimulationCasteSettingsColumn[] columns;
    public void createCasteSettingsColumn(int count) {
        columns = new SimulationCasteSettingsColumn[count];
    }

    /// <summary>
    /// Gives the lowest Column-Index.
    /// </summary>
    public int getMinIndex() {
        return Offset;
    }

    private ResourceBundle bundle = ResourceBundle.getBundle("package.Resource", Locale.getDefault());

    /// <summary>
    /// Gives the highest Column-Index.
    /// </summary>
    public int getMaxIndex() {
        return Offset + columns.length -1;
    }

    /// <summary>
    /// Delivers the right caste-column.
    /// </summary>
    /// <param name="index">index of column</param>
    /// <returns>caste-Column</returns>
    public SimulationCasteSettingsColumn get(int index) {
        if (index < Offset) {
            throw new IndexOutOfBoundsException(bundle.getString("SimulationCoreSettingsCasteColumnToSmall"));
        }
        else if (index > getMaxIndex()) {
            throw new IndexOutOfBoundsException(bundle.getString("SimulationCoreSettingsCasteColumnToBig"));
        }

        // Deliver the right column
        return columns[index - Offset];
    }

    /// <summary>
    /// Checks the value-ranges of all properties.
    /// </summary>
    public void ruleCheck() throws ConfigurationErrorsException {
        
        if (Offset > 0)
        {
            throw new ConfigurationErrorsException("Ein Kasten-Offset darf nicht größer als 0 sein");
        }

        for (SimulationCasteSettingsColumn column : columns)
        {
            column.ruleCheck();
        }
    }

    public int size() {
        return columns.length;
    }

}
