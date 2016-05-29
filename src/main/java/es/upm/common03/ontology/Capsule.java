package es.upm.common03.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Capsule
* @author ontology bean generator
* @version 2016/05/26, 11:49:49
*/
public class Capsule implements Concept {

   /**
* Protege name: rover
   */
   private Rover rover;
   public void setRover(Rover value) { 
    this.rover=value;
   }
   public Rover getRover() {
     return this.rover;
   }

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
* Protege name: capsule_agent
   */
   private AID capsule_agent;
   public void setCapsule_agent(AID value) { 
    this.capsule_agent=value;
   }
   public AID getCapsule_agent() {
     return this.capsule_agent;
   }

}
