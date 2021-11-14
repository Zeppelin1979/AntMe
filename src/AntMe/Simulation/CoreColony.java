package AntMe.Simulation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import AntMe.SharedComponents.States.ColonyState;

/// <summary>
/// Ein Ameisenvolk. Kapselt alle Informationen die zur Laufzeit eines Spiels
/// zu einem Spieler anfallen.
/// </summary>
/// <author>Wolfgang Gallo (wolfgang@antme.net)</author>

final class CoreColony {
    // Die Id des n�chsten erzeugten Volkes.
    private static int nextId = 0;
    private int[] antsInCaste;
    private ArrayList<CoreAnthill> antHills = new ArrayList<CoreAnthill>();
    ArrayList<CoreAnthill> getAntHills() {
        return antHills;
    }
    private CoreTeam team;
    private ArrayList<CoreInsect> beatenInsects = new ArrayList<CoreInsect>();
    private ArrayList<CoreInsect> eatenInsects = new ArrayList<CoreInsect>();

    /// <summary>
    /// Die Id die das Volk w�hrend eines laufenden Spiels idenzifiziert.
    /// </summary>
    private int id;
    public int getId() {
        return id;
    }

    /// <summary>
    /// Die Guid, die das Volk eindeutig identifiziert.
    /// </summary>
    private UUID guid;
    public UUID getGuid() {
        return guid;
    }

    private ArrayList<CoreInsect> insects = new ArrayList<CoreInsect>();
    ArrayList<CoreInsect> getInsects() {
        return insects;
    }
    private Class<? extends CoreInsect> Klasse;
    private Grid marker;
    Grid getMarker() {
        return marker;
    }
    ArrayList<CoreMarker> newMarker = new ArrayList<CoreMarker>();
    ArrayList<CoreMarker> getNewMarker() {
        return newMarker;
    }

    /// <summary>
    /// Der Spieler der das Verhalten des Volkes steuert.
    /// </summary>
    private PlayerInfo player;
    PlayerInfo getPlayer() {
        return player;
    }

    private ArrayList<CoreInsect> verhungerteInsekten = new ArrayList<CoreInsect>();

    private int breiteI;
    int getBreiteI() {
        return breiteI;
    }
    private int breiteI2;
    int getBreiteI2() {
        return breiteI2;
    }
    private int hoeheI;
    int getHoeheI() {
        return hoeheI;
    }
    private int hoeheI2;
    int getHoeheI2() {
        return hoeheI2;
    }
    private CorePlayground playground;
    public CorePlayground getPlayground() {
        return playground;
    }

    private PlayerStatistics statistic = new PlayerStatistics();
    PlayerStatistics getStatistic() {
        return statistic;
    }

    private PlayerStatistics averageStatistic = new PlayerStatistics();
    PlayerStatistics getAverageStatistic() {
        return statistic;
    }

    /// <summary>
    /// Z�hlt die Anzahl an Runden herunter, bis wieder eine neus Insekt erzeugt
    /// werden kann.
    /// </summary>
    private int insectDelay = 0;

    private int insectCountDown;
    int getInsectCountDown() {
        return insectCountDown;
    }
    void setInsectCountDown(int value) {
        this.insectCountDown = value;
    }

    /// <summary>
    /// Die Angriffswerte aller Kasten des Volkes.
    /// </summary>
    private int[] angriff;
    int[] getAngriff() {
        return angriff;
    }

    /// <summary>
    /// Gitter f�r die verschiedenen Sichtweiten.
    /// </summary>
    private Grid[] grids;
    Grid[] getGrids() {
        return grids;
    }

    /// <summary>
    /// Die Drehgeschwindigkeiten aller Kasten des Volkes in Grad pro Runde.
    /// </summary>
    private int[] drehgeschwindigkeit;
    int[] getDrehgeschwindigkeit() {
        return drehgeschwindigkeit;
    }

    /// <summary>
    /// Die Lebenspunkte aller Kasten des Volkes.
    /// </summary>
    private int[] energie;
    int[] getEnergie(){
        return energie;
    }

    /// <summary>
    /// Die Geschwindigkeiten aller Kasten des Volkes in der internen Einheit.
    /// </summary>
    private int[] geschwindigkeitI;
    int[] getGeschwindigkeitI() {
        return geschwindigkeitI;
    }

    /// <summary>
    /// Die maximalen Lastwerte aller Kasten des Volkes.
    /// </summary>
    private int[] last;
    int[] getLast() {
        return last;
    }

