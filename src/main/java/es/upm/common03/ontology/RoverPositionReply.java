package es.upm.common03.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: RoverPositionReply
* @author ontology bean generator
* @version 2016/05/10, 17:47:14
*/
public class RoverPositionReply implements AgentAction {

   /**
* Protege name: RoverPosition
   */
   private Location roverPosition;
   public void setRoverPosition(Location value) { 
    this.roverPosition=value;
   }
   public Location getRoverPosition() {
     return this.roverPosition;
   }

}
