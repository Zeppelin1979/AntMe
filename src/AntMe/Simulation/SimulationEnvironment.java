package AntMe.Simulation;

import java.util.Random;


import AntMe.SharedComponents.States.SimulationState;
import javafx.event.Event;

public final class SimulationEnvironment {

    public static final int PLAYGROUND_UNIT = 64;

    private int currentRound;
    private int playerCount;
    private Random random;

    /// <summary>
    /// Holds the current playground.
    /// </summary>
    private CorePlayground playground;

    /// <summary>
    /// Holds a list of active teams.
    /// </summary>
    private CoreTeam[] teams;

    /// <summary>
    /// Holds the "colony" of bugs.
    /// </summary>
    private CoreColony bugs;

    /// <summary>
    /// Creates a new instance of SimulationEnvironment
    /// </summary>
    public SimulationEnvironment()
    {
        precalculateAngles();
        playerCall.AreaChanged += playerCallAreaChanged;
    }

    /// <summary>
    /// Weitergabe des Verantwortungswechsels
    /// </summary>
    /// <param name="sender"></param>
    /// <param name="e"></param>
    private void playerCallAreaChanged(Object sender, AreaChangeEventArgs e)
    {
        AreaChange(this, e);
    }

    /// <summary>
    /// Initialisiert die Simulation um mit Runde 1 zu beginnen
    /// </summary>
    /// <param name="configuration">Konfiguration der Simulation</param>
    /// <throws><see cref="ArgumentException"/></throws>
    /// <throws><see cref="ArgumentNullException"/></throws>
    /// <throws><see cref="PathTooLongException"/></throws>
    /// <throws><see cref="DirectoryNotFoundException"/></throws>
    /// <throws><see cref="IOException"/></throws>
    /// <throws><see cref="UnauthorizedAccessException"/></throws>
    /// <throws><see cref="FileNotFoundException"/></throws>
    /// <throws><see cref="NotSupportedException"/></throws>
    /// <throws><see cref="SecurityException"/></throws>
    /// <throws><see cref="FileLoadException"/></throws>
    /// <throws><see cref="BadImageFormatException"/></throws>
    /// <throws><see cref="RuleViolationException"/></throws>
    /// <throws><see cref="TypeLoadException"/></throws>
    public void init(SimulatorConfiguration configuration)
    {
        // Init some varialbes
        currentRound = 0;
        if (configuration.getMapInitialValue() != 0)
            random = new Random(configuration.getMapInitialValue());
        else
            random = new Random();

        // count players
        playerCount = 0;
        for (TeamInfo team : configuration.getTeams())
        {
            playerCount += team.getPlayer().size();
        }

        // Sugar-relevant stuff
        sugarDelay = 0;
        sugarCountDown = (int)(SimulationSettings.getCustom().SugarTotalCount *
                                (1 + (SimulationSettings.getCustom().SugarTotalCountPlayerMultiplier * playerCount)));
        sugarLimit = (int)(SimulationSettings.getCustom().SugarSimultaneousCount *
                            (1 + (SimulationSettings.getCustom().SugarCountPlayerMultiplier * playerCount)));

        // Fruit-relevant stuff
        fruitDelay = 0;
        fruitCountDown = (int)(SimulationSettings.getCustom().FruitTotalCount *
                                (1 + (SimulationSettings.getCustom().FruitTotalCountPlayerMultiplier * playerCount)));
        fruitLimit = (int)(SimulationSettings.getCustom().FruitSimultaneousCount *
                            (1 + (SimulationSettings.getCustom().FruitCountPlayerMultiplier * playerCount)));

        // Ant-relevant stuff
        int antCountDown = (int)(SimulationSettings.getCustom().AntTotalCount *
                                  (1 + (SimulationSettings.getCustom().AntTotalCountPlayerMultiplier * playerCount)));
        antLimit = (int)(SimulationSettings.getCustom().AntSimultaneousCount *
                          (1 + (SimulationSettings.getCustom().AntCountPlayerMultiplier * playerCount)));

        // Spielfeld erzeugen
        float area = SimulationSettings.getCustom().PlayGroundBaseSize;
        area *= 1 + (playerCount * SimulationSettings.getCustom().PlayGroundSizePlayerMultiplier);
        int playgroundWidth = (int)Math.round(Math.sqrt(area * 4 / 3));
        int playgroundHeight = (int)Math.round(Math.sqrt(area * 3 / 4));
        playground = new CorePlayground(playgroundWidth, playgroundHeight, random, playerCount);

        // Bugs-relevant stuff
        bugs = new CoreColony(playground);
        bugs.setInsectCountDown((int)(SimulationSettings.getCustom().BugTotalCount *
                                      (1 + (SimulationSettings.getCustom().BugTotalCountPlayerMultiplier * playerCount))));
        bugLimit = (int)(SimulationSettings.getCustom().BugSimultaneousCount *
                          (1 + (SimulationSettings.getCustom().BugCountPlayerMultiplier * playerCount)));

        // Völker erzeugen
        teams = new CoreTeam[configuration.getTeams().size()];
        int i=0;
        for (TeamInfo team: configuration.getTeams())
        {
            teams[i] = new CoreTeam(i, team.getGuid(), team.getName());
            teams[i].createColonies(team.getPlayer().size());

            // Völker erstellen
            for (int j = 0; j < team.getPlayer().size(); j++)
            {
                PlayerInfo player = team.getPlayer().get(j);
                CoreColony colony = new CoreColony(playground, player, teams[i]);
                teams[i].getColonies()[j] = colony;

                colony.getAntHills().add(playground.neuerBau(colony.getId()));
                colony.setInsectCountDown(antCountDown);
            }
            i++;
        }
    }


