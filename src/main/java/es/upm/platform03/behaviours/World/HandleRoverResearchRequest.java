package es.upm.platform03.behaviours.World;

import es.upm.platform03.World;
import es.upm.platform03.XplorationMap;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.util.Pair;
import org.joda.time.DateTime;

import java.util.ArrayList;


public class HandleRoverResearchRequest extends CyclicBehaviour {
    int movementMillis = 3000;
    World agent;
    ArrayList<Pair<ACLMessage, DateTime>> researchConvo;
    ArrayList<Pair<ACLMessage, DateTime>> moveConvo;
    MessageTemplate mtAll;

    public HandleRoverResearchRequest(World agent, ArrayList<Pair<ACLMessage, DateTime>> researchConvo, ArrayList<Pair<ACLMessage, DateTime>> moveConvo) {
        super(agent);
        this.agent = agent;
        this.researchConvo = researchConvo;
        this.moveConvo = moveConvo;
        mtAll = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.and(agent.getMtOntoAndCodec(),
                        MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_ANALYZE_MINERAL)));
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mtAll);
        if (msg == null) {
            block();
            return;
        }
        System.out.println("Got new request for research from " + msg.getSender().getLocalName());
        if (researchConvo.stream().anyMatch(c -> c.getKey().getSender() == msg.getSender()) ||
            moveConvo.stream().anyMatch(c -> c.getKey().getSender() == msg.getSender()) ||
            XplorationMap.getPosition(msg.getSender()) == null){
            System.out.println("Sending REFUSE to rover");
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.REFUSE);
            agent.send(reply);
            block();
            return;
        }
        researchConvo.add(new Pair<>(msg, DateTime.now().plusMillis(movementMillis)));
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.AGREE);
        agent.send(reply);
        System.out.println("Sending agree to rover");


    }
}
