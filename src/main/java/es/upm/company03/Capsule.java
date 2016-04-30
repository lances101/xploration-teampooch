package es.upm.company03;

import es.upm.company03.common.RFBAgent;
import es.upm.ontology.Location;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.logging.Level;

public class Capsule extends RFBAgent {
    Location location;

    @Override
    protected void setup() {
        Object[] arguments = getArguments();
        if (arguments.length >= 1 && arguments[0] != null) {
            location = (arguments[0] instanceof Location ? (Location) arguments[0] : null);
        }
        if (location == null) {
            logger.log(Level.SEVERE, "Tried to instantiate Capsule without location.");
            return;
        }
        System.out.printf("%s: dropped at %d,%d%n", getLocalName(), location.getX(), location.getY());
        try {
            AgentController ac = getContainerController().createNewAgent("Rover03", "es.upm.company03.Rover", new Object[]{location});
            ac.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        super.setup();
    }


}