    /// <summary>
    /// Berechnet einen neuen Spielschritt
    /// </summary>
    /// <returns>Zustandskopie des Simulationsstandes nachdem der Schritt ausgeführt wurde</returns>
    /// <throws>RuleViolationException</throws>
    /// <throws>Exception</throws>
    public void step(SimulationState simulationState)
    {
        currentRound++;

        removeSugar();
        spawnSugar();
        spawnFruit();

        Bugs.Grids[0].Clear();
        for (int i = 0; i < Teams.Length; i++)
        {
            for (int j = 0; j < Teams[i].Colonies.Length; j++)
            {
                Bugs.Grids[0].Add(Teams[i].Colonies[j].Insects);
            }
        }

        // Lasse die Wanzen von der Spiel Befehle entgegen nehmen.
        //foreach (CoreBug wanze in Bugs.Insects) {
        //    wanze.NimmBefehleEntgegen = true;
        //}

        // Schleife über alle Wanzen.
        for (int bugIndex = 0; bugIndex < Bugs.Insects.Count; bugIndex++)
        {
            CoreBug bug = (CoreBug) Bugs.Insects[bugIndex];
            Debug.Assert(bug != null);

            bug.NimmBefehleEntgegen = true;

            // Finde Ameisen in Angriffsreichweite.
            List<CoreInsect> ants = Bugs.Grids[0].FindAnts(bug);

            // Bestimme wie der Schaden auf die Ameisen verteilt wird.
            if (ants.Count >= SimulationSettings.getCustom().BugAttack)
            {
                // Es sind mehr Ameisen in der SpielUmgebung als die Wanze
                // Schadenpunke verteilen kann. Daher werden den Ameisen am
                // Anfang der Liste jeweils ein Energiepunkt abgezogen.
                for (int index = 0; index < SimulationSettings.getCustom().BugAttack; index++)
                {
                    ants[index].AktuelleEnergieBase--;
                    //((Ameise)ameisen[i]).WirdAngegriffen(wanze);
                    PlayerCall.UnderAttack((CoreAnt)ants[index], bug);
                    if (ants[index].AktuelleEnergieBase <= 0)
                    {
                        ants[index].colony.EatenInsects.Add(ants[index]);
                    }
                }
            }
            else if (ants.Count > 0)
            {
                // Bestimme die Energie die von jeder Ameise abgezogen wird.
                // Hier können natürlich Rundungsfehler auftreten, die die Wanze
                // abschwächen, die ignorieren wir aber.
                int schaden = SimulationSettings.getCustom().BugAttack / ants.Count;
                for (int index = 0; index < ants.Count; index++)
                {
                    ants[index].AktuelleEnergieBase -= schaden;
                    //((Ameise)ameisen[i]).WirdAngegriffen(wanze);
                    PlayerCall.UnderAttack((CoreAnt)ants[index], bug);
                    if (ants[index].AktuelleEnergieBase <= 0)
                    {
                        ants[index].colony.EatenInsects.Add(ants[index]);
                    }
                }
            }

            // Während eines Kampfes kann die Wanze sich nicht bewegen.
            if (ants.Count > 0)
            {
                continue;
            }

            // Bewege die Wanze.
            bug.Bewegen();
            if (bug.RestStreckeBase == 0)
            {
                bug.DreheInRichtungBase(random.Next(360));
                bug.GeheGeradeausBase(random.Next(160, 320));
            }
            bug.NimmBefehleEntgegen = false;
        }

        // Verhindere, daß ein Spieler einer gesichteten Wanze Befehle gibt.
        //for(int i = 0; i < Bugs.Insects.Count; i++) {
        //  CoreBug wanze = Bugs.Insects[i] as CoreBug;
        //  if(wanze != null) {
        //    wanze.NimmBefehleEntgegen = false;
        //  }
        //}

        // Loop through all teams.
        for (int teamIndex = 0; teamIndex < Teams.Length; teamIndex++)
        {
            // Loop through all colonies in that team.
            for (int colonyIndex = 0; colonyIndex < Teams[teamIndex].Colonies.Length; colonyIndex++)
            {
                CoreColony colony = Teams[teamIndex].Colonies[colonyIndex];

                // Leere alle Buckets.
                for (int casteIndex = 0; casteIndex < colony.AnzahlKasten; casteIndex++)
                {
                    colony.Grids[casteIndex].Clear();
                }

                // Fülle alle Buckets, aber befülle keinen Bucket doppelt.
                for (int casteIndex = 0; casteIndex < colony.AnzahlKasten; casteIndex++)
                {
                    if (colony.Grids[casteIndex].Count == 0)
                    {
                        colony.Grids[casteIndex].Add(Bugs.Insects);
                        for (int j = 0; j < Teams.Length; j++)
                        {
                            for (int i = 0; i < Teams[j].Colonies.Length; i++)
                            {
                                CoreColony v = Teams[j].Colonies[i];
                                colony.Grids[casteIndex].Add(v.Insects);
                            }
                        }
                    }
                }

                // Schleife über alle Ameisen.
                for (int antIndex = 0; antIndex < colony.Insects.Count; antIndex++)
                {
                    CoreAnt ameise = (CoreAnt) colony.Insects[antIndex];
                    Debug.Assert(ameise != null);

                    // Finde und Zähle die Insekten im Sichtkreis der Ameise.
                    CoreBug wanze;
                    CoreAnt feind;
                    CoreAnt freund;
                    CoreAnt teammember;
                    int bugCount, enemyAntCount, colonyAntCount, casteAntCount, teamAntCount;
                    colony.Grids[ameise.CasteIndexBase].findAndCountInsects(
                        ameise,
                        out wanze,
                        out bugCount,
                        out feind,
                        out enemyAntCount,
                        out freund,
                        out colonyAntCount,
                        out casteAntCount,
                        out teammember,
                        out teamAntCount);
                    ameise.BugsInViewrange = bugCount;
                    ameise.ForeignAntsInViewrange = enemyAntCount;
                    ameise.FriendlyAntsInViewrange = colonyAntCount;
                    ameise.FriendlyAntsFromSameCasteInViewrange = casteAntCount;
                    ameise.TeamAntsInViewrange = teamAntCount;

                    // Bewege die Ameise.
                    ameise.Bewegen();

 
                    // Ameise hat ihre Reichweite überschritten.
                    if (ameise.ZurückgelegteStreckeI > colony.ReichweiteI[ameise.CasteIndexBase])
                    {
                        ameise.AktuelleEnergieBase = 0;
                        colony.VerhungerteInsekten.Add(ameise);
                        continue;
                    }

                        // Ameise hat ein Drittel ihrer Reichweite zurückgelegt.
                    else if (ameise.ZurückgelegteStreckeI > colony.ReichweiteI[ameise.CasteIndexBase] / 3)
                    {
                        if (ameise.IstMüdeBase == false)
                        {
                            ameise.IstMüdeBase = true;
                            PlayerCall.BecomesTired(ameise);
                        }
                    }


                    // Rufe die Ereignisse auf, falls die Ameise nicht schon ein 
                    // entsprechendes Ziel hat.
                    if (wanze != null && !(ameise.ZielBase is CoreBug))
                    {
                        PlayerCall.SpotsEnemy(ameise, wanze);
                    }
                    if (feind != null && !(ameise.ZielBase is CoreAnt) ||
                        (ameise.ZielBase is CoreAnt && ((CoreAnt)ameise.ZielBase).colony == colony))
                    {
                        PlayerCall.SpotsEnemy(ameise, feind);
                    }
                    if (freund != null && !(ameise.ZielBase is CoreAnt) ||
                        (ameise.ZielBase is CoreAnt && ((CoreAnt)ameise.ZielBase).colony != colony))
                    {
                        PlayerCall.SpotsFriend(ameise, freund);
                    }
                    if (teammember != null && !(ameise.ZielBase is CoreAnt) ||
                        (ameise.ZielBase is CoreAnt && ((CoreAnt)ameise.ZielBase).colony != colony))
                    {
                        PlayerCall.SpotsTeamMember(ameise, teammember);
                    }

                    // Kampf mit Wanze.
                    if (ameise.ZielBase is CoreBug)
                    {
                        CoreBug k = (CoreBug)ameise.ZielBase;
                        if (k.AktuelleEnergieBase > 0)
                        {
                            int entfernung =
                                CoreCoordinate.BestimmeEntfernungI(ameise.CoordinateBase, ameise.ZielBase.CoordinateBase);
                            if (entfernung < SimulationSettings.getCustom().BattleRange * PLAYGROUND_UNIT)
                            {
                                k.AktuelleEnergieBase -= ameise.AngriffBase;
                                if (k.AktuelleEnergieBase <= 0)
                                {
                                    Bugs.EatenInsects.Add(k);
                                    colony.Statistik.KilledBugs++;
                                    ameise.BleibStehenBase();
                                }
                            }
                        }
                        else
                        {
                            ameise.ZielBase = null;
                        }
                    }

                        // Kampf mit feindlicher Ameise.
                    else if (ameise.ZielBase is CoreAnt)
                    {
                        CoreAnt a = (CoreAnt)ameise.ZielBase;
                        if (a.colony != colony && a.AktuelleEnergieBase > 0)
                        {
                            int entfernung =
                                CoreCoordinate.BestimmeEntfernungI(ameise.CoordinateBase, ameise.ZielBase.CoordinateBase);
                            if (entfernung < SimulationSettings.getCustom().BattleRange * PLAYGROUND_UNIT)
                            {
                                PlayerCall.UnderAttack(a, ameise);
                                a.AktuelleEnergieBase -= ameise.AngriffBase;
                                if (a.AktuelleEnergieBase <= 0)
                                {
                                    a.colony.BeatenInsects.Add(a);
                                    colony.Statistik.KilledAnts++;
                                    ameise.BleibStehenBase();
                                }
                            }
                        }
                        else
                        {
                            ameise.ZielBase = null;
                        }
                    }


                    // Prüfe ob die Ameise an ihrem Ziel angekommen ist.
                    if (ameise.AngekommenBase)
                    {
                        ameiseUndZiel(ameise);
                    }

                    // Prüfe ob die Ameise einen Zuckerhaufen oder ein Obststück sieht.
                    ameiseUndZucker(ameise);
                    if (ameise.GetragenesObstBase == null)
                    {
                        ameiseUndObst(ameise);
                    }

                    // Prüfe ob die Ameise eine Markierung bemerkt.
                    ameiseUndMarkierungen(ameise);

                    if (ameise.ZielBase == null && ameise.RestStreckeBase == 0)
                    {
                        PlayerCall.Waits(ameise);
                    }

                    PlayerCall.Tick(ameise);
                }

                removeAnt(colony);
                spawnAnt(colony);

                aktualisiereMarkierungen(colony);
                removeFruit(colony);
            }
        }

        removeBugs();
        healBugs();
        spawnBug();

        bewegeObstUndInsekten();

        erzeugeZustand(simulationState);
    }


