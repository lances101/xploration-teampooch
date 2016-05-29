package es.upm.common03.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Rover
* @author ontology bean generator
* @version 2016/05/26, 11:49:49
*/
public class Rover implements Concept {

   /**
* Protege name: name
   */
   private String name;
   public void setName(String value) { 
    this.name=value;
   }
   public String getName() {
     return this.name;
   }

   /**
* Protege name: rover_agent
   */
   private AID rover_agent;
   public void setRover_agent(AID value) { 
    this.rover_agent=value;
   }
   public AID getRover_agent() {
     return this.rover_agent;
   }

}
