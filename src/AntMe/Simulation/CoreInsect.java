package AntMe.Simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

/// <summary>
/// Abstrakte Basisklasse für alle Insekten.
/// </summary>
/// <author>Wolfgang Gallo (wolfgang@antme.net)</author>
public abstract class CoreInsect implements ICoordinate {
    /// <summary>
    /// Die Id des nächste erzeugten Insekts.
    /// </summary>
    private static int neueId = 0;
    ResourceBundle bundle = ResourceBundle.getBundle("package.Resource", Locale.getDefault());

    /// <summary>
    /// Speichert die Markierungen, die das Insekt schon gesehen hat. Das
    /// verhindert, daß das Insekt zwischen Markierungen im Kreis läuft.
    /// </summary>
    private ArrayList<CoreMarker> smelledMarker = new ArrayList<CoreMarker>();
    ArrayList<CoreMarker> getSmelledMarker() {
        return smelledMarker;
    }

    private boolean reached = false;
    private int antCount = 0;
    private int casteCount = 0;
    private int colonyCount = 0;
    private int bugCount = 0;
    private int teamCount = 0;
    private CoreFruit getragenesObst;

    /// <summary>
    /// Die Id die das Insekt w�hrend eines Spiels eindeutig indentifiziert.
    /// </summary>
    private int id;
    int getId() {
        return id;
    }

    /// <summary>
    /// Die Position des Insekts auf dem Spielfeld.
    /// </summary>
    private CoreCoordinate koordinate;
    CoreCoordinate getKoordinate() {
        return koordinate;
    }

    /// <summary>
    /// Legt fest, ob das Insekt Befehle entgegen nimmt.
    /// </summary>
    private boolean nimmBefehleEntgegen = false;
    boolean isNimmBefehleEntgegen() {
        return nimmBefehleEntgegen;
    }

    private int restStreckeI;
    private int restWinkel = 0;

    /// <summary>
    /// Der Index der Kaste des Insekts in der Kasten-Struktur des Spielers.
    /// </summary>
    private int casteIndexBase;
    int getCasteIndexBase() {
        return casteIndexBase;
    }
    void setCasteIndexBase(int value) {
        casteIndexBase = value;
    }

    /// <summary>
    /// Das Volk zu dem das Insekts geh�rt.
    /// </summary>
    private CoreColony colony;
    public CoreColony getColony() {
        return colony;
    }

    public void setColony(CoreColony value) {
        colony = value;
    }

    private ICoordinate ziel = null;
    private int zurueckgelegteStreckeI;

    CoreInsect() { }

    /// <summary>
    /// Die Kaste des Insekts.
    /// </summary>
    public String getKasteBase() {
        return colony.getPlayer().getCastes().get(getCasteIndexBase()).getName();
    }

    /// <summary>
    /// Die Anzahl von Wanzen in Sichtweite des Insekts.
    /// </summary>
    public int getBugsInViewrange() {
        return bugCount;
    }
    public void setBugsInViewrange(int value) {
        bugCount = value;
    }

    /// <summary>
    /// Die Anzahl feindlicher Ameisen in Sichtweite des Insekts.
    /// </summary>
    public int getForeignAntsInViewrange() {
        return antCount;
    }
    public void setForeignAntsInViewrange(int value) {
        antCount = value;
    }

    /// <summary>
    /// Die Anzahl befreundeter Ameisen in Sichtweite des Insekts.
    /// </summary>
    public int getFriendlyAntsInViewrange() {
        return colonyCount;
    }
    public void setFriendlyAntsInViewrange(int value) {
        colonyCount = value;
    }

    /// <summary>
    /// Die Anzahl befreundeter Ameisen der selben Kaste in Sichtweite des
    /// Insekts.
    /// </summary>
    public int getFriendlyAntsFromSameCasteInViewrange() {
        return casteCount;
    }
    public void setFriendlyAntsFromSameCasteInViewrange(int value) {
        casteCount = value;
    }

