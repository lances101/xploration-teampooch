package es.upm.company03;

import es.upm.company03.common.RFBAgent;
import es.upm.ontology.Location;

import java.util.logging.Level;

/**
 * Created by borismakogonyuk on 30.04.16.
 */
public class Rover extends RFBAgent {
    Location location;

    @Override
    protected void setup() {
        Object[] arguments = getArguments();
        if (arguments.length >= 1 && arguments[0] != null) {
            location = (arguments[0] instanceof Location ? (Location) arguments[0] : null);
        }
        if (location == null) {
            logger.log(Level.SEVERE, "Tried to instantiate Rover without location.");
            return;
        }
        System.out.printf("%s: dropped at %d,%d%n", getLocalName(), location.getX(), location.getY());
        super.setup();
    }

}
