/*
 * Copyright (C) 2017 ftanada.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package codelets.perception;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import ws3dproxy.model.Thing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ftanada
 */

/**
 * Detect jewels in the vision field.
 * This class detects a number of things related to apples, such as if there are any within reach,
 * any on sight, if they are rotten, and so on.
 *
 * @author klaus
 */
public class JewelDetector extends Codelet {
    private MemoryObject visionMO;
    private MemoryObject knownJewelsMO;

    public JewelDetector() {
    }

    @Override
    public void accessMemoryObjects() {
        synchronized (this) {
            this.visionMO = (MemoryObject) this.getInput("VISION");
        }
        this.knownJewelsMO = (MemoryObject) this.getOutput("KNOWN_JEWELS");
    }

    @Override
    public void proc() {
        List<Thing> vision;
        synchronized (visionMO) {
            vision = new ArrayList((List<Thing>) visionMO.getI());
        }

        List<Thing> known = (List<Thing>) knownJewelsMO.getI();

        synchronized (known) {
            for (Thing t : vision) {
                boolean found = false;
                for (Thing e : known)
                    if (t.getName().equals(e.getName())) {
                        found = true;
                        break;
                    }
                //System.out.println("jewelDetector.proc: "+t.getName());
                if (!found && t.getName().contains("Jewel")) {
                    //System.out.println("jewelDetector.proc: adding "+t.getName());
                    known.add(t);
                }
            }
        }

    }// end proc

    @Override
    public void calculateActivation() {

    }


}//end class
    

