package AntMe.Simulation;

import java.util.Locale;
import java.util.ResourceBundle;

import AntMe.SharedComponents.States.CasteState;

public class CasteInfo {
    /// <summary>
    /// Der Angriffmodifikator der Kaste.
    /// </summary>
    private int attack = 0;

    /// <summary>
    /// Der Drehgeschwindigkeitmodifikator der Kaste.
    /// </summary>
    private int rotationSpeed = 0;

    /// <summary>
    /// Der Energiemodifikator der Kaste.
    /// </summary>
    private int energy = 0;

    /// <summary>
    /// Der Geschwindigkeitmodifikator der Kaste.
    /// </summary>
    private int speed = 0;

    /// <summary>
    /// Der Lastmodifikator der Kaste.
    /// </summary>
    private int load = 0;

    /// <summary>
    /// Der Name der Kaste.
    /// </summary>
    private String name = "";

    /// <summary>
    /// Der Reichweitenmodifikator der Kaste.
    /// </summary>
    private int range = 0;

    /// <summary>
    /// Der Sichtweitenmodifikator der Kaste.
    /// </summary>
    private int viewRange = 0;

    ResourceBundle bundle = ResourceBundle.getBundle("package.Resource", Locale.getDefault());

    /// <summary>
    /// Pr�ft, ob diese Ameisenkaste den Regeln entspricht
    /// </summary>
    /// <throws>RuleViolationException</throws>
    public void rulecheck(String aiName) throws RuleViolationException
    {
        // Ignoriere die Kaste, wenn er keinen Namen hat.
        if (name.isEmpty())
        {
            throw new RuleViolationException(
                String.format(bundle.getString("SimulationCoreCasteRuleNoName"), aiName));
        }

        // Pr�fen, ob der Geschindwigkeitsmodifikator im Rahmen ist
        if (speed < SimulationSettings.getCustom().CasteSettings.getMinIndex() ||
            speed > SimulationSettings.getCustom().CasteSettings.getMaxIndex())
        {
            throw new RuleViolationException(
                String.format(
                    bundle.getString("SimulationCoreCasteRuleSpeedFailed"), name, aiName));
        }

        // Pr�fen, ob der Drehgeschwindigkeitsmodifikator im Rahmen ist
        if (rotationSpeed < SimulationSettings.getCustom().CasteSettings.getMinIndex() ||
            rotationSpeed > SimulationSettings.getCustom().CasteSettings.getMaxIndex())
        {
            throw new RuleViolationException(
                String.format(
                    bundle.getString("SimulationCoreCasteRuleRotationSpeedFailed"),
                    name,
                    aiName));
        }

        // Pr�fen, ob der Lastmodifikator im Rahmen ist
        if (load < SimulationSettings.getCustom().CasteSettings.getMinIndex() ||
            load > SimulationSettings.getCustom().CasteSettings.getMaxIndex())
        {
            throw new RuleViolationException(
                String.format(bundle.getString("SimulationCoreCasteRuleLoadFailed"), name, aiName));
        }

        // Pr�fen, ob der Sichtweitemodifikator im Rahmen ist
        if (viewRange < SimulationSettings.getCustom().CasteSettings.getMinIndex() ||
            viewRange > SimulationSettings.getCustom().CasteSettings.getMaxIndex())
        {
            throw new RuleViolationException(
                String.format(
                    bundle.getString("SimulationCoreCasteRuleViewRangeFailed"), name, aiName));
        }

        // Pr�fen, ob der Riechweitemodifikator im Rahmen ist
        if (range < SimulationSettings.getCustom().CasteSettings.getMinIndex() ||
            range > SimulationSettings.getCustom().CasteSettings.getMaxIndex())
        {
            throw new RuleViolationException(
                String.format(
                    bundle.getString("SimulationCoreCasteRuleRangeFailed"), name, aiName));
        }

        // Pr�fen, ob der Energiemodifikator im Rahmen ist
        if (energy < SimulationSettings.getCustom().CasteSettings.getMinIndex() ||
            energy > SimulationSettings.getCustom().CasteSettings.getMaxIndex())
        {
            throw new RuleViolationException(
                String.format(bundle.getString("SimulationCoreCasteRuleEnergyFailed"), name, aiName));
        }

        // Pr�fen, ob der Angriffsmodifikator im Rahmen ist
        if (attack < SimulationSettings.getCustom().CasteSettings.getMinIndex() ||
            attack > SimulationSettings.getCustom().CasteSettings.getMaxIndex())
        {
            throw new RuleViolationException(
                String.format(bundle.getString("SimulationCoreCasteRuleAttackFailed"), name, aiName));
        }

        // Pr�fen, ob die Eigenschaftssumme stimmt
        if (speed +
            rotationSpeed +
            load +
            viewRange +
            range +
            energy +
            attack > SimulationSettings.getCustom().CasteSettings.Sum)
        {
            throw new RuleViolationException(
                String.format(bundle.getString("SimulationCoreCasteRuleSumFailed"), name, aiName));
        }
    }

    /// <summary>
    /// Gibt an, ob es sich bei dieser Ameisenkaste um die Standard-Kaste handelt
    /// </summary>
    /// <returns>Standardwert</returns>
    public boolean isEmpty()
    {
        return name.isEmpty() &&
                attack == 0 &&
                rotationSpeed == 0 &&
                energy == 0 &&
                speed == 0 &&
                load == 0 &&
                range == 0 &&
                viewRange == 0;
    }

    /// <summary>
    /// Erzeugt ein CasteState-Objekt.
    /// </summary>
    /// <returns></returns>
    public CasteState createState(int colonyId, int id)
    {
        CasteState state = new CasteState(colonyId, id);
        state.setName(name);
        state.setSpeedModificator((byte)speed);
        state.setRotationSpeedModificator((byte)rotationSpeed);
        state.setLoadModificator((byte)load);
        state.setViewRangeModificator((byte)viewRange);
        state.setRangeModificator((byte)range);
        state.setVitalityModificator((byte)energy);
        state.setAttackModificator((byte)attack);
        return state;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(int rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getViewRange() {
        return viewRange;
    }

    public void setViewRange(int viewRange) {
        this.viewRange = viewRange;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

}
