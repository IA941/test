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

import ws3dproxy.CommandExecException;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;
import ws3dproxy.model.World;

/**
 * @author rgudwin
 */
public class Environment {

    public Creature myCreature = null;

    public Environment() {
        WS3DProxy proxy = new WS3DProxy();
        try {
            World w = World.getInstance();
            w.reset();
            myCreature = proxy.createCreature(100, 450, 0);
            World.createFood(0, 350, 75);
            World.createFood(0, 100, 220);
            World.createFood(0, 250, 210);
            World.createJewel(1, 200, 200);
            World.createJewel(2, 300, 300);
            World.createJewel(3, 400, 400);
            World.createJewel(4, 140, 410);
            World.grow(1);
            myCreature.start();
            myCreature.genLeaflet();
            myCreature.updateState();
        } catch (CommandExecException e) {
            e.printStackTrace();
        }

        System.out.println("Robot " + myCreature.getName() + " is ready to go.");
    }
}
