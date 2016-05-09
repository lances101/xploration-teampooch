package es.upm.platform03.behaviours;

import es.upm.common03.ontology.RoverPositionQuery;
import es.upm.ontology.RequestRoverMovement;
import es.upm.platform03.World;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Timer;

/**
 * Created by borismakogonyuk on 08.05.16.
 */
public class HandleRoverMovementRequest extends CyclicBehaviour {
    int movementMillis = 3000;
    World agent;
    AID mapAID;
    HashMap<AID, Timer> movingRovers = new HashMap<AID, Timer>();

    public HandleRoverMovementRequest(World agent, AID mapAID) {
        super(agent);
        this.agent = agent;
        this.mapAID = mapAID;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg == null) {
            block();
            return;
        }
        System.out.printf("Got message %s from %s%n",
                msg.getPerformative(), msg.getSender().getLocalName());
        MessageTemplate mtAll = MessageTemplate.and(agent.getMtOntoAndCodec(),
                MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_ROVER_MOVEMENT));
        if (!mtAll.match(msg)) {
            agent.replyWithNotUnderstood(msg);
            block();
            return;
        }

        switch (msg.getPerformative()) {
            case ACLMessage.REQUEST:
                if (movingRovers.containsKey(msg.getSender())) {
                    System.out.println("Sending REFUSE to rover");
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REFUSE);
                    agent.send(reply);
                    block();
                    return;
                }
                startMovement(msg);
                break;
            case ACLMessage.CANCEL:
                Timer timer = movingRovers.get(msg.getSender());
                if(timer != null)
                {
                    timer.cancel();
                    movingRovers.remove(msg.getSender());
                    System.out.println("Got cancel. Cancelling");
                }
                break;
        }


    }

    void startMovement(ACLMessage msg) {
        try {
            Action action = (Action) agent.getContentManager().extractContent(msg);
            RequestRoverMovement request = (RequestRoverMovement) action.getAction();
            int direction = request.getDirection().getX();
            if (direction >= 0 && direction < 7) {
                movingRovers.put(msg.getSender(), createTimer(msg, direction));

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.AGREE);
                agent.send(reply);
                System.out.println("Sending agree to rover");


            } else {
                agent.replyWithNotUnderstood(msg);
            }
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }


    //TODO : THIS IS VERY FUCKING UGLY SHIT.
    Timer createTimer(ACLMessage msg, int dir)
    {

        Timer timer = new java.util.Timer();
        timer.schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    //TODO: add crash func
                    ACLMessage mapMsg = new ACLMessage(ACLMessage.REQUEST);
                    mapMsg.setOntology(agent.getxOntology().getName());
                    mapMsg.setLanguage(agent.getCodec().getName());
                    mapMsg.addReceiver(mapAID);
                    RoverPositionQuery query = new RoverPositionQuery();
                    query.setRoverAID(msg.getSender());
                    try {
                        agent.getContentManager().fillContent(mapMsg, new Action(agent.getAID(), query));
                        agent.send(mapMsg);
                    } catch (Codec.CodecException e) {
                        e.printStackTrace();
                    } catch (OntologyException e) {
                        e.printStackTrace();
                    }
                    

                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    agent.send(reply);
                    System.out.println("Movement ended.");
                }
            },
            movementMillis
        );
        return timer;
    }
}
