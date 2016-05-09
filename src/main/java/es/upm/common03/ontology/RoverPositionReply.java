package es.upm.common03.ontology;


import jade.content.AgentAction;

/**
* Protege name: RoverPositionReply
* @author ontology bean generator
* @version 2016/05/8, 21:09:33
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
