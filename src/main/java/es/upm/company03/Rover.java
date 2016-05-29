package es.upm.company03;

import es.upm.common03.TeamConstants;
import es.upm.common03.TeamAgent;
import es.upm.common03.ontology.InformAID;
import es.upm.company03.behaviors.Rover.HandleResearch;
import es.upm.ontology.Direction;
import es.upm.ontology.Location;
import es.upm.ontology.RequestRoverMovement;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.logging.Level;

/**
 * Created by borismakogonyuk on 30.04.16.
 */
public class Rover extends TeamAgent {
    Location location;
    AID world;

    @Override
    protected void setup() {
        AID companyAID = null;
        Object[] arguments = getArguments();
        if (arguments.length >= 2 && arguments[0] != null) {
            companyAID = (arguments[0] instanceof AID ? (AID) arguments[0] : null);
            location = (arguments[1] instanceof Location ? (Location) arguments[1] : null);
        }
        if (location == null) {
            logger.log(Level.SEVERE, "Tried to instantiate Rover without location.");
            return;
        }
        System.out.printf("%s: dropped at %d,%d%n", getLocalName(), location.getX(), location.getY());
        super.setup();
        world = findService("World");
        if(world == null)
        {
            System.out.println("CRITICAL. NO WORLD FOUND");
            this.doDelete();
            return;
        }
        super.setup();
        InformCompany(companyAID);
        addBehaviour(new HandleResearch(this, world));




        sendMovementMessage(TeamConstants.Direction.DOWN_LEFT);
        doWait(1000);
        sendMovementMessage(TeamConstants.Direction.CANCEL);
        doWait(2000);
        sendMovementMessage(TeamConstants.Direction.DOWN_RIGHT);
        doWait(6000);


    }

    private void InformCompany(AID company) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setLanguage(codec.getName());
        msg.setOntology(teamOntology.getName());
        msg.setProtocol(teamOntology.PROTOCOL_INFORM_AID);
        msg.addReceiver(company);
        InformAID informAID = new InformAID();
        es.upm.common03.ontology.Rover rover = new es.upm.common03.ontology.Rover();
        rover.setName("Name");
        rover.setRover_agent(getAID());
        informAID.setSubject(rover);
        try {
            getContentManager().fillContent(msg, new Action(getAID(), informAID));
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
        send(msg);
    }

    void sendMovementMessage(int dir) {
        try{
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.setProtocol(xOntology.PROTOCOL_ROVER_MOVEMENT);
            message.setOntology(xOntology.getName());
            message.setLanguage(codec.getName());
            message.addReceiver(world);
            if(dir == TeamConstants.Direction.CANCEL){
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