    /// <summary>
    /// Erzeugt einen Zustand aus dem aktuellen Spielumstand
    /// </summary>
    /// <returns>aktueller Spielstand</returns>
    private void erzeugeZustand(SimulationState zustand)
    {
        zustand.setPlaygroundWidth(playground.getWidth());
        zustand.setPlaygroundHeight(playground.getHeight());
        zustand.setCurrentRound(currentRound);

        for (int i = 0; i < teams.length; i++)
        {
            zustand.getTeamStates().add(teams[i].createState());
        }

        for (int i = 0; i < bugs.getInsects().size(); i++)
        {
            zustand.getBugStates().add(((CoreBug)bugs.getInsects().get(i)).erzeugeInfo());
        }

        for (int i = 0; i < playground.getSugarHills().size(); i++)
        {
            zustand.getSugarStates().add(playground.getSugarHills().get(i).createState());
        }

        for (int i = 0; i < playground.getFruits().size(); i++)
        {
            zustand.FruitStates.add(playground.getFruits().get(i).ErzeugeInfo());
        }
    }

 
    public AreaChangeEventHandler AreaChange;

    public static int[][] Cos, Sin;

    private static void precalculateAngles()
    {
        int max = SimulationSettings.getCustom().getMaximumSpeed() * PLAYGROUND_UNIT + 1;

        Cos = new int[max + 1][360];
        Sin = new int[max + 1][360];

        // Cosinus und Sinus Werte vorberechnen.
        for (int amplitude = 0; amplitude <= max; amplitude++)
        {
            for (int winkel = 0; winkel < 360; winkel++)
            {
                Cos[amplitude][winkel] =
                  (int)Math.round(amplitude * Math.cos(winkel * Math.PI / 180d));
                Sin[amplitude][winkel] =
                  (int)Math.round(amplitude * Math.sin(winkel * Math.PI / 180d));
            }
        }
    }

