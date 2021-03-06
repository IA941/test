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

package codelets.motor;


import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Creature;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Hands Action Codelet monitors working storage for instructions and acts on the World accordingly.
 *
 * @author klaus
 */

public class HandsActionCodelet extends Codelet {
    private static Logger log = Logger.getLogger(HandsActionCodelet.class.getCanonicalName());
    private MemoryObject handsMO;
    private String previousHandsAction = "";
    private Creature c;
    private Random r = new Random();

    public HandsActionCodelet(Creature nc) {
        c = nc;
    }

    @Override
    public void accessMemoryObjects() {
        handsMO = (MemoryObject) this.getInput("HANDS");
    }

    public void proc() {
        String command = (String) handsMO.getI();

        if (!command.equals("") && (!command.equals(previousHandsAction))) {
            JSONObject jsonAction;
            try {
                jsonAction = new JSONObject(command);
                if (jsonAction.has("ACTION") && jsonAction.has("OBJECT")) {
                    String action = jsonAction.getString("ACTION");
                    String objectName = jsonAction.getString("OBJECT");
                    if (action.equals("PICKUP") || action.equals("SACKIT")) {
                        try {
                            c.putInSack(objectName);
                            System.out.println("HandsAction.proc sackit " + objectName);
                        } catch (Exception e) {
                            System.out.println("HandsAction.proc sackit: " + e.getMessage());
                        }
                        log.info("Sending Put In Sack command to agent:****** " + objectName + "**********");
                        //							}
                    }
                    if (action.equals("EATIT")) {
                        try {
                            c.eatIt(objectName);
                        } catch (Exception e) {

                        }
                        log.info("Sending Eat command to agent:****** " + objectName + "**********");
                    }
                    if (action.equals("BURY") || action.equals("HIDEIT")) {
                        try {
                            c.hideIt(objectName);
                        } catch (Exception e) {
                        }
                        log.info("Sending Bury command to agent:****** " + objectName + "**********");
                    }
                } else if (jsonAction.has("ACTION")) {
                    int x = 0, y = 0;
                    String action = jsonAction.getString("ACTION");
                    if (action.equals("FORAGE")) {
                        try {
                            x = r.nextInt(600);
                            y = r.nextInt(800);
                            c.moveto(1, x, y);
                        } catch (Exception e) {

                        }
                        System.out.println("Sending Search command to agent:****** (" + x + "," + y + ") **********");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        previousHandsAction = (String) handsMO.getI();
    }

    @Override
    public void calculateActivation() {

    }
}
