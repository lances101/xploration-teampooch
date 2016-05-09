package es.upm.platform03;

import es.upm.common03.RFBAgent;
import es.upm.common03.ontology.Location;
import es.upm.platform03.behaviours.HandleRoverPositionRequest;
import jade.core.AID;

import java.util.HashMap;

public class Map extends RFBAgent {
    public HashMap<AID, Location> rovers = new HashMap<AID, Location>();
    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", getLocalName());
        registerSelfWithServices(new String[]{"Map"});
        addBehaviour(new HandleRoverPositionRequest(this));
        super.setup();
    }


}