    public static int cosinus(int amplitude, int winkel)
    {
        return (int)Math.round(amplitude * Math.cos(winkel * Math.PI / 180d));
    }

    public static int sinus(int amplitude, int winkel)
    {
        return (int)Math.round(amplitude * Math.sin(winkel * Math.PI / 180d));
    }

    /// <summary>
    /// Delay-counter for sugar-respawn.
    /// </summary>
    private int sugarDelay;

    /// <summary>
    /// Counts down the total number of allowed sugar-hills.
    /// </summary>
    private int sugarCountDown;

    /// <summary>
    /// Gets the count of simultaneous existing sugar-hills. 
    /// </summary>
    private int sugarLimit;

    /// <summary>
    /// Removes all empty sugar-hills from list.
    /// </summary>
    private void removeSugar()
    {
        // TODO: speedup
        //List<CoreSugar> gemerkterZucker = new List<CoreSugar>();
        for (int i = 0; i < playground.getSugarHills().size(); i++)
        {
            CoreSugar zucker = Playground.SugarHills[i];
            if (zucker != null)
            {
                if (zucker.Menge == 0)
                {
                    //gemerkterZucker.Add(zucker);
                    //L�schen
                    playground.entferneZucker(zucker);
                    i--;
                }
            }
        }
        //for(int i = 0; i < gemerkterZucker.Count; i++) {
        //  CoreSugar zucker = gemerkterZucker[i];
        //  if(zucker != null) {
        //    Playground.SugarHills.Remove(zucker);
        //  }
        //}
        //gemerkterZucker.Clear();
    }

