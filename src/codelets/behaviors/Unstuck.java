package codelets.behaviors;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import model.Helper;
import model.SimulationStatus;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;
import ws3dproxy.model.World;
import ws3dproxy.model.WorldPoint;

import java.util.ArrayList;
import java.util.List;

public class Unstuck extends Codelet {

    private final List<WorldPoint> worldPoints = new ArrayList<>();

    private final Creature creature;

    private MemoryObject knownApples;

    private MemoryObject knownJewels;


    public Unstuck(final Creature creature) {
        setTimeStep(1000);
        this.creature = creature;
    }


    @Override
    public void accessMemoryObjects() {
        this.knownApples = (MemoryObject) getInput("KNOWN_APPLES");
        this.knownJewels = (MemoryObject) getInput("KNOWN_JEWELS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        worldPoints.add(creature.getPosition());

        if (worldPoints.size() == 4) {
            worldPoints.remove(0);
        } else {
            return;
        }

        if (worldPoints.get(0).getX() == worldPoints.get(1).getX()
                && worldPoints.get(0).getX() == worldPoints.get(1).getX()
                && worldPoints.get(0).getY() == worldPoints.get(1).getY()
                && worldPoints.get(0).getY() == worldPoints.get(1).getY()) {
            try {
                List<Thing> things = World.getWorldEntities();

                for (Thing thing : new ArrayList<>(things)) {
                    if (thing.withinBoundingArea(creature.getPosition().getX(), creature.getPosition().getY())) {
                        creature.putInSack(thing.getName());

                        if (!Helper.removeByName((List<Thing>) knownApples.getI(), thing.getName())) {
                            Helper.removeByName((List<Thing>) knownJewels.getI(), thing.getName());
                        }
                    }
                }
            } catch (CommandExecException e) {
                e.printStackTrace();
            }
        }
    }
}
