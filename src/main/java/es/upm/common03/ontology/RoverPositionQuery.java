package es.upm.common03.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: RoverPositionQuery
* @author ontology bean generator
* @version 2016/05/8, 21:09:33
*/
public class RoverPositionQuery implements AgentAction {

   /**
* Protege name: RoverAID
   */
   private AID roverAID;
   public void setRoverAID(AID value) { 
    this.roverAID=value;
   }
   public AID getRoverAID() {
     return this.roverAID;
   }

}
