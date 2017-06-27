/*****************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *    Klaus Raizer, Andre Paraense, Ricardo Ribeiro Gudwin
 * Altered by:
 *    Fabio Tanada
 *****************************************************************************/

package codelets.behaviors;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import memory.CreatureInnerSense;
import model.Helper;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class GetClosestJewel extends Codelet {
    private MemoryObject closestJewelMO;
    private MemoryObject innerSenseMO;
    private MemoryObject knownJewelsMO;
    private int reachDistance;
    private MemoryObject handsMO;
    private Thing closestJewel;
    private List<Thing> known;
    private MemoryObject leafletsMO = null;
    private MemoryObject fuelMO = null;

    public GetClosestJewel(int reachDistance) {
        setTimeStep(50);
        this.reachDistance = reachDistance;
    }

    @Override
    public void accessMemoryObjects() {
        closestJewelMO = (MemoryObject) this.getInput("CLOSEST_JEWEL");
        innerSenseMO = (MemoryObject) this.getInput("INNER");
        handsMO = (MemoryObject) this.getOutput("HANDS");
        knownJewelsMO = (MemoryObject) this.getOutput("KNOWN_JEWELS");
        leafletsMO = (MemoryObject) this.getInput("LEAFLETS");
    }

    public boolean isInLeaflet(List<Leaflet> leaflets, String jewelColor) {
        if (leaflets == null || leaflets.isEmpty()) {
            return false;
        }

        for (Leaflet leaflet : leaflets) {
            if (leaflet.ifInLeaflet(jewelColor) &&
                    leaflet.getTotalNumberOfType(jewelColor) >
                            leaflet.getCollectedNumberOfType(jewelColor)) {
                System.out.println("isInLeaflet: found leafletJewel");
                return true;
            }
        }

        return false;
    }

    @Override
    public void proc() {
        String jewelName = "";
        closestJewel = (Thing) closestJewelMO.getI();
        CreatureInnerSense cis = (CreatureInnerSense) innerSenseMO.getI();
        known = (List<Thing>) knownJewelsMO.getI();

        List<Leaflet> leaflets;
        if (leafletsMO != null) {
            leaflets = (List<Leaflet>) leafletsMO.getI();
        } else
            leaflets = null;

        if (closestJewel == null) {
            handsMO.setI("");
            return;
        }

        double jewelX = 0;
        double jewelY = 0;

        try {
            jewelX = closestJewel.getX1();
            jewelY = closestJewel.getY1();
            jewelName = closestJewel.getName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        double selfX = cis.position.getX();
        double selfY = cis.position.getY();

        Point2D pJewel = new Point();
        pJewel.setLocation(jewelX, jewelY);

        Point2D pSelf = new Point();
        pSelf.setLocation(selfX, selfY);

        double distance = pSelf.distance(pJewel);
        JSONObject message = new JSONObject();
        try {
            if (distance < reachDistance) { //get or hide it, depends on leaflet
                message.put("OBJECT", jewelName);
                if (leaflets != null) {
                    if (isInLeaflet(leaflets, closestJewel.getMaterial().getColorName())) {
                        message.put("ACTION", "SACKIT");
                        System.out.println("GetClosestJewel.proc:  sacking " + jewelName);
                    } else {
                        message.put("ACTION", "HIDEIT");
                        System.out.println("GetClosestJewel.proc:  hiding " + jewelName);
                    }
                } else {
                    message.put("ACTION", "SACKIT");
                }
                System.out.println("GetClosestJewel.proc: " + message.toString());
                handsMO.setI(message.toString());
                destroyClosestJewel();
            } else {
                handsMO.setI("");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void calculateActivation() {
    }

    public void destroyClosestJewel() {
        if (closestJewel != null) {
            Helper.removeByName((List<Thing>) knownJewelsMO.getI(), closestJewel.getName());
            closestJewel = null;
        }
    }
}
