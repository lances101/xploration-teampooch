package es.upm.platform03.behaviours;

import es.upm.common03.ontology.RoverPositionQuery;
import es.upm.common03.ontology.RoverPositionReply;
import es.upm.common03.ontology.Location;
import es.upm.platform03.Map;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleRoverPositionRequest extends SimpleBehaviour {
    Map agent;

    public HandleRoverPositionRequest(Map map) {
        this.agent = map;
    }

    MessageTemplate matchRegRequest;

    @Override
    public void action() {

        if (matchRegRequest == null) {
            matchRegRequest =
                    MessageTemplate.and(agent.getMtOntoAndCodec(),
                            MessageTemplate.and(
                                    MessageTemplate.MatchProtocol(agent.getRFBOntology().PROTOCOL_GET_ROVER_POSITION),
                                    MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                            )
                    );
        }
        ACLMessage msg = agent.receive(matchRegRequest);
        if (msg == null) {
            block();
            return;
        }
        ACLMessage reply = msg.createReply();
        if (!msg.getSender().getLocalName().equalsIgnoreCase("world")) {
            reply.setPerformative(ACLMessage.REFUSE);
            agent.send(reply);
            block();
            return;
        }
        reply.setPerformative(ACLMessage.AGREE);
        try {
            Action act = (Action) agent.getContentManager().extractContent(msg);
            RoverPositionQuery query = (RoverPositionQuery) act.getAction();
            Location location = agent.rovers.get(query.getRoverAID());

            if (location == null) {
                reply.setPerformative(ACLMessage.FAILURE);
            } else {
                reply.setPerformative(ACLMessage.INFORM);
                RoverPositionReply roverReply = new RoverPositionReply();
                roverReply.setRoverPosition(location);
                agent.getContentManager().fillContent(reply, new Action(agent.getAID(), roverReply));
            }
            agent.send(reply);
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean done() {
        return false;
    }
}
