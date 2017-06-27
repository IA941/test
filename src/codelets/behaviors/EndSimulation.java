package codelets.behaviors;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Mind;
import model.Helper;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;

public class EndSimulation extends Codelet {

    private final Creature creature;

    private final Mind mind;


    public EndSimulation(final Creature creature, final Mind mind) {
        setTimeStep(1000);
        this.creature = creature;
        this.mind = mind;
    }


    @Override
    public void accessMemoryObjects() {

    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if (Helper.hasCompletedLeaflets(creature.getLeaflets())) {
            System.out.println("Ending simulation because all leaflets have been completed...");
            try {
                creature.stop();
                creature.getLeaflets().forEach(leaflet -> {
                    try {
                        creature.deliverLeaflet(leaflet.getID().toString());
                    } catch (CommandExecException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            mind.shutDown();
        }
    }
}
