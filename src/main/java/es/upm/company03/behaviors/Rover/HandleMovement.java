package es.upm.company03.behaviors.Rover;

import es.upm.common03.LocationCalculator;
import es.upm.common03.TeamAgent;
import es.upm.common03.TeamConstants;
import es.upm.ontology.Direction;
import es.upm.ontology.Location;
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
    enum State {
        Send,
        Receive,
        End
    }

    private int direction;
    private TeamAgent agent;
    private AID world;
    private State state = State.Send;
    private MessageTemplate mtMovement;
    private Location currentLocation;

    public State getState() {
        return state;
    }
    public int getDirection() {
        return direction;
    }
    public void setDirection(int dir) {
        this.direction = dir;
    }
    public Location getCurrentLocation() {
        return currentLocation;
    }


    public HandleMovement(TeamAgent agent, AID world) {
        this.agent = agent;
        this.world = world;
        mtMovement = MessageTemplate.and(agent.getMtOntoAndCodec(),
                MessageTemplate.MatchProtocol(XplorationOntology.PROTOCOL_ROVER_MOVEMENT)
        );
        this.state = State.End;
    }

    public void startMovement(Location loc, int dir) {
        this.currentLocation = loc;
        this.direction = dir;
        this.state = State.Send;
        this.reset();
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
                if (direction == TeamConstants.Direction.CANCEL) {
                    message.setPerformative(ACLMessage.CANCEL);
                } else {
                    RequestRoverMovement request = new RequestRoverMovement();
                    Direction direction = new Direction();
                    direction.setX(this.direction);
                    request.setDirection(direction);

                    try {
                        agent.getContentManager().fillContent(message, new Action(agent.getAID(), request));
                        agent.send(message);
                        System.out.printf("%s: requesting movement%n",
                                agent.getLocalName());
                        state = State.Receive;
                    } catch (Codec.CodecException e) {
                        e.printStackTrace();
                    } catch (OntologyException e) {
                        e.printStackTrace();
                    }
                }
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
                        currentLocation = LocationCalculator.calculateNewLocation(currentLocation, direction);
                        System.out.printf("We have moved! Now at %d, %d%n", currentLocation.getX(), currentLocation.getY());
                        state = State.End;
                        break;
                }

                break;
        }
    }

    @Override
    public boolean done() {
        return state == State.End;
    }
}
