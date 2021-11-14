package AntMe.Simulation;

import AntMe.SharedComponents.States.AnthillState;

/// <summary>
/// Ein Bau eines Ameisenvolkes.
/// </summary>
/// <author>Wolfgang Gallo (wolfgang@antme.net)</author>

public class CoreAnthill implements ICoordinate {
        // Die Id des n�chsten erzeugten Bau.
        private static int newId = 0;

        /// <summary>
        /// Die Id die den Bau w�hrend eines Spiels eindeutig identifiziert.
        /// </summary>
        private int id;
        public int getId() {
            return id;
        }

        private int colonyId;

        /// <summary>
        /// Erzeugt eine neue Instanz der Bau-Klasse.
        /// <param name="x">X-Koordinate</param>
        /// <param name="y">Y.Koordinate</param>
        /// <param name="radius">Radius</param>
        /// <param name="colonyId">Volk ID</param>
        /// </summary>
        CoreAnthill(int x, int y, int radius, int colonyId)
        {
            this.colonyId = colonyId;
            id = newId++;
            coordinateBase = new CoreCoordinate(x, y, radius);
        }

         /// <summary>
        /// Die Position des Bau auf dem Spielfeld.
        /// </summary>
        private CoreCoordinate coordinateBase;
        public CoreCoordinate getCoordinateBase() {
            return coordinateBase;
        }
        void setCoordinateBase(CoreCoordinate value) {
            coordinateBase = value;
        }

        /// <summary>
        /// Erzeugt ein BauZustand-Objekt mit den aktuellen Daten des Bau.
        /// </summary>
        AnthillState erzeugeInfo()
        {
            AnthillState zustand = new AnthillState(colonyId, id);
            zustand.setPositionX(coordinateBase.getX() / SimulationEnvironment.PLAYGROUND_UNIT);
            zustand.setPositionY(coordinateBase.getY() / SimulationEnvironment.PLAYGROUND_UNIT);
            zustand.setRadius(coordinateBase.getRadius() / SimulationEnvironment.PLAYGROUND_UNIT);
            return zustand;
        }

}