    /// <summary>
    /// Spawns new sugar, if its time.
    /// </summary>
    private void spawnSugar()
    {
        if (playground.SugarHills.Count < sugarLimit &&
           sugarDelay <= 0 &&
           sugarCountDown > 0)
        {
            sugarDelay = SimulationSettings.getCustom().SugarRespawnDelay;
            sugarCountDown--;
            Playground.NeuerZucker();
        }
        sugarDelay--;
    }

    /// <summary>
    /// Delay-counter for fruit-respawn.
    /// </summary>
    private int fruitDelay;

    /// <summary>
    /// Counts down the total number of allowed fruits.
    /// </summary>
    private int fruitCountDown;

    /// <summary>
    /// Gets the count of simultaneous existing fruits. 
    /// </summary>
    private int fruitLimit;

    /// <summary>
    /// Spawns new fruit, if its time.
    /// </summary>
    private void spawnFruit()
    {
        if (playground.getFruits().size() < fruitLimit &&
           fruitDelay <= 0 &&
           fruitCountDown > 0)
        {
            fruitDelay = SimulationSettings.getCustom().FruitRespawnDelay;
            fruitCountDown--;
            playground.neuesObst();
        }
        fruitDelay--;
    }

    /// <summary>
    /// Removes fruit from list.
    /// </summary>
    /// <param name="colony">winning colony</param>
    private void removeFruit(CoreColony colony)
    {
        //List<CoreFruit> gemerktesObst = new List<CoreFruit>();
        for (int j = 0; j < Playground.Fruits.Count; j++)
        {
            CoreFruit obst = Playground.Fruits[j];
            for (int i = 0; i < colony.AntHills.Count; i++)
            {
                CoreAnthill bau = colony.AntHills[i];
                if (bau != null)
                {
                    int entfernung = CoreCoordinate.BestimmeEntfernungI(obst.CoordinateBase, bau.CoordinateBase);
                    if (entfernung <= PLAYGROUND_UNIT)
                    {
                        //gemerktesObst.Add(obst);

                        // L�schen
                        colony.Statistik.CollectedFood += obst.Menge;
                        colony.Statistik.CollectedFruits++;
                        obst.Menge = 0;
                        for (int z = 0; z < obst.TragendeInsekten.Count; z++)
                        {
                            CoreInsect insect = obst.TragendeInsekten[z];
                            if (insect != null)
                            {
                                insect.GetragenesObstBase = null;
                                insect.AktuelleLastBase = 0;
                                insect.RestStreckeI = 0;
                                insect.RestWinkelBase = 0;
                                insect.GeheZuBauBase();
                            }
                        }
                        obst.TragendeInsekten.Clear();
                        Playground.EntferneObst(obst);
                        j--;
                    }
                }
            }
        }
    }

