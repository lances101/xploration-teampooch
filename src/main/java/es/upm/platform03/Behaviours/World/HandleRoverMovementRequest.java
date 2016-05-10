package es.upm.platform03.behaviours.World;

import com.sun.tools.javac.util.Pair;
import es.upm.ontology.RequestRoverMovement;
import es.upm.platform03.World;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.DateTime;

import java.util.ArrayList;


public class HandleRoverMovementRequest extends CyclicBehaviour {
    int movementMillis = 3000;
    World agent;
    ArrayList<Pair<ACLMessage, DateTime>> moveConvo = new ArrayList<Pair<ACLMessage, DateTime>>();

    public HandleRoverMovementRequest(World agent, ArrayList<Pair<ACLMessage, DateTime>> moveConvo) {
        super(agent);
        this.agent = agent;
        this.moveConvo = moveConvo;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg == null) {
            block();
            return;
        }
        MessageTemplate mtAll = MessageTemplate.and(agent.getMtOntoAndCodec(),
                MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_ROVER_MOVEMENT));
        if (!mtAll.match(msg)) {
            agent.replyWithNotUnderstood(msg);
            block();
            return;
        }

        switch (msg.getPerformative()) {
            case ACLMessage.REQUEST:
                if (moveConvo.stream().anyMatch(c -> c.fst.getSender() == msg.getSender())) {
                    System.out.println("Sending REFUSE to rover");
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REFUSE);
                    agent.send(reply);
                    block();
                    return;
                }

                if (tryAddRoverMovement(msg)) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.AGREE);
                    agent.send(reply);
                    System.out.println("Sending agree to rover");
                } else {
                    agent.replyWithNotUnderstood(msg);
                }

                break;
            case ACLMessage.CANCEL:
                System.out.println("Got a cancel");
                for (int i = 0; i < moveConvo.size(); i++) {
                    ACLMessage convoMsg = moveConvo.get(i).fst;
                    if (convoMsg.getSender().equals(msg.getSender())) {
                        if (msg.getConversationId() != null) {
                            //Rover sent specific convo id, searching for it then
                            if(convoMsg != null && msg.getConversationId().equalsIgnoreCase(convoMsg.getConversationId())){
                                moveConvo.remove(i);
                                i--;
                                System.out.println("Got cancel. Found rover. Cancelled.");
                            }
                        }
                        else{
                            moveConvo.remove(i);
                            i--;
                            System.out.println("Got cancel. Found rover. Cancelled.");
                        }
                    }
                }


                break;

        }


    }

    boolean tryAddRoverMovement(ACLMessage msg) {
        try {
            Action action = (Action) agent.getContentManager().extractContent(msg);
            RequestRoverMovement request = (RequestRoverMovement) action.getAction();
            int direction = request.getDirection().getX();
            if (direction >= 0 && direction < 7) {
                moveConvo.add(new Pair<>(msg, DateTime.now().plusMillis(movementMillis)));
                return true;
            }
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
        return false;
    }
}
