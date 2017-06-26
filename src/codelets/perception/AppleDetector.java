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

package codelets.perception;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import ws3dproxy.model.Thing;

import java.util.ArrayList;
import java.util.List;

/**
 * Detect apples in the vision field.
 * This class detects a number of things related to apples, such as if there are any within reach,
 * any on sight, if they are rotten, and so on.
 *
 * @author klaus
 */
public class AppleDetector extends Codelet {

    private MemoryObject visionMO;
    private MemoryObject knownApplesMO;

    public AppleDetector() {

    }

    @Override
    public void accessMemoryObjects() {
        synchronized (this) {
            this.visionMO = (MemoryObject) this.getInput("VISION");
        }
        this.knownApplesMO = (MemoryObject) this.getOutput("KNOWN_APPLES");
    }

    @Override
    public void proc() {
        List<Thing> vision;
        synchronized (visionMO) {
            vision = new ArrayList<>((List<Thing>) visionMO.getI());
        }

        List<Thing> known = (List<Thing>) knownApplesMO.getI();

        synchronized (known) {
            for (Thing thingInVision : vision) {
                boolean found = false;
                for (Thing knownThing : known) {
                    if (thingInVision.getName().equals(knownThing.getName())) {
                        found = true;
                        break;
                    }
                }

                if (!found && thingInVision.getName().contains("PFood") && !thingInVision.getName().contains("NPFood")) {
                    known.add(thingInVision);
                }
            }
        }
    }

    @Override
    public void calculateActivation() {

    }
}