    /// <summary>
    /// Gets the count of simultaneous existing ants. 
    /// </summary>
    private int antLimit;

    /// <summary>
    /// Pr�ft ob eine Ameise an ihrem Ziel angekommen ist.
    /// </summary>
    /// <param name="ant">betroffene Ameise</param>
    private static void ameiseUndZiel(CoreAnt ant)
    {
        // Ameisenbau.
        if (ant.ZielBase is CoreAnthill)
        {
            if (ant.GetragenesObstBase == null)
            {
                ant.Zur�ckgelegteStreckeI = 0;
                ant.ZielBase = null;
                ant.SmelledMarker.Clear();
                ant.colony.Statistik.CollectedFood += ant.AktuelleLastBase;
                ant.AktuelleLastBase = 0;
                ant.AktuelleEnergieBase = ant.MaximaleEnergieBase;
                ant.IstM�deBase = false;
            }
        }

          // Zuckerhaufen.
        else if (ant.ZielBase is CoreSugar)
        {
            CoreSugar zucker = (CoreSugar)ant.ZielBase;
            ant.ZielBase = null;
            if (zucker.Menge > 0)
            {
                PlayerCall.TargetReached(ant, zucker);
            }
        }

          // Obstst�ck.
        else if (ant.ZielBase is CoreFruit)
        {
            CoreFruit obst = (CoreFruit)ant.ZielBase;
            ant.ZielBase = null;
            if (obst.Menge > 0)
            {
                PlayerCall.TargetReached(ant, obst);
            }
        }

          // Insekt.
        else if (ant.ZielBase is CoreInsect) { }

          // Anderes Ziel.
        else
        {
            ant.ZielBase = null;
        }
    }

    /// <summary>
    /// Pr�ft ob eine Ameise einen Zuckerhaufen sieht.
    /// </summary>
    /// <param name="ant">betroffene Ameise</param>
    private void ameiseUndZucker(CoreAnt ant)
    {
        for (int i = 0; i < Playground.SugarHills.Count; i++)
        {
            CoreSugar sugar = Playground.SugarHills[i];
            int entfernung = CoreCoordinate.BestimmeEntfernungI(ant.CoordinateBase, sugar.CoordinateBase);
            if (ant.ZielBase != sugar && entfernung <= ant.SichtweiteI)
            {
                PlayerCall.Spots(ant, sugar);
            }
        }
    }

    /// <summary>
    /// Pr�ft ob eine Ameise ein Obsst�ck sieht.
    /// </summary>
    /// <param name="ameise">betroffene Ameise</param>
    private void ameiseUndObst(CoreAnt ameise)
    {
        for (int i = 0; i < Playground.Fruits.Count; i++)
        {
            CoreFruit obst = Playground.Fruits[i];
            int entfernung = CoreCoordinate.BestimmeEntfernungI(ameise.CoordinateBase, obst.CoordinateBase);
            if (ameise.ZielBase != obst && entfernung <= ameise.SichtweiteI)
            {
                PlayerCall.Spots(ameise, obst);
            }
        }
    }

    /// <summary>
    /// Pr�ft ob die Ameise eine Markierung bemerkt.
    /// </summary>
    /// <param name="ameise">betroffene Ameise</param>
    private static void ameiseUndMarkierungen(CoreAnt ameise)
    {
        CoreMarker marker = ameise.colony.Marker.FindMarker(ameise);
        if (marker != null)
        {
            PlayerCall.SmellsFriend(ameise, marker);
            ameise.SmelledMarker.Add(marker);
        }
    }