    /// <summary>
    /// Die Reichweiten aller Kasten des Volkes in der internen Einheit.
    /// </summary>
    private int[] reichweiteI;
    int[] getReichweiteI() {
        return reichweiteI;
    }

    /// <summary>
    /// Die Sichtweiten aller Kasten des Volkes in der internen Einheit.
    /// </summary>
    private int[] sichtweiteI;
    int[] getSichtweiteI() {
        return sichtweiteI;
    }

    /// <summary>
    /// Erzeugt eine neue Instanz der Volk-Klasse. Erzeugt ein Wanzen-Volk.
    /// </summary>
    /// <param name="spielfeld">Das Spielfeld.</param>
    CoreColony(CorePlayground spielfeld)
    {
        initPlayground(spielfeld);

        // Die Wanzen werden vom Spiel gesteuert.
        player = null;

        // Die Klasse ist in diesem Fall bereits bekannt.
        Klasse = CoreBug.class;

        //TODO: Werte �berpr�fen.
        geschwindigkeitI = new int[1];
        geschwindigkeitI[0] = SimulationSettings.getCustom().BugSpeed * SimulationEnvironment.PLAYGROUND_UNIT;
        drehgeschwindigkeit = new int[1];
        drehgeschwindigkeit[0] = SimulationSettings.getCustom().BugRotationSpeed;
        reichweiteI = new int[1];
        reichweiteI[0] = Integer.MAX_VALUE;
        sichtweiteI = new int[1];
        sichtweiteI[0] = 0;
        last = new int[1];
        last[0] = 0;
        energie = new int[1];
        energie[0] =  SimulationSettings.getCustom().BugEnergy;
        angriff = new int[1];
        angriff[0] = SimulationSettings.getCustom().BugAttack;

        grids = new Grid[1];
        grids[0] =
            Grid.create
                (
                spielfeld.getWidth() * SimulationEnvironment.PLAYGROUND_UNIT,
                spielfeld.getHeight() * SimulationEnvironment.PLAYGROUND_UNIT,
                SimulationSettings.getCustom().BattleRange * SimulationEnvironment.PLAYGROUND_UNIT);

        antsInCaste = new int[1];
    }

