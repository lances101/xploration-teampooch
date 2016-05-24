package es.upm.platform03;

import es.upm.common03.RFBAgent;
import es.upm.platform03.behaviours.World.HandleRoverMovementReply;
import es.upm.platform03.behaviours.World.HandleRoverMovementRequest;
import es.upm.platform03.behaviours.World.HandleRoverResearchRequest;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;
import org.joda.time.DateTime;

import java.util.ArrayList;


public class World extends RFBAgent {

    ArrayList<Pair<ACLMessage, DateTime>> moveConvo = new ArrayList<>();
    ArrayList<Pair<ACLMessage, DateTime>> researchConvo = new ArrayList<>();
    @Override
    protected void setup() {

        System.out.printf("%s is starting up!%n", getLocalName());
        registerSelfWithServices(new String[]{"World", "Proxy"});
        super.setup();

        addBehaviour(new HandleRoverMovementRequest(this, moveConvo));
        addBehaviour(new HandleRoverMovementReply(this, 250, moveConvo));
        addBehaviour(new HandleRoverResearchRequest(this, researchConvo));
        addBehaviour(new HandleRoverMovementReply(this, 250, researchConvo));

    }
}
