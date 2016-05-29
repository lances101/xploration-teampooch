package es.upm.platform03;

import es.upm.common03.TeamAgent;
import es.upm.ontology.Location;
import es.upm.platform03.behaviors.Broker.HandleMessageProxy;
import es.upm.platform03.behaviors.World.*;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;
import org.joda.time.DateTime;

import java.util.ArrayList;


public class World extends TeamAgent {

    ArrayList<Pair<ACLMessage, DateTime>> moveConvo = new ArrayList<>();
    ArrayList<Pair<ACLMessage, DateTime>> researchConvo = new ArrayList<>();
    @Override
    protected void setup() {
        super.setup();
        System.out.printf("%s is starting up!%n", getLocalName());
        Location loc = new Location();
        loc.setX(5);
        loc.setY(5);
        XplorationMap.GetAgentsInRange(1, loc);


        addBehaviour(new HandleRoverMovementRequest(this, moveConvo));
        addBehaviour(new HandleRoverMovementReply(this, 250, moveConvo));
        addBehaviour(new HandleRoverResearchRequest(this, researchConvo));
        addBehaviour(new HandleRoverResearchReply(this, 250, researchConvo));
        addBehaviour(new HandleMessageProxy(this));

        registerSelfWithServices(new String[]{"World", "Broker", "Proxy"});
    }
}
