package es.upm.company03.behaviors.Rover;

import es.upm.company03.Rover;
import es.upm.ontology.MoveInformation;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleRoverInfo extends CyclicBehaviour {

    private final Rover agent;
    private final MessageTemplate mtMoveInfo;

    public HandleRoverInfo(Rover agent){
        this.agent = agent;
        mtMoveInfo = MessageTemplate.and(agent.getMtOntoAndCodec(),
                MessageTemplate.and(
                        MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_MOVE_INFO),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM)));
    }

    @Override
    public void action() {
        ACLMessage msg = agent.receive(mtMoveInfo);
        if(msg == null){
            block();
            return;
        }
        try {
            Action act = (Action) agent.getContentManager().extractContent(msg);
            MoveInformation moveInformation = (MoveInformation) act.getAction();
            if(moveInformation.getRover().getRover_agent() != null && moveInformation.getLocation() != null) {
                if(moveInformation.getRover().getRover_agent() != agent.getAID())
                    agent.updateRoverInfo(moveInformation.getRover().getRover_agent(), moveInformation.getLocation());
            }
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
