package AntMe.Simulation;

import AntMe.SharedComponents.States.AntState;
import AntMe.SharedComponents.States.LoadType;
import AntMe.SharedComponents.States.TargetType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

/// <summary>
/// Abstrakte Basisklasse für alle Ameisen.
/// </summary>
/// <author>Wolfgang Gallo (wolfgang@antme.net)</author>
public abstract class CoreAnt extends CoreInsect {

    ResourceBundle bundle = ResourceBundle.getBundle("package.Resource", Locale.getDefault());

    @Override
    /*internal*/ void init(CoreColony colony, Random random, HashMap<String, Integer> availableInsects)
    {
        super.init(colony, random, availableInsects);

        getKoordinate().setRadius(2);

        // Bestimme die Kaste der neuen Ameise.
        int casteIndex = -1;
        String casteName = "";
        if (availableInsects != null)
        {
            casteName = bestimmeKasteBase(availableInsects);
            for (int i = 0; i < colony.getPlayer().getCastes().size(); i++)
            {
                if (colony.getPlayer().getCastes().get(i).getName() == casteName)
                {
                    casteIndex = i;
                    break;
                }
            }
        }

        // Check, if caste is available
        if (casteIndex == -1)
        {
            throw new UnsupportedOperationException(String.format(bundle.getString("SimulationCoreChooseWrongCaste"), casteName));
        }

        // Setze die von der Kaste abhängigen Werte.
        setCasteIndexBase(casteIndex);
        setAktuelleEnergieBase(colony.getEnergie()[casteIndex]);
        aktuelleGeschwindigkeitI = colony.getGeschwindigkeitI()[casteIndex];
        setAngriffBase(colony.getAngriff()[casteIndex]);
    }

    /// <summary>
    /// Bestimmt die Kaste einer neuen Ameise.
    /// </summary>
    /// <param name="anzahl">Die Anzahl der von jeder Klaste bereits vorhandenen Ameisen.</param>
    /// <returns>Der Name der Kaste der Ameise.</returns>
    String bestimmeKasteBase(HashMap<String, Integer> anzahl)
    {
        return "";
    }

    /// <summary>
    /// Erzeugt ein AmeiseZustand-Objekt mit den aktuellen Daten der Ameise.
    /// </summary>
    AntState erzeugeInfo()
    {
        AntState zustand = new AntState(getColony().getId(), getId());

        zustand.setCasteId(getCasteIndexBase());
        zustand.setPositionX(getCoordinateBase().getX() / SimulationEnvironment.PLAYGROUND_UNIT);
        zustand.setPositionY(getCoordinateBase().getY() / SimulationEnvironment.PLAYGROUND_UNIT);
        zustand.setViewRange(getSichtweiteBase());
        zustand.setDebugMessage(debugMessage);
        debugMessage = "";
        zustand.setDirection(getCoordinateBase().getRichtung());
        if (getZielBase() != null)
        {
            zustand.setTargetPositionX(getZielBase().getCoordinateBase().getX() / SimulationEnvironment.PLAYGROUND_UNIT);
            zustand.setTargetPositionY(getZielBase().getCoordinateBase().getY() / SimulationEnvironment.PLAYGROUND_UNIT);
        }
        else
        {
            zustand.setTargetPositionX(0);
            zustand.setTargetPositionY(0);
        }
        zustand.setLoad(getAktuelleLastBase());
        if (getAktuelleLastBase() > 0)
            if (getGetragenesObstBase() != null)
                zustand.setLoadType(LoadType.Fruit);
            else
                zustand.setLoadType(LoadType.Sugar);
        zustand.setVitality(getAktuelleEnergieBase());

        if (getZielBase().getClass() == CoreAnthill.class)
            zustand.setTargetType(TargetType.Anthill);
        else if (getZielBase().getClass() == CoreSugar.class)
            zustand.setTargetType(TargetType.Sugar);
        else if (getZielBase().getClass() == CoreFruit.class)
            zustand.setTargetType(TargetType.Fruit);
        else if (getZielBase().getClass() == CoreBug.class)
            zustand.setTargetType(TargetType.Bug);
        else if (getZielBase().getClass() == CoreMarker.class)
            zustand.setTargetType(TargetType.Marker);
        else if (getZielBase().getClass() == CoreAnt.class)
            zustand.setTargetType(TargetType.Ant);
        else
            zustand.setTargetType(TargetType.None);

        return zustand;
    }

    private boolean istMuede;

