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
 *****************************************************************************/

package codelets.behaviors;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import memory.CreatureInnerSense;
import model.Helper;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Thing;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class EatClosestApple extends Codelet {

    private MemoryObject closestAppleMO;
    private MemoryObject innerSenseMO;
    private MemoryObject knownApplesMO;
    private int reachDistance;
    private MemoryObject handsMO;
    private Thing closestApple;

    public EatClosestApple(int reachDistance) {
        setTimeStep(50);
        this.reachDistance = reachDistance;
    }

    @Override
    public void accessMemoryObjects() {
        closestAppleMO = (MemoryObject) this.getInput("CLOSEST_APPLE");
        innerSenseMO = (MemoryObject) this.getInput("INNER");
        handsMO = (MemoryObject) this.getOutput("HANDS");
        knownApplesMO = (MemoryObject) this.getOutput("KNOWN_APPLES");
    }

    @Override
    public void proc() {
        String appleName = "";
        closestApple = (Thing) closestAppleMO.getI();
        CreatureInnerSense cis = (CreatureInnerSense) innerSenseMO.getI();

        if (closestApple == null) {
            handsMO.setI("");
            return;
        }

        double appleX = 0;
        double appleY = 0;

        try {
            appleX = closestApple.getX1();
            appleY = closestApple.getY1();
            appleName = closestApple.getName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        double selfX = cis.position.getX();
        double selfY = cis.position.getY();

        Point2D pApple = new Point();
        pApple.setLocation(appleX, appleY);

        Point2D pSelf = new Point();
        pSelf.setLocation(selfX, selfY);

        double distance = pSelf.distance(pApple);
        JSONObject message = new JSONObject();
        try {
            if (distance < reachDistance) {
                message.put("OBJECT", appleName);
                message.put("ACTION", "EATIT");
                System.out.println("EatClosestApple.proc: " + message.toString());
                handsMO.setI(message.toString());
                clearClosestApple();
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

    public void clearClosestApple() {
        if (closestAppleMO != null) {
            Helper.removeByName((List<Thing>) knownApplesMO.getI(), closestApple.getName());
            closestApple = null;
        }
    }
}