    /// <summary>
    /// Anzahl Ameisen aus befreundeten V�lkern in sichtweite des Insekts.
    /// </summary>
    public int getTeamAntsInViewrange() {
        return teamCount;
    }
    public void setTeamAntsInViewrange(int value) {
        teamCount = value;
    }

    /// <summary>
    /// Die Richtung in die das Insekt gedreht ist.
    /// </summary>
    public int getRichtungBase()
    {
        return koordinate.getRichtung();
    }

    /// <summary>
    /// Die Strecke die die Ameise zur�ckgelegt hat, seit sie das letzte Mal in
    /// einem Ameisenbau war.
    /// </summary>
    public int getZurueckgelegteStreckeBase()
    {
        return zurueckgelegteStreckeI / SimulationEnvironment.PLAYGROUND_UNIT;
    }

    /// <summary>
    /// Die Strecke die die Ameise zur�ckgelegt hat, seit sie das letzte Mal in
    /// einem Ameisenbau war in der internen Einheit.
    /// </summary>
    public int getZurueckgelegteStreckeI() {
        return zurueckgelegteStreckeI;
    }

    public void setZurueckgelegteStreckeI(int value) {
        zurueckgelegteStreckeI = value;
    }

    /// <summary>
    /// Die Strecke die das Insekt geradeaus gehen wird, bevor das n�chste Mal
    /// Wartet() aufgerufen wird bzw. das Insekt sich zu seinem Ziel ausrichtet.
    /// </summary>
    public int getRestStreckeBase()
    {
        return restStreckeI / SimulationEnvironment.PLAYGROUND_UNIT;
    }

    /// <summary>
    /// Die Strecke die das Insekt geradeaus gehen wird, bevor das n�chste 
    /// Mal Wartet() aufgerufen wird bzw. das Insekt sich zu seinem Ziel
    /// ausrichtet in der internen Einheit.
    /// </summary>
    public int getRestStreckeI() {
        return restStreckeI;
    }
    public void setRestStreckeI(int value) {
        restStreckeI = value;
    }

    /// <summary>
    /// Der Winkel um den das Insekt sich noch drehen mu�, bevor es wieder
    /// geradeaus gehen kann.
    /// </summary>
    public int getRestWinkelBase() {
        return restWinkel;
    }

    public void setRestWinkelBase(int value) {
            // TODO: Modulo?
            restWinkel = value;
            while (restWinkel > 180)
            {
                restWinkel -= 360;
            }
            while (restWinkel < -180)
            {
                restWinkel += 360;
            }
    }

    /// <summary>
    /// Das Ziel auf das das Insekt zugeht.
    /// </summary>
    public ICoordinate getZielBase() {
        return ziel;
    }
    
    public void setZielBase(ICoordinate value) {
        if (ziel != value || value == null)
        {
            ziel = value;
            restWinkel = 0;
            restStreckeI = 0;
        }
    }

    /// <summary>
    /// Liefert die Entfernung in Schritten zum n�chsten Ameisenbau.
    /// </summary>
    public int getEntfernungZuBauBase() {
        int aktuelleEntfernung;
        int gemerkteEntfernung = Integer.MAX_VALUE;
        for (CoreAnthill bau : colony.getAntHills())
        {
            aktuelleEntfernung = CoreCoordinate.bestimmeEntfernungI(getCoordinateBase(), bau.getCoordinateBase());
            if (aktuelleEntfernung < gemerkteEntfernung)
            {
                gemerkteEntfernung = aktuelleEntfernung;
            }
        }
        return gemerkteEntfernung / SimulationEnvironment.PLAYGROUND_UNIT;
    }

    /// <summary>
    /// Gibt das Obst zur�ck, das das Insekt gerade tr�gt.
    /// </summary>
    public CoreFruit getGetragenesObstBase() {
        return getragenesObst;
    }
    public void setGetragenesObstBase(CoreFruit value) {
        getragenesObst = value;
    }

    /// <summary>
    /// Gibt zur�ck on das Insekt bei seinem Ziel angekommen ist.
    /// </summary>
    public boolean getAngekommenBase() {
        return reached;
    }

    private Random randomBase;
    public Random getRandomBase() {
        return randomBase;
    }
    private void setRandomBase(Random value) {
        randomBase = value;
    }
 
