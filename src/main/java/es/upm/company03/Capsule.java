package es.upm.company03;

import es.upm.common03.TeamAgent;
import es.upm.common03.ontology.*;
import es.upm.company03.behaviors.Capsule.HandleUpdateFindings;
import es.upm.ontology.Location;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.logging.Level;

public class Capsule extends TeamAgent {
    Location location;

    @Override
    protected void setup() {
        super.setup();
        AID companyAID = null;
        int mapSizeX = 0, mapSizeY = 0;
        Object[] arguments = getArguments();
        if (arguments.length >= 4 && arguments[0] != null) {
            companyAID = (arguments[0] instanceof AID ? (AID) arguments[0] : null);
            location = (arguments[1] instanceof Location ? (Location) arguments[1] : null);
            mapSizeX = (arguments[2] instanceof Integer ? (Integer) arguments[2] : null);
            mapSizeY = (arguments[3] instanceof Integer ? (Integer) arguments[3] : null);
        }
        if (location == null || companyAID == null) {
            logger.log(Level.SEVERE, "Tried to instantiate Capsule without location.");
            return;
        }
        System.out.printf("%s: dropped at %d,%d%n", getLocalName(), location.getX(), location.getY());
        try {
            AgentController ac = getContainerController().createNewAgent("Rover03", "es.upm.company03.Rover", new Object[]{companyAID, location, mapSizeX, mapSizeY});
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
    }
        InformCompany(companyAID);
        addBehaviour(new HandleUpdateFindings(this, findService("Spacecraft")));
    }

    private void InformCompany(AID company) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setLanguage(codec.getName());
        msg.setOntology(teamOntology.getName());
        msg.setProtocol(teamOntology.PROTOCOL_INFORM_AID);
        msg.addReceiver(company);
        InformAID informAID = new InformAID();
        es.upm.ontology.Capsule capsule = new es.upm.ontology.Capsule();
        //TODO: remove. its useless. added because I cannot bother to change the ontology
        es.upm.ontology.Rover rover = new es.upm.ontology.Rover();
        rover.setName("Name");
        capsule.setRover(rover);
        capsule.setName("Name");
        capsule.setCapsule_agent(getAID());

        informAID.setSubject(capsule);
        try {
            getContentManager().fillContent(msg, new Action(getAID(), informAID));
            send(msg);
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }

    }


}
