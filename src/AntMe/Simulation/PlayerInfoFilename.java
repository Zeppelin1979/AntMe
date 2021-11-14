package AntMe.Simulation;

public final class PlayerInfoFilename extends PlayerInfo {
    /// <summary>
    /// Pfad zur KI-Datei
    /// </summary>
    private String file;

    /// <summary>
    /// Creates an instance of PlayerInfoFilename
    /// </summary>
    public PlayerInfoFilename() { }

    /// <summary>
    /// Konstruktor der SpielerInfo mit Dateinamen
    /// </summary>
    public PlayerInfoFilename(String file)
    {
        this.file = file;
    }

    /// <summary>
    /// Konstruktor der SpielerInfo mit Dateinamen
    /// </summary>
    /// <param name="info"></param>
    /// <param name="file"></param>
    public PlayerInfoFilename(PlayerInfo info, String file)
    {
        super(info);
        this.file = file;
    }

    /// <summary>
    /// Ermittelt, ob die KIs gleich sind
    /// </summary>
    /// <param name="obj"></param>
    /// <returns></returns>
    @Override
    public boolean equals(Object obj)
    {
        PlayerInfoFilename other = (PlayerInfoFilename)obj;
        return (hashCode() == other.hashCode());
    }

    /// <summary>
    /// Erzeugt einen Hash aus den gegebenen Daten
    /// </summary>
    /// <returns>Hashcode</returns>
    @Override
    public int hashCode()
    {
        return file.hashCode() ^ this.getClassName().hashCode();
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
