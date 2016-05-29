package es.upm.platform03;

import es.upm.common03.TeamAgent;
import es.upm.platform03.behaviours.Broker.HandleMessageProxy;
import es.upm.platform03.behaviours.World.HandleRoverMovementReply;
import es.upm.platform03.behaviours.World.HandleRoverMovementRequest;
import es.upm.platform03.behaviours.World.HandleRoverResearchReply;
import es.upm.platform03.behaviours.World.HandleRoverResearchRequest;
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

        addBehaviour(new HandleRoverMovementRequest(this, moveConvo, researchConvo));
        addBehaviour(new HandleRoverMovementReply(this, 250, moveConvo));
        addBehaviour(new HandleRoverResearchRequest(this, researchConvo, moveConvo));
        addBehaviour(new HandleRoverResearchReply(this, 250, researchConvo));
        addBehaviour(new HandleMessageProxy(this));

        registerSelfWithServices(new String[]{"World", "Broker", "Proxy"});
    }
}
