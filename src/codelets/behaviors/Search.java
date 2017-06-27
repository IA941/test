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
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Thing;

import java.util.List;

public class Search extends Codelet {
    private MemoryObject knownApplesMO;
    private MemoryObject legsMO;
    private MemoryObject knownJewelsMO;
    private MemoryObject fuelMO;

    public Search() {
        this.setTimeStep(300);
    }

    @Override
    public void proc() {
        final int knownApples = ((List<Thing>) knownApplesMO.getI()).size();
        final int knownJewels = ((List<Thing>) knownJewelsMO.getI()).size();
        final double fuelLevel = ((double) fuelMO.getI());

        if (fuelLevel >= 400 && knownJewels > 0) {
            return;
        } else if (fuelLevel < 400 && knownApples > 0) {
            return;
        }

        JSONObject message = new JSONObject();
        try {
            message.put("ACTION", "FORAGE");
            System.out.println("Search.proc: " + message.toString());
            legsMO.setI(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void accessMemoryObjects() {
        knownApplesMO = (MemoryObject) this.getInput("KNOWN_APPLES");
        knownJewelsMO = (MemoryObject) this.getInput("KNOWN_JEWELS");
        fuelMO = (MemoryObject) this.getInput("FUEL");
        legsMO = (MemoryObject) this.getOutput("LEGS");
    }

    @Override
    public void calculateActivation() {

    }
}