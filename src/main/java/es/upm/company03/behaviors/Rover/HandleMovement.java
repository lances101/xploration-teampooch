package es.upm.company03.behaviors.Rover;

import es.upm.common03.LocationUtility;
import es.upm.common03.TeamConstants;
import es.upm.company03.Rover;
import es.upm.ontology.Direction;
import es.upm.ontology.RequestRoverMovement;
import es.upm.ontology.XplorationOntology;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleMovement extends SimpleBehaviour {
    public static class EndCodes {
        public static final int TO_ROAMING = 1;
        public static final int TO_DELIVERING = 2;

    }
    public enum State {
        Send,
        Receive,
        End
    }

    private Rover agent;
    private AID world;
    private State state = State.Send;
    private MessageTemplate mtMovement;
    public HandleMovement(Rover agent, AID world) {
        this.agent = agent;
        this.world = world;
        mtMovement = MessageTemplate.and(agent.getMtOntoAndCodec(),
                MessageTemplate.MatchProtocol(XplorationOntology.PROTOCOL_ROVER_MOVEMENT)
        );
    }

    @Override
    public void action() {
        switch (state) {
            case Send:
                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                message.setProtocol(XplorationOntology.PROTOCOL_ROVER_MOVEMENT);
                message.setOntology(agent.getxOntology().getName());
                message.setLanguage(agent.getCodec().getName());
                message.addReceiver(world);
                if (agent.getNextDirection() == TeamConstants.Direction.CANCEL) {
                    message.setPerformative(ACLMessage.CANCEL);
                    state = State.End;
                } else {
                    RequestRoverMovement request = new RequestRoverMovement();
                    Direction direction = new Direction();
                    direction.setX(agent.getNextDirection());
                    request.setDirection(direction);
                    try {
                        agent.getContentManager().fillContent(message, new Action(agent.getAID(), request));
                        System.out.printf("%s: requesting movement%n",
                                agent.getLocalName());
                        state = State.Receive;
                    } catch (Codec.CodecException e) {
                        e.printStackTrace();
                    } catch (OntologyException e) {
                        e.printStackTrace();
                    }
                }
                agent.send(message);
                break;
            case Receive:
                ACLMessage msg = agent.receive(mtMovement);
                if (msg == null) {
                    block();
                    return;
                }
                switch (msg.getPerformative()) {
                    case ACLMessage.REFUSE:
                        //TODO: handle this somehow
                        state = State.End;
                        break;
                    case ACLMessage.AGREE:
                        //just wait then
                        break;
                    case ACLMessage.INFORM:
                        agent.setRoverLocation(LocationUtility.calculateNewLocation(agent.getRoverLocation(), agent.getNextDirection(), agent.getMapSizeX(), agent.getMapSizeY()));
                        System.out.printf("We have moved! Now at %d, %d%n", agent.getRoverLocation().getX(), agent.getRoverLocation().getY());
                        state = State.End;
                        break;
                }
                break;
            case End:
                block();
                break;
        }
    }

    @Override
    public int onEnd() {
        return agent.getCurrentJob() == Rover.RoverJobs.ROAMING ? EndCodes.TO_ROAMING : EndCodes.TO_DELIVERING;
    }

    @Override
    public void reset() {
        super.reset();
        state = State.Send;
    }

    @Override
    public boolean done() {
        return state == State.End;
    }
}
