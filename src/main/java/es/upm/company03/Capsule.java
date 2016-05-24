package es.upm.company03;

import es.upm.common03.RFBAgent;
import es.upm.common03.ontology.InformAID;
import es.upm.ontology.Location;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.logging.Level;

public class Capsule extends RFBAgent {
    Location location;

    @Override
    protected void setup() {
        AID companyAID = null;
        Object[] arguments = getArguments();
        if (arguments.length >= 2 && arguments[0] != null) {
            companyAID = (arguments[0] instanceof AID ? (AID) arguments[0] : null);
            location = (arguments[1] instanceof Location ? (Location) arguments[1] : null);
        }
        if (location == null || companyAID == null) {
            logger.log(Level.SEVERE, "Tried to instantiate Capsule without location.");
            return;
        }
        System.out.printf("%s: dropped at %d,%d%n", getLocalName(), location.getX(), location.getY());
        try {
            AgentController ac = getContainerController().createNewAgent("Rover03", "es.upm.company03.Rover", new Object[]{companyAID, location});
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        InformCompany(companyAID);
        super.setup();
    }

    private void InformCompany(AID company) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setLanguage(codec.getName());
        msg.setOntology(teamOntology.getName());
        msg.setProtocol(teamOntology.PROTOCOL_INFORM_AID);
        msg.addReceiver(company);
        InformAID aid = new InformAID();
        es.upm.common03.ontology.Capsule capsule = new es.upm.common03.ontology.Capsule();
        capsule.setCapsule_agent(getAID());
        aid.setSubject(capsule);
        try {
            getContentManager().fillContent(msg, new Action(getAID(), capsule));
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
        send(msg);
    }


}