    /// <summary>
    /// Erzeugt eine neue Instanz der Volk-Klasse. Erzeugt ein Ameisen-Volk.
    /// </summary>
    /// <param name="spielfeld">Das Spielfeld.</param>
    /// <param name="spieler">Das Spieler zu dem das Volk geh�rt.</param>
    /// <param name="team">Das dazugeh�rige Team.</param>
    CoreColony(CorePlayground spielfeld, PlayerInfo spieler, CoreTeam team)
    {
        initPlayground(spielfeld);
        this.team = team;

        player = spieler;
        try {
            Klasse = (Class<? extends CoreInsect>) Class.forName(spieler.getClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Basisameisenkaste erstellen, falls keine Kasten definiert wurden
        if (spieler.getCastes().size() == 0)
        {
            spieler.getCastes().add(new CasteInfo());
        }

        geschwindigkeitI = new int[spieler.getCastes().size()];
        drehgeschwindigkeit = new int[spieler.getCastes().size()];
        last = new int[spieler.getCastes().size()];
        sichtweiteI = new int[spieler.getCastes().size()];
        grids = new Grid[spieler.getCastes().size()];
        reichweiteI = new int[spieler.getCastes().size()];
        energie = new int[spieler.getCastes().size()];
        angriff = new int[spieler.getCastes().size()];
        antsInCaste = new int[spieler.getCastes().size()];

        int index = 0;
        for (CasteInfo caste : spieler.getCastes())
        {
            geschwindigkeitI[index] = SimulationEnvironment.PLAYGROUND_UNIT;
            geschwindigkeitI[index] *= SimulationSettings.getCustom().CasteSettings.get(caste.getSpeed()).Speed;
            drehgeschwindigkeit[index] = SimulationSettings.getCustom().CasteSettings.get(caste.getRotationSpeed()).RotationSpeed;
            last[index] = SimulationSettings.getCustom().CasteSettings.get(caste.getLoad()).Load;
            sichtweiteI[index] = SimulationEnvironment.PLAYGROUND_UNIT;
            sichtweiteI[index] *= SimulationSettings.getCustom().CasteSettings.get(caste.getViewRange()).ViewRange;
            reichweiteI[index] = SimulationEnvironment.PLAYGROUND_UNIT;
            reichweiteI[index] *= SimulationSettings.getCustom().CasteSettings.get(caste.getRange()).Range;
            energie[index] = SimulationSettings.getCustom().CasteSettings.get(caste.getEnergy()).Energy;
            angriff[index] = SimulationSettings.getCustom().CasteSettings.get(caste.getAttack()).Attack;

            grids[index] =
                Grid.create
                    (
                    spielfeld.getWidth() * SimulationEnvironment.PLAYGROUND_UNIT,
                    spielfeld.getHeight() * SimulationEnvironment.PLAYGROUND_UNIT,
                    sichtweiteI[index]);

            index++;
        }
    }

    private void initPlayground(CorePlayground spielfeld)
    {
        playground = spielfeld;
        breiteI = spielfeld.getWidth() * SimulationEnvironment.PLAYGROUND_UNIT;
        hoeheI = spielfeld.getHeight() * SimulationEnvironment.PLAYGROUND_UNIT;
        breiteI2 = breiteI * 2;
        hoeheI2 = hoeheI * 2;

        

        marker =
            new Grid
                (
                spielfeld.getWidth() * SimulationEnvironment.PLAYGROUND_UNIT,
                spielfeld.getHeight() * SimulationEnvironment.PLAYGROUND_UNIT,
                SimulationSettings.getCustom().getMaximumMarkerSize() * SimulationEnvironment.PLAYGROUND_UNIT);

        id = nextId++;
        guid = UUID.randomUUID();
    }

    /// <summary>
    /// Die Anzahl von Insektenkasten in diesem Volk.
    /// </summary>
    public int getAnzahlKasten()
    {
        return player.getCastes().size();
    }

    /// <summary>
    /// Erstellt eine neues Insekt.
    /// </summary>
    void neuesInsekt(Random random)
    {
        HashMap<String, Integer> dictionary = null;

        if (getPlayer() != null)
        {
            dictionary = new HashMap<String, Integer>();
            for (CasteInfo caste : getPlayer().getCastes())
            {
                if (!dictionary.containsKey(caste.getName()))
                {
                    dictionary.put(caste.getName(), antsInCaste[dictionary.size()]);
                }
            }
        }

        //Insekt insekt =
        //    (Insekt)
        //    Klasse.Assembly.CreateInstance
        //        (Klasse.FullName, false, BindingFlags.Default, null, new object[] {this, dictionary}, null,
        //         new object[] {});
        CoreInsect insekt;
        try {
            insekt = (CoreInsect) Klasse.getConstructor().newInstance();
            insekt.init(this, random, dictionary);

            // Merke das Insekt.
            insects.add(insekt);
            antsInCaste[insekt.getCasteIndexBase()]++;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /// <summary>
    /// Entfernt ein Insekt.
    /// </summary>
    /// <param name="insekt">Insekt</param>
    void entferneInsekt(CoreInsect insekt)
    {
        if (insects.remove(insekt))
        {
            antsInCaste[insekt.getCasteIndexBase()]--;
        }
    }

    /// <summary>
    /// Erzeugt ein VolkZustand Objekt mit dem aktuellen Zustand des Volkes.
    /// </summary>
    /// <returns></returns>
    public ColonyState erzeugeInfo()
    {
        ColonyState info = new ColonyState(id, player.getGuid(), player.getColonyName(), player.getFirstName() + " " + player.getLastName());

        info.setCollectedFood(statistic.CollectedFood);
        info.setCollectedFruits(statistic.CollectedFruits);
        info.setStarvedAnts(statistic.StarvedAnts);
        info.setEatenAnts(statistic.EatenAnts);
        info.setBeatenAnts(statistic.BeatenAnts);
        info.setKilledBugs(statistic.KilledBugs);
        info.setKilledEnemies(statistic.KilledAnts);
        info.setPoints(statistic.getPoints());

        int index;

        for (index = 0; index < antHills.size(); index++)
        {
            info.getAnthillStates().add(antHills.get(index).erzeugeInfo());
        }

        for (index = 1; index < player.getCastes().size(); index++)
        {
            info.getCasteStates().add(player.getCastes().get(index).createState(id, index));
        }

        for (index = 0; index < insects.size(); index++)
        {
            info.getAntStates().add(((CoreAnt)insects.get(index)).erzeugeInfo());
        }

        // Markierungen ist ein Bucket und die Bucket Klasse enth�lt keinen
        // Indexer. Daher k�nnen wir hier keine for Schleife verwenden, wie eben
        // bei den Bauten und den Ameisen. Daher benutzen wir eine foreach
        // Schleife f�r die wir eine extra Laufvariable ben�tigen.
        for (CoreMarker markierung : marker)
        {
            info.getMarkerStates().add(markierung.erzeugeInfo());
        }

        return info;
    }

}
