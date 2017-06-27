package model;

import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;

import java.util.List;

public class Helper {

    public static boolean removeByName(final List<Thing> things, final String targetThingName) {
        synchronized (things) {
            return things.removeIf(thing -> thing.getName().equals(targetThingName));
        }
    }

    public static boolean hasCompletedLeaflets(final List<Leaflet> leaflets) {
        synchronized (leaflets) {
            return leaflets.stream().allMatch(
                    leaflet -> leaflet.getWhatToCollect().values().stream().allMatch(
                            itemsToCollect -> (int) itemsToCollect == 0
                    )
            );
        }
    }
}
