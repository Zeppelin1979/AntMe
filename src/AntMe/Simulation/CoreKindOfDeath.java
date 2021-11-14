package AntMe.Simulation;
/// <summary>
/// Beschreibt wie eine Ameise gestorben ist.
/// </summary>
/// <author>Wolfgang Gallo (wolfgang@antme.net)</author>

public enum CoreKindOfDeath {
    /// <summary>
    /// Gibt an, dass die Ameise verhungert ist.
    /// </summary>
    Starved,

    /// <summary>
    /// Gibt an, dass die Ameise von einer Wanze gefressen wurde.
    /// </summary>
    Eaten,

    /// <summary>
    /// Gibt an, dass die Ameise von einer feindlichen Ameise besiegt wurde.
    /// </summary>
    Beaten

}
