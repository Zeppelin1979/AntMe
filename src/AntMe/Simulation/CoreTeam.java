package AntMe.Simulation;

import java.util.UUID;

import AntMe.SharedComponents.States.TeamState;

public class CoreTeam {
    private int id;
    int getId() {
        return id;
    }
    private UUID guid;
    UUID getGuid() {
        return guid;
    }
    private String name;
    String getName() {
        return name;
    }

    private CoreColony[] colonies;
    CoreColony[] getColonies() {
        return colonies;
    }

    public CoreTeam(int id, UUID guid, String name)
    {
        this.id = id;
        this.guid = guid;
        this.name = name;
    }

    public TeamState createState()
    {
        TeamState state = new TeamState(id, guid, name);

        for (int i = 0; i < colonies.length; i++)
        {
            colonies[i].getStatistic().CurrentAntCount = colonies[i].getInsects().size();
            state.getColonyStates().add(colonies[i].erzeugeInfo());
        }

        return state;
    }

    public void createColonies(int size) {
        colonies = new CoreColony[size];
    }

}