    /// <summary>
    /// Die Position des Insekts auf dem Spielfeld.
    /// </summary>
    public CoreCoordinate getCoordinateBase() {
        return koordinate;
    }
    void setCoordinateBase(CoreCoordinate value) {
        koordinate = value;
    }

    /// <summary>
    /// Der abstrakte Insekt-Basiskonstruktor.
    /// </summary>
    /// <param name="colony">Das Volk zu dem das neue Insekt geh�rt.</param>
    /// <param name="vorhandeneInsekten">Hier unbenutzt!</param>
    void init(CoreColony colony, Random random, HashMap<String, Integer> vorhandeneInsekten)
    {
        id = neueId;
        neueId++;

        this.colony = colony;
        this.randomBase = random;

        koordinate.setRichtung(randomBase.nextInt(359));

        // Zuf�llig auf dem Spielfeldrand platzieren.
        if (colony.getAntHills().size() == 0) // Am oberen oder unteren Rand platzieren.
        {
            if (randomBase.nextInt(2) == 0)
            {
                koordinate.setX(randomBase.nextInt(colony.getPlayground().getWidth()) * SimulationEnvironment.PLAYGROUND_UNIT);
                if (randomBase.nextInt(2) == 0)
                {
                    koordinate.setY(0);
                }
                else
                {
                    koordinate.setY(colony.getPlayground().getHeight() * SimulationEnvironment.PLAYGROUND_UNIT);
                }
            }

                // Am linken oder rechten Rand platzieren.
            else
            {
                if (randomBase.nextInt(2) == 0)
                {
                    koordinate.setX(0);
                }
                else
                {
                    koordinate.setX(colony.getPlayground().getWidth() * SimulationEnvironment.PLAYGROUND_UNIT);
                }
                koordinate.setY(randomBase.nextInt(colony.getPlayground().getHeight())*SimulationEnvironment.PLAYGROUND_UNIT);
            }
        }

            // In einem zuf�lligen Bau platzieren.
        else
        {
            int i = randomBase.nextInt(colony.getAntHills().size());
            koordinate.setX(colony.getAntHills().get(i).getCoordinateBase().getX() +
                            SimulationEnvironment.cosinus(
                                colony.getAntHills().get(i).getCoordinateBase().getRadius(), koordinate.getRichtung()));
            koordinate.setY(colony.getAntHills().get(i).getCoordinateBase().getY() +
                            SimulationEnvironment.sinus(
                                colony.getAntHills().get(i).getCoordinateBase().getRadius(), koordinate.getRichtung()));
        }
    }

    /// <summary>
    /// Gibt an, ob weitere Insekten ben�tigt werden, um ein St�ck Obst zu
    /// tragen.
    /// </summary>
    /// <param name="obst">Obst</param>
    /// <returns></returns>
    boolean brauchtNochTraeger(CoreFruit obst)
    {
        return obst.brauchtNochTraeger(colony);
    }

