package es.upm.common03.ontology;


import jade.content.Concept;

/**
* Protege name: Location
* @author ontology bean generator
 * @version 2016/05/24, 22:31:50
*/
public class Location implements Concept {

   /**
* Protege name: Y
   */
   private int y;
   /**
* Protege name: X
   */
   private int x;

    public int getY() {
        return this.y;
   }

    public void setY(int value) {
        this.y = value;
    }

   public int getX() {
     return this.x;
   }

    public void setX(int value) {
        this.x = value;
   }

}
