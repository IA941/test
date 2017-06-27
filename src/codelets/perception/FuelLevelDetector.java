package codelets.perception;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import ws3dproxy.model.Creature;

public class FuelLevelDetector extends Codelet {

    private final Creature creature;

    private MemoryObject fuelLevel;


    public FuelLevelDetector(Creature creature) {
        setTimeStep(30);
        this.creature = creature;
    }


    @Override
    public void accessMemoryObjects() {
        this.fuelLevel = (MemoryObject) this.getOutput("FUEL");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        creature.updateState();
        fuelLevel.setI(creature.getFuel());
    }
}
