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

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import codelets.behaviors.*;
import codelets.motor.HandsActionCodelet;
import codelets.motor.LegsActionCodelet;
import codelets.perception.*;
import codelets.sensors.InnerSense;
import codelets.sensors.Vision;
import memory.CreatureInnerSense;
import support.MindView;
import utils.Intent;
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author rgudwin
 */
public class AgentMind extends Mind {
    private static int creatureBasicSpeed = 1;
    private static int reachDistance = 30;

    public AgentMind(Environment env) {
        super();

        // Declare Memory Objects        
        MemoryObject legsMO;
        MemoryObject handsMO;
        MemoryObject visionMO;
        MemoryObject innerSenseMO;
        MemoryObject closestAppleMO;
        MemoryObject knownApplesMO;
        // FMT 2017
        MemoryObject closestJewelMO;
        MemoryObject knownJewelsMO;
        MemoryObject fuelMO;
        MemoryObject leafletMO;
        MemoryObject intentMO;

        //Initialize Memory Objects
        legsMO = createMemoryObject("LEGS", "");
        handsMO = createMemoryObject("HANDS", "");
        List<Thing> vision_list = Collections.synchronizedList(new ArrayList<Thing>());
        visionMO = createMemoryObject("VISION", vision_list);
        CreatureInnerSense cis = new CreatureInnerSense();
        innerSenseMO = createMemoryObject("INNER", cis);
        Thing closestApple = null;
        closestAppleMO = createMemoryObject("CLOSEST_APPLE", closestApple);
        List<Thing> knownApples = Collections.synchronizedList(new ArrayList<Thing>());
        knownApplesMO = createMemoryObject("KNOWN_APPLES", knownApples);
        intentMO = createMemoryObject("INTENT", Intent.NONE);

        // FMT 2017 initialize jewel objects
        Thing closestJewel = null;
        closestJewelMO = createMemoryObject("CLOSEST_JEWEL", closestJewel);
        List<Thing> knownJewels = Collections.synchronizedList(new ArrayList<Thing>());
        knownJewelsMO = createMemoryObject("KNOWN_JEWELS", knownJewels);
        // handling leaflet
        List<Leaflet> leaflets = env.myCreature.getLeaflets();
        leafletMO = createMemoryObject("LEAFLETS", leaflets);
        // handling energy
        double fuel = env.myCreature.getFuel();
        fuelMO = createMemoryObject("FUEL", fuel);

        // Create and Populate MindViewer
        MindView mv = new MindView("FMT_MindView");
        mv.addMO(knownApplesMO);
        mv.addMO(visionMO);
        mv.addMO(closestAppleMO);
        mv.addMO(innerSenseMO);
        mv.addMO(handsMO);
        mv.addMO(legsMO);
        // FMT 2017
        mv.addMO(closestJewelMO);
        mv.addMO(knownJewelsMO);
        mv.addMO(leafletMO);
        mv.addMO(fuelMO);

        mv.StartTimer();
        mv.setVisible(true);

        // Create Sensor Codelets
        Codelet vision = new Vision(env.myCreature);
        vision.addOutput(visionMO);
        insertCodelet(vision); //Creates a vision sensor

        Codelet innerSense = new InnerSense(env.myCreature);
        innerSense.addOutput(innerSenseMO);
        insertCodelet(innerSense); //A sensor for the inner state of the creature

        // Create Actuator Codelets
        Codelet legs = new LegsActionCodelet(env.myCreature);
        legs.addInput(legsMO);
        insertCodelet(legs);

        Codelet hands = new HandsActionCodelet(env.myCreature);
        hands.addInput(handsMO);
        insertCodelet(hands);

        Codelet fuelDetector = new FuelLevelDetector(env.myCreature);
        fuelDetector.addOutput(fuelMO);
        insertCodelet(fuelDetector);

        // Create Perception Codelets
        Codelet appleDetector = new AppleDetector();
        appleDetector.addInput(visionMO);
        appleDetector.addOutput(knownApplesMO);
        insertCodelet(appleDetector);

        Codelet closestAppleDetector = new ClosestAppleDetector();
        closestAppleDetector.addInput(knownApplesMO);
        closestAppleDetector.addInput(innerSenseMO);
        closestAppleDetector.addOutput(closestAppleMO);
        insertCodelet(closestAppleDetector);

        // Create Behavior Codelets
        Codelet goToClosestApple = new GoToClosestApple(creatureBasicSpeed, reachDistance);
        goToClosestApple.addInput(closestAppleMO);
        goToClosestApple.addInput(innerSenseMO);
        goToClosestApple.addInput(fuelMO);
        goToClosestApple.addOutput(legsMO);
        //insertCodelet(goToClosestApple);

        Codelet eatApple = new EatClosestApple(reachDistance);
        eatApple.addInput(closestAppleMO);
        eatApple.addInput(innerSenseMO);
        eatApple.addOutput(handsMO);
        eatApple.addOutput(knownApplesMO);
        insertCodelet(eatApple);

        // FMT adding jewel handling
        // Create Perception Codelets
        Codelet jewelDetector = new JewelDetector();
        jewelDetector.addInput(visionMO);
        jewelDetector.addOutput(knownJewelsMO);
        insertCodelet(jewelDetector);

        Codelet closestJewelDetector = new ClosestJewelDetector();
        closestJewelDetector.addInput(knownJewelsMO);
        closestJewelDetector.addInput(innerSenseMO);
        closestJewelDetector.addOutput(closestJewelMO);
        insertCodelet(closestJewelDetector);

        // Create Behavior Codelets
        Codelet goToClosestJewel = new GoToClosestJewel(creatureBasicSpeed, reachDistance);
        goToClosestJewel.addInput(closestJewelMO);
        goToClosestJewel.addInput(innerSenseMO);
        goToClosestJewel.addInput(fuelMO);
        goToClosestJewel.addOutput(legsMO);
        insertCodelet(goToClosestJewel);

        Codelet getJewel = new GetClosestJewel(reachDistance);
        getJewel.addInput(closestJewelMO);
        getJewel.addInput(innerSenseMO);
        getJewel.addInput(leafletMO);
        getJewel.addOutput(handsMO);
        getJewel.addOutput(knownJewelsMO);
        insertCodelet(getJewel);

        Codelet search = new Search();
        search.addInput(knownJewelsMO);
        search.addInput(knownApplesMO);
        search.addOutput(legsMO);
        insertCodelet(search);

        start();
    }
}