    /// <summary>
    /// Erntfernt Ameisen die keine Energie mehr haben.
    /// </summary>
    /// <param name="colony">betroffenes Volk</param>
    private void removeAnt(CoreColony colony)
    {
        List<CoreAnt> liste = new List<CoreAnt>();

        for (int i = 0; i < colony.VerhungerteInsekten.Count; i++)
        {
            CoreAnt ant = colony.VerhungerteInsekten[i] as CoreAnt;
            if (ant != null && !liste.Contains(ant))
            {
                liste.Add(ant);
                colony.Statistik.StarvedAnts++;
                PlayerCall.HasDied(ant, CoreKindOfDeath.Starved);
            }
        }

        for (int i = 0; i < colony.EatenInsects.Count; i++)
        {
            CoreAnt ant = colony.EatenInsects[i] as CoreAnt;
            if (ant != null && !liste.Contains(ant))
            {
                liste.Add(ant);
                colony.Statistik.EatenAnts++;
                PlayerCall.HasDied(ant, CoreKindOfDeath.Eaten);
            }
        }

        for (int i = 0; i < colony.BeatenInsects.Count; i++)
        {
            CoreAnt ant = colony.BeatenInsects[i] as CoreAnt;
            if (ant != null)
            {
                if (!liste.Contains(ant))
                {
                    liste.Add(ant);
                    colony.Statistik.BeatenAnts++;
                    PlayerCall.HasDied(ant, CoreKindOfDeath.Beaten);
                }
            }
        }

        for (int i = 0; i < liste.Count; i++)
        {
            CoreAnt ant = liste[i];
            if (ant != null)
            {
                colony.EntferneInsekt(ant);

                for (int j = 0; j < Playground.Fruits.Count; j++)
                {
                    CoreFruit fruit = Playground.Fruits[j];
                    fruit.TragendeInsekten.Remove(ant);
                }
            }
        }

        colony.VerhungerteInsekten.Clear();
        colony.EatenInsects.Clear();
        colony.BeatenInsects.Clear();
    }

    /// <summary>
    /// Erzeugt neue Ameisen.
    /// </summary>
    /// <param name="colony">betroffenes Volk</param>
    private void spawnAnt(CoreColony colony)
    {
        if (colony.Insects.Count < antLimit &&
           colony.insectDelay < 0 &&
           colony.insectCountDown > 0)
        {
            colony.NeuesInsekt(random);
            colony.insectDelay = SimulationSettings.getCustom().AntRespawnDelay;
            colony.insectCountDown--;
        }
        colony.insectDelay--;
    }

    // Bewegt Obsst�cke und alle Insekten die das Obsst�ck tragen.
    private void bewegeObstUndInsekten()
    {
        Playground.Fruits.ForEach(delegate(CoreFruit fruit)
        {
            if (fruit.TragendeInsekten.Count > 0)
            {
                int dx = 0;
                int dy = 0;
                int last = 0;

                fruit.TragendeInsekten.ForEach(delegate(CoreInsect insect)
                {
                    if (insect.ZielBase != fruit && insect.RestWinkelBase == 0)
                    {
                        dx += Cos[insect.aktuelleGeschwindigkeitI, insect.RichtungBase];
                        dy += Sin[insect.aktuelleGeschwindigkeitI, insect.RichtungBase];
                        last += insect.AktuelleLastBase;
                    }
                });

                last = Math.Min((int)(last * SimulationSettings.getCustom().FruitLoadMultiplier), fruit.Menge);
                dx = dx * last / fruit.Menge / fruit.TragendeInsekten.Count;
                dy = dy * last / fruit.Menge / fruit.TragendeInsekten.Count;

                fruit.CoordinateBase = new CoreCoordinate(fruit.CoordinateBase, dx, dy);
                fruit.TragendeInsekten.ForEach(
                  delegate(CoreInsect insect) { insect.CoordinateBase = new CoreCoordinate(insect.CoordinateBase, dx, dy); });
            }
        });
        //foreach(CoreFruit obst in Playground.Fruits) {
        //  if(obst.TragendeInsekten.Count > 0) {
        //    int dx = 0;
        //    int dy = 0;
        //    int last = 0;

        //    foreach(CoreInsect insekt in obst.TragendeInsekten) {
        //      if(insekt.ZielBase != obst && insekt.RestWinkelBase == 0) {
        //        dx += Cos[insekt.aktuelleGeschwindigkeitI, insekt.RichtungBase];
        //        dy += Sin[insekt.aktuelleGeschwindigkeitI, insekt.RichtungBase];
        //        last += insekt.AktuelleLastBase;
        //      }
        //    }

        //    last = Math.Min((int)(last * SimulationSettings.Settings.FruitLoadMultiplier), obst.Menge);
        //    dx = dx * last / obst.Menge / obst.TragendeInsekten.Count;
        //    dy = dy * last / obst.Menge / obst.TragendeInsekten.Count;

        //    obst.Coordinate = new CoreCoordinate(obst.Coordinate, dx, dy);

        //    foreach(CoreInsect insekt in obst.TragendeInsekten) {
        //      insekt.Coordinate = new CoreCoordinate(insekt.Coordinate, dx, dy);
        //    }
        //  }
        //}
    }