    /// <summary>
    /// Dreht das Insekt um den angegebenen Winkel. Die maximale Drehung betr�gt
    /// -180 Grad (nach links) bzw. 180 Grad (nach rechts). Gr��ere Werte werden
    /// abgeschnitten.
    /// </summary>
    /// <param name="winkel">Winkel</param>
    void dreheUmWinkelBase(int winkel)
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        setRestWinkelBase(winkel);
    }

    /// <summary>
    /// Dreht das Insekt in die angegebene Richtung (Grad).
    /// </summary>
    /// <param name="richtung">Richtung</param>
    void dreheInRichtungBase(int richtung)
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        dreheInRichtung(richtung);
    }

    private void dreheInRichtung(int richtung)
    {
        setRestWinkelBase(richtung - koordinate.getRichtung());
    }

    /// <summary>
    /// Dreht das Insekt in die Richtung des angegebenen Ziels.
    /// </summary>
    /// <param name="ziel">Ziel</param>
    void dreheZuZielBase(ICoordinate ziel)
    {
        dreheInRichtungBase(CoreCoordinate.bestimmeRichtung(this, ziel));
    }

    /// <summary>
    /// Dreht das Insekt um 180 Grad.
    /// </summary>
    void dreheUmBase()
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        if (restWinkel > 0)
        {
            restWinkel = 180;
        }
        else
        {
            restWinkel = -180;
        }
    }

    /// <summary>
    /// L�sst das Insekt unbegrenzt geradeaus gehen.
    /// </summary>
    void geheGeradeausBase()
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        restStreckeI = Integer.MAX_VALUE;
    }

    /// <summary>
    /// L�sst das Insekt die angegebene Entfernung in Schritten geradeaus gehen.
    /// </summary>
    /// <param name="entfernung">Die Entfernung.</param>
    void geheGeradeausBase(int entfernung)
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        restStreckeI = entfernung * SimulationEnvironment.PLAYGROUND_UNIT;
    }

    /// <summary>
    /// L�sst das Insekt auf ein Ziel zugehen. Das Ziel darf sich bewegen.
    /// Wenn das Ziel eine Wanze ist, wird dieser angegriffen.
    /// </summary>
    /// <param name="ziel">Das Ziel.</param>
    void geheZuZielBase(ICoordinate ziel)
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        setZielBase(ziel);
    }

    /// <summary>
    /// L�sst das Insekt ein Ziel angreifen. Das Ziel darf sich bewegen.
    /// In der aktuellen Version kann das Ziel nur eine Wanze sein.
    /// </summary>
    /// <param name="ziel">Ziel</param>
    void greifeAnBase(CoreInsect ziel)
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        setZielBase(ziel);
    }

    /// <summary>
    /// L�sst das Insekt von der aktuellen Position aus entgegen der Richtung zu
    /// einer Quelle gehen. Wenn die Quelle ein Insekt eines anderen Volkes ist,
    /// befindet sich das Insekt auf der Flucht.
    /// </summary>
    /// <param name="quelle">Die Quelle.</param> 
    void geheWegVonBase(ICoordinate quelle)
    {
        dreheInRichtungBase(CoreCoordinate.bestimmeRichtung(this, quelle) + 180);
        geheGeradeausBase();
    }

    /// <summary>
    /// L�sst das Insekt von der aktuellen Position aus die angegebene
    /// Entfernung in Schritten entgegen der Richtung zu einer Quelle gehen.
    /// Wenn die Quelle ein Insekt eines anderen Volkes ist, befindet sich das
    /// Insekt auf der Flucht.
    /// </summary>
    /// <param name="quelle">Die Quelle.</param> 
    /// <param name="entfernung">Die Entfernung in Schritten.</param>
    void geheWegVonBase(ICoordinate quelle, int entfernung)
    {
        dreheInRichtungBase(CoreCoordinate.bestimmeRichtung(this, quelle) + 180);
        geheGeradeausBase(entfernung);
    }

    /// <summary>
    /// L�sst das Insekt zum n�chsten Bau gehen.
    /// </summary>
    void geheZuBauBase()
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        int aktuelleEntfernung;
        int gemerkteEntfernung = Integer.MAX_VALUE;
        CoreAnthill gemerkterBau = null;
        for (CoreAnthill bau : colony.getAntHills())
        {
            aktuelleEntfernung = CoreCoordinate.bestimmeEntfernungI(getCoordinateBase(), bau.getCoordinateBase());
            if (aktuelleEntfernung < gemerkteEntfernung)
            {
                gemerkterBau = bau;
                gemerkteEntfernung = aktuelleEntfernung;
            }
        }
        geheZuZielBase(gemerkterBau);
    }

    /// <summary>
    /// L�sst das Insekt anhalten. Dabei geht sein Ziel verloren.
    /// </summary>
    void bleibStehenBase()
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        setZielBase(null);
        restStreckeI = 0;
        restWinkel = 0;
    }

    /// <summary>
    /// L�sst das Insekt Zucker von einem Zuckerhaufen nehmen.
    /// </summary>
    /// <param name="zucker">Zuckerhaufen</param>
    void nimmBase(CoreSugar zucker)
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        int entfernung = CoreCoordinate.bestimmeEntfernungI(getCoordinateBase(), zucker.getCoordinateBase());
        if (entfernung <= SimulationEnvironment.PLAYGROUND_UNIT)
        {
            int menge = Math.min(getMaximaleLastBase() - aktuelleLast, zucker.getMenge());
            setAktuelleLastBase(getAktuelleLastBase()+menge);
            zucker.setMenge(zucker.getMenge()-menge);
        }
        else
        {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /// <summary>
    /// L�sst das Insekt ein Obstst�ck nehmen.
    /// </summary>
    /// <param name="obst">Das Obstst�ck.</param>
    void nimmBase(CoreFruit obst)
    {
        if (!nimmBefehleEntgegen)
        {
            return;
        }
        if (getGetragenesObstBase() == obst)
        {
            return;
        }
        if (getGetragenesObstBase() != null)
        {
            lasseNahrungFallenBase();
        }
        int entfernung = CoreCoordinate.bestimmeEntfernungI(getCoordinateBase(), obst.getCoordinateBase());
        if (entfernung <= SimulationEnvironment.PLAYGROUND_UNIT)
        {
            bleibStehenBase();
            setGetragenesObstBase(obst);
            obst.getTragendeInsekten().add(this);
            setAktuelleLastBase(colony.getLast()[casteIndexBase]);
        }
    }

    /// <summary>
    /// L�sst das Insekt die aktuell getragene Nahrung fallen. Das Ziel des
    /// Insekts geht dabei verloren.
    /// </summary>
    void lasseNahrungFallenBase()
    {
        if (!isNimmBefehleEntgegen())
        {
            return;
        }
        setAktuelleLastBase(0);
        setZielBase(null);
        if (getGetragenesObstBase() != null)
        {
            getGetragenesObstBase().getTragendeInsekten().remove(this);
            setGetragenesObstBase(null);
        }
    }

    /// <summary>
    /// L�sst die Ameise eine Markierung spr�hen. Die Markierung enth�lt die
    /// angegebene Information und breitet sich um die angegebene Anzahl an
    /// Schritten weiter aus. Je weiter sich eine Markierung ausbreitet, desto
    /// k�rzer bleibt sie aktiv.
    /// </summary>
    /// <param name="information">Die Information.</param>
    /// <param name="ausbreitung">Die Ausbreitung in Schritten.</param>
    void sprueheMarkierungBase(int information, int ausbreitung) throws AiException
    {
        if (!isNimmBefehleEntgegen())
        {
            return;
        }

        // Check for unsupported markersize
        if (ausbreitung < 0)
        {
            throw new AiException(String.format("{0}: {1}", colony.getPlayer().getGuid(),
                bundle.getString("SimulationCoreNegativeMarkerSize")));
        }

        CoreMarker markierung = new CoreMarker(koordinate, ausbreitung, colony.getId());
        markierung.setInformation(information);
        colony.getNewMarker().add(markierung);
        smelledMarker.add(markierung);
    }

    /// <summary>
    /// L�sst die Ameise eine Markierung spr�hen. Die Markierung enth�lt die
    /// angegebene Information und breitet sich nicht aus. So hat die Markierung
    /// die maximale Lebensdauer.
    /// </summary>
    /// <param name="information">Die Information.</param>
    void sprueheMarkierungBase(int information) throws AiException
    {
        if (!isNimmBefehleEntgegen())
        {
            return;
        }
        sprueheMarkierungBase(information, 0);
    }

    /// <summary>
    /// Berechnet die Bewegung des Insekts.
    /// </summary>
    void bewegen()
    {
        reached = false;

        // Insekt dreht sich.
        if (restWinkel != 0)
        {
            // Zielwinkel wird erreicht.
            if (Math.abs(restWinkel) < colony.getDrehgeschwindigkeit()[casteIndexBase])
            {
                koordinate.setRichtung(koordinate.getRichtung() + restWinkel);
                restWinkel = 0;
            }

                // Insekt dreht sich nach rechts.
            else if (restWinkel >= colony.getDrehgeschwindigkeit()[casteIndexBase])
            {
                koordinate.setRichtung(koordinate.getRichtung() + colony.getDrehgeschwindigkeit()[casteIndexBase]);
                setRestWinkelBase(getRestWinkelBase() - colony.getDrehgeschwindigkeit()[casteIndexBase]);
            }

                // Insekt dreht sich nach links.
            else if (restWinkel <= -colony.getDrehgeschwindigkeit()[casteIndexBase])
            {
                koordinate.setRichtung(koordinate.getRichtung() - colony.getDrehgeschwindigkeit()[casteIndexBase]);
                setRestWinkelBase(getRestWinkelBase() + colony.getDrehgeschwindigkeit()[casteIndexBase]);
            }
        }

            // Insekt geht.
        else if (restStreckeI > 0)
        {
            if (getGetragenesObstBase() == null)
            {
                int strecke = Math.min(restStreckeI, aktuelleGeschwindigkeitI);

                restStreckeI -= strecke;
                zurueckgelegteStreckeI += strecke;
                koordinate.setX(koordinate.getX()+SimulationEnvironment.Cos[strecke][koordinate.getRichtung()]);
                koordinate.setY(koordinate.getY()+SimulationEnvironment.Sin[strecke][koordinate.getRichtung()]);
            }
        }

            // Insekt geht auf Ziel zu.
        else if (ziel != null)
        {
            int entfernungI;

            if (getZielBase().getClass() == CoreMarker.class)
            {
                entfernungI = CoreCoordinate.bestimmeEntfernungDerMittelpunkteI(koordinate, ziel.getCoordinateBase());
            }
            else
            {
                entfernungI = CoreCoordinate.bestimmeEntfernungI(koordinate, ziel.getCoordinateBase());
            }

            reached = entfernungI <= SimulationEnvironment.PLAYGROUND_UNIT;
            if (!reached)
            {
                int richtung = CoreCoordinate.bestimmeRichtung(koordinate, ziel.getCoordinateBase());

                // Ziel ist in Sichtweite oder Insekt tr�gt Obst.
                if (entfernungI < colony.getSichtweiteI()[casteIndexBase] || getragenesObst != null)
                {
                    restStreckeI = entfernungI;
                }

                    // Ansonsten Richtung verf�lschen.
                else
                {
                    richtung += getRandomBase().nextInt(36)-18;
                    restStreckeI = colony.getSichtweiteI()[casteIndexBase];
                }

                dreheInRichtung(richtung);
            }
        }

        // Koordinaten links begrenzen.
        if (koordinate.getX() < 0)
        {
            koordinate.setX(-koordinate.getX());
            if (koordinate.getRichtung() > 90 && koordinate.getRichtung() <= 180)
            {
                koordinate.setRichtung(180 - koordinate.getRichtung());
            }
            else if (koordinate.getRichtung() > 180 && koordinate.getRichtung() < 270)
            {
                koordinate.setRichtung(540 - koordinate.getRichtung());
            }
        }

        // Koordinaten rechts begrenzen.
        else if (koordinate.getX() > colony.getBreiteI())
        {
            koordinate.setX(colony.getBreiteI2() - koordinate.getX());
            if (koordinate.getRichtung() >= 0 && koordinate.getRichtung() < 90)
            {
                koordinate.setRichtung(180 - koordinate.getRichtung());
            }
            else if (koordinate.getRichtung() > 270 && koordinate.getRichtung() < 360)
            {
                koordinate.setRichtung(540 - koordinate.getRichtung());
            }
        }

        // Koordinaten oben begrenzen.
        if (koordinate.getY() < 0)
        {
            koordinate.setY(-koordinate.getY());
            if (koordinate.getRichtung() > 180 && koordinate.getRichtung() < 360)
            {
                koordinate.setRichtung(360 - koordinate.getRichtung());
            }
        }

            // Koordinaten unten begrenzen.
        else if (koordinate.getY() > colony.getHoeheI())
        {
            koordinate.setY(colony.getHoeheI2() - koordinate.getY());
            if (koordinate.getRichtung() > 0 && koordinate.getRichtung() < 180)
            {
                koordinate.setRichtung(360 - koordinate.getRichtung());
            }
        }
    }


    /// <summary>
    /// Die aktuelle Geschwindigkeit des Insekts in der internen Einheit.
    /// </summary>
    int aktuelleGeschwindigkeitI;

    /// <summary>
    /// Die aktuelle Geschwindigkeit des Insekts in Schritten. Wenn das Insekt
    /// seine maximale Last tr�gt, halbiert sich seine Geschwindigkeit.
    /// </summary>
    int getAktuelleGeschwindigkeitBase() {
       return aktuelleGeschwindigkeitI / SimulationEnvironment.PLAYGROUND_UNIT;
    }

    /// <summary>
    /// Die maximale Geschwindigkeit des Insekts.
    /// </summary>
    int getMaximaleGeschwindigkeitBase() {
        return colony.getGeschwindigkeitI()[casteIndexBase] / SimulationEnvironment.PLAYGROUND_UNIT;
    }

    /// <summary>
    /// Die Drehgeschwindigkeit des Insekts in Grad pro Runde.
    /// </summary>
    int getDrehgeschwindigkeitBase()
    {
        return colony.getDrehgeschwindigkeit()[casteIndexBase];
    }

    private int aktuelleLast = 0;

    /// <summary>
    /// Die Last die die Ameise gerade tr�gt.
    /// </summary>
    int getAktuelleLastBase() {
        return aktuelleLast;
    }
    void setAktuelleLastBase(int value) {
        aktuelleLast = value >= 0 ? value : 0;
        aktuelleGeschwindigkeitI = colony.getGeschwindigkeitI()[casteIndexBase];
        aktuelleGeschwindigkeitI -= aktuelleGeschwindigkeitI * aktuelleLast / colony.getLast()[casteIndexBase] / 2;
    }

    /// <summary>
    /// Die maximale Last die das Insekt tragen kann.
    /// </summary>
    int getMaximaleLastBase() {
        return colony.getLast()[casteIndexBase];
    }

    /// <summary>
    /// Die Sichtweite des Insekts in Schritten.
    /// </summary>
    int getSichtweiteBase()
    {
        return colony.getSichtweiteI()[casteIndexBase] / SimulationEnvironment.PLAYGROUND_UNIT;
    }

    /// <summary>
    /// Die Sichtweite des Insekts in der internen Einheit.
    /// </summary>
    int getSichtweiteI()
    {
        return colony.getSichtweiteI()[casteIndexBase];
    }


    /// <summary>
    /// Die Reichweite des Insekts in Schritten.
    /// </summary>
    int getReichweiteBase()
    {
        return colony.getReichweiteI()[casteIndexBase] / SimulationEnvironment.PLAYGROUND_UNIT;
    }

    /// <summary>
    /// Die Reichweite des Insekts in der internen Einheit.
    /// </summary>
    int getReichweiteI()
    {
        return colony.getReichweiteI()[casteIndexBase];
    }

    private int aktuelleEnergie;

    /// <summary>
    /// Die verbleibende Energie des Insekts.
    /// </summary>
    int getAktuelleEnergieBase()
    {
        return aktuelleEnergie;
    }
    void setAktuelleEnergieBase(int value) {
        aktuelleEnergie = value >= 0 ? value : 0;
    }

    /// <summary>
    /// Die maximale Energie des Insetks.
    /// </summary>
    int getMaximaleEnergieBase()
    {
        return colony.getEnergie()[casteIndexBase];
    }

    private int angriff;

    /// <summary>
    /// Die Angriffst�rke des Insekts. Wenn das Insekt Last tr�gt ist die
    /// Angriffst�rke gleich Null.
    /// </summary>
    int getAngriffBase() {
        return aktuelleLast == 0 ? angriff : 0;
    }
    void setAngriffBase(int value) {
        angriff = value >= 0 ? value : 0;
    }

    String debugMessage;

    void denkeCore(String message) {
        debugMessage = message.length() > 100 ? message.substring(0, 100) : message;
    }
}
