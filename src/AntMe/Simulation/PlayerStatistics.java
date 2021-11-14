package AntMe.Simulation;
/// <summary>
/// Speichert die Statistik eines Spielers.
/// </summary>

public class PlayerStatistics {
    /// <summary>
    /// Die aktuelle Anzahl an Ameisen.
    /// </summary>
    public int CurrentAntCount;

    private int LoopCount;

    /// <summary>
    /// Die Anzahl der durch eigene Ameisen besiegten feindlichen Ameisen.
    /// </summary>
    public int KilledAnts;

    /// <summary>
    /// Die Anzahl der besiegten Wanzen.
    /// </summary>
    public int KilledBugs;

    /// <summary>
    /// Die Anzahl der gesammelten Nahrungspunkte.
    /// </summary>
    public int CollectedFood;

    /// <summary>
    /// Anzahl gesammeltem Obst.
    /// </summary>
    public int CollectedFruits;

    /// <summary>
    /// Die Anzahl der verhungerten Ameisen.
    /// </summary>
    public int StarvedAnts;

    /// <summary>
    /// Die Anzahl der von feindlichen Ameisen besiegten eigenen Ameisen.
    /// </summary>
    public int BeatenAnts;

    /// <summary>
    /// Die Anzahl der von Wanzen gefressenen Ameisen.
    /// </summary>
    public int EatenAnts;

    /// <summary>
    /// Gibt die Gesamtpunktzahl zurück.
    /// </summary>
    public int getPoints() {
        return (
            (int)(SimulationSettings.getCustom().PointsForFoodMultiplier * CollectedFood) +
            (SimulationSettings.getCustom().PointsForFruits * CollectedFruits) +
            (SimulationSettings.getCustom().PointsForBug * KilledBugs) +
            (SimulationSettings.getCustom().PointsForForeignAnt * KilledAnts) +
            (SimulationSettings.getCustom().PointsForBeatenAnts * BeatenAnts) +
            (SimulationSettings.getCustom().PointsForEatenAnts * EatenAnts) +
            (SimulationSettings.getCustom().PointsForStarvedAnts * StarvedAnts)
                )/Math.max(LoopCount, 1);
    }

    /// <summary>
    /// Zählt zwei Statistiken zusammen.
    /// </summary>
    /// <param name="s1">Statistik 1.</param>
    /// <param name="s2">Statistik 2.</param>
    /// <returns>Statistik 1 + Statistik 2.</returns>
    public static PlayerStatistics add(PlayerStatistics s1, PlayerStatistics s2) {
        PlayerStatistics s = new PlayerStatistics();
        s.CollectedFood = s1.CollectedFood + s2.CollectedFood;
        s.CollectedFruits = s1.CollectedFruits + s2.CollectedFruits;
        s.CurrentAntCount = s1.CurrentAntCount + s2.CurrentAntCount;
        s.StarvedAnts = s1.StarvedAnts + s2.StarvedAnts;
        s.EatenAnts = s1.EatenAnts + s2.EatenAnts;
        s.BeatenAnts = s1.BeatenAnts + s2.BeatenAnts;
        s.KilledAnts = s1.KilledAnts + s2.KilledAnts;
        s.KilledBugs = s1.KilledBugs + s2.KilledBugs;
        s.LoopCount = Math.max(s1.LoopCount + s2.LoopCount, 2);
        return s;
    }

}