    /// <summary>
    /// Entfernt abgelaufene Markierungen und erzeugt neue Markierungen.
    /// </summary>
    /// <param name="colony">betroffenes Volk</param>
    private static void aktualisiereMarkierungen(CoreColony colony)
    {
        // TODO: Settings ber�cksichtigen
        // Markierungen aktualisieren und inaktive Markierungen l�schen.
        List<CoreMarker> gemerkteMarkierungen = new List<CoreMarker>();

        foreach (CoreMarker markierung in colony.Marker)
        {
            if (markierung.IstAktiv)
            {
                markierung.Aktualisieren();
            }
            else
            {
                gemerkteMarkierungen.Add(markierung);
            }
        }
        gemerkteMarkierungen.ForEach(delegate(CoreMarker marker)
        {
            colony.Insects.ForEach(delegate(CoreInsect insect)
            {
                CoreAnt ant = insect as CoreAnt;
                if (ant != null)
                {
                    ant.SmelledMarker.Remove(marker);
                }
            });
        });
        colony.Marker.Remove(gemerkteMarkierungen);

        // Neue Markierungen �berpr�fen und hinzuf�gen.
        gemerkteMarkierungen.Clear();
        colony.NewMarker.ForEach(delegate(CoreMarker newMarker)
        {
            bool zuNah = false;
            foreach (CoreMarker markierung in colony.Marker)
            {
                int entfernung =
                  CoreCoordinate.BestimmeEntfernungDerMittelpunkteI
                    (markierung.CoordinateBase, newMarker.CoordinateBase);
                if (entfernung < SimulationSettings.getCustom().MarkerDistance * PLAYGROUND_UNIT)
                {
                    zuNah = true;
                    break;
                }
            }
            if (!zuNah)
            {
                colony.Marker.Add(newMarker);
            }
        });
        colony.NewMarker.Clear();
    }

    /// <summary>
    /// Gets the count of simultaneous existing bugs. 
    /// </summary>
    private int bugLimit;

    /// <summary>
    /// Remove dead bugs.
    /// </summary>
    private void removeBugs()
    {
        for (int i = 0; i < Bugs.EatenInsects.Count; i++)
        {
            CoreBug bug = Bugs.EatenInsects[i] as CoreBug;
            if (bug != null)
            {
                Bugs.Insects.Remove(bug);
            }
        }
        Bugs.EatenInsects.Clear();
    }

    /// <summary>
    /// Heals the bugs, if its time.
    /// </summary>
    private void healBugs()
    {
        if (currentRound % SimulationSettings.getCustom().BugRegenerationDelay == 0)
        {
            for (int i = 0; i < Bugs.Insects.Count; i++)
            {
                CoreBug bug = Bugs.Insects[i] as CoreBug;
                if (bug != null)
                {
                    if (bug.AktuelleEnergieBase < bug.MaximaleEnergieBase)
                    {
                        bug.AktuelleEnergieBase += SimulationSettings.getCustom().BugRegenerationValue;
                    }
                }
            }
        }
    }

    /// <summary>
    /// Spawn new bugs, if needed.
    /// </summary>
    private void spawnBug()
    {
        if (Bugs.Insects.Count < bugLimit &&
           Bugs.insectDelay < 0 &&
           Bugs.insectCountDown > 0)
        {
            Bugs.NeuesInsekt(random);
            Bugs.insectDelay = SimulationSettings.getCustom().BugRespawnDelay;
            Bugs.insectCountDown--;
        }
        Bugs.insectDelay--;
    }

}
