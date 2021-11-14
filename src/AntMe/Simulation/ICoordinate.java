package AntMe.Simulation;

/// <summary>
/// Ermöglicht es, die Koordinate eines Objekts auf dem Spielfeld abzufragen.
/// </summary>
/// <author>Wolfgang Gallo (wolfgang@antme.net)</author>
public interface ICoordinate {
    /// <summary>
    /// Liest die Koordinate eines Objekts auf dem Spielfeld aus.
    /// </summary>
    CoreCoordinate getCoordinateBase();
}
