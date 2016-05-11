package es.upm.company03;

import es.upm.common03.RFBAgent;
import es.upm.common03.RFBConstants;
import es.upm.ontology.Direction;
import es.upm.ontology.Location;
import es.upm.ontology.RequestRoverMovement;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.NotFoundException;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.logging.Level;

/**
 * Created by borismakogonyuk on 30.04.16.
 */
public class Rover extends RFBAgent {
    Location location;
    AID world;

    @Override
    protected void setup() {
        Object[] arguments = getArguments();
        if (arguments.length >= 1 && arguments[0] != null) {
            location = (arguments[0] instanceof Location ? (Location) arguments[0] : null);
        }
        if (location == null) {
            logger.log(Level.SEVERE, "Tried to instantiate Rover without location.");
            return;
        }
        System.out.printf("%s: dropped at %d,%d%n", getLocalName(), location.getX(), location.getY());
        super.setup();
        try {
            findWorldService();
        } catch (NotFoundException e) {
            e.printStackTrace();
            this.doDelete();
            return;
        }

        sendMovementMessage(RFBConstants.Direction.DOWN_LEFT);
        doWait(1000);
        sendMovementMessage(RFBConstants.Direction.CANCEL);
        doWait(2000);
        sendMovementMessage(RFBConstants.Direction.DOWN_RIGHT);
    }
    void findWorldService() throws NotFoundException {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("World");
        dfd.addServices(sd);

        DFAgentDescription[] found;
        try {
            found = DFService.search(this, dfd);
            if (found.length == 0) {
                System.out.printf("%s: Search yielded nothing. Waiting.%n",
                        this.getLocalName());
                throw new NotFoundException("Could not find World.");
            }
            world = found[0].getName();

        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
    void sendMovementMessage(int dir) {
        try{
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.setProtocol(xOntology.PROTOCOL_ROVER_MOVEMENT);
            message.setOntology(xOntology.getName());
            message.setLanguage(codec.getName());
            message.addReceiver(world);
            if(dir == RFBConstants.Direction.CANCEL){
                message.setPerformative(ACLMessage.CANCEL);
            }
            else {
                RequestRoverMovement request = new RequestRoverMovement();
                Direction direction = new Direction();
                direction.setX(dir);
                request.setDirection(direction);
                getContentManager().fillContent(message, new Action(getAID(), request));
            }
            send(message);
            System.out.printf("%s: requesting movement towards %d %n",
                    getLocalName(), dir);
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }

}