    /// <summary>
    /// Gibt an, ob die Ameise müde ist.
    /// </summary>
    boolean IsMuedeBase()
    {
        return istMuede;
    }
    void SetIstMuede(boolean value) {
        istMuede = value;
    }

    /// <summary>
    /// Wird wiederholt aufgerufen, wenn der die Ameise nicht weiss wo sie
    /// hingehen soll.
    /// </summary>
    void wartetBase() { }

    /// <summary>
    /// Wird einmal aufgerufen, wenn die Ameise ein Drittel ihrer maximalen 
    /// Reichweite überschritten hat.
    /// </summary>
    void wirdMüdeBase() { }


    /// <summary>
    /// Wird wiederholt aufgerufen, wenn die Ameise mindestens einen
    /// Zuckerhaufen sieht.
    /// </summary>
    /// <param name="zucker">Der nächstgelegene Zuckerhaufen.</param>
    void siehtBase(CoreSugar zucker) { }

    /// <summary>
    /// Wird wiederholt aufgerufen, wenn die Ameise mindstens ein
    /// Obststück sieht.
    /// </summary>
    /// <param name="obst">Das nächstgelegene Obststück.</param>
    void siehtBase(CoreFruit obst) { }

    /// <summary>
    /// Wird einmal aufgerufen, wenn die Ameise einen Zuckerhaufen als Ziel
    /// hat und bei diesem ankommt.
    /// </summary>
    /// <param name="zucker">Der Zuckerhaufen.</param>
    void zielErreichtBase(CoreSugar zucker) { }

    /// <summary>
    /// Wird einmal aufgerufen, wenn die Ameise ein Obststück als Ziel hat und
    /// bei diesem ankommt.
    /// </summary>
    /// <param name="obst">Das Obstück.</param>
    void zielErreichtBase(CoreFruit obst) { }

    /// <summary>
    /// Wird einmal aufgerufen, wenn die Ameise eine Markierung des selben
    /// Volkes riecht. Einmal gerochene Markierungen werden nicht erneut
    /// gerochen.
    /// </summary>
    /// <param name="markierung">Die nächste neue Markierung.</param>
    void riechtFreundBase(CoreMarker markierung) { }

    /// <summary>
    /// Wird wiederholt aufgerufen, wenn die Ameise mindstens eine Ameise des
    /// selben Volkes sieht.
    /// </summary>
    /// <param name="ameise">Die nächstgelegene befreundete Ameise.</param>
    void siehtFreundBase(CoreAnt ameise) { }

    /// <summary>
    /// Wird wiederholt aufgerufen, wenn die Ameise mindestens eine Ameise verbündeter
    /// Völker sieht.
    /// </summary>
    /// <param name="ameise"></param>
    void siehtVerbündetenBase(CoreAnt ameise) { }

    /// <summary>
    /// Wird wiederholt aufgerufen, wenn die Ameise mindestens eine Wanze
    /// sieht.
    /// </summary>
    /// <param name="wanze">Die nächstgelegene Wanze.</param>
    void siehtFeindBase(CoreBug wanze) { }

    /// <summary>
    /// Wird wiederholt aufgerufen, wenn die Ameise mindestens eine Ameise eines
    /// anderen Volkes sieht.
    /// </summary>
    /// <param name="ameise">Die nächstgelegen feindliche Ameise.</param>
    void siehtFeindBase(CoreAnt ameise) { }

    /// <summary>
    /// Wird wiederholt aufgerufen, wenn die Ameise von einer Wanze angegriffen
    /// wird.
    /// </summary>
    /// <param name="wanze">Die angreifende Wanze.</param>
    void wirdAngegriffenBase(CoreBug wanze) { }

    /// <summary>
    /// Wird wiederholt aufgerufen in der die Ameise von einer Ameise eines
    /// anderen Volkes Ameise angegriffen wird.
    /// </summary>
    /// <param name="ameise">Die angreifende feindliche Ameise.</param>
    void wirdAngegriffenBase(CoreAnt ameise) { }

    /// <summary>
    /// Wird einmal aufgerufen, wenn die Ameise gestorben ist.
    /// </summary>
    /// <param name="todesArt">Die Todesart der Ameise</param>
    void istGestorbenBase(CoreKindOfDeath todesArt) { }

    /// <summary>
    /// Wird unabhängig von äußeren Umständen in jeder Runde aufgerufen.
    /// </summary>
    void tickBase() { }

}
