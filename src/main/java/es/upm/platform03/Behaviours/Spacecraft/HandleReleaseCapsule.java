package es.upm.platform03.behaviours.Spacecraft;

import es.upm.common03.CompanyAIDTuple;
import es.upm.common03.RFBAgent;
import es.upm.ontology.Location;
import es.upm.ontology.RegisterAgents;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Random;

public class HandleReleaseCapsule extends SimpleBehaviour {

    boolean releaseCapsuleSent = false;
    RFBAgent agent;
    ArrayList<CompanyAIDTuple> companies;
    MessageTemplate mtAll;

    public HandleReleaseCapsule(RFBAgent a, ArrayList<CompanyAIDTuple> companies) {
        super(a);
        this.agent = a;
        this.companies = companies;
        mtAll = MessageTemplate.and(agent.getMtOntoAndCodec(),
                MessageTemplate.and(
                        MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_RELEASE_CAPSULE),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM)
                )
        );
    }

    @Override
    public void action() {
        if (!releaseCapsuleSent) {
            SendReleaseCapsuleMessages();
            releaseCapsuleSent = true;
            return;
        }
        ACLMessage msg = agent.receive(mtAll);
        if (msg == null) {
            block();
            return;
        }
        try {
            AID sender = msg.getSender();
            Action action = (Action) agent.getContentManager().extractContent(msg);
            RegisterAgents regAgents = (RegisterAgents) action.getAction();
            CompanyAIDTuple tuple = companies.stream().filter(c -> c.getCompany().equals(sender)).findFirst().get();
            tuple.setCapsule(regAgents.getCapsule().getCapsule_agent());
            tuple.setRover(regAgents.getRover().getRover_agent());
            System.out.println("Aware of " + sender.getLocalName() + " rover and capsule.");
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }

    private void SendReleaseCapsuleMessages() {
        System.out.printf("%s: sending ReleaseCapsuleBehavior to %d companies%n",
                agent.getLocalName(), companies.size());
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setOntology(agent.getxOntology().getName());
        msg.setLanguage(agent.getCodec().getName());
        msg.setProtocol(agent.getxOntology().PROTOCOL_RELEASE_CAPSULE);
        for (CompanyAIDTuple tuple : companies)
            msg.addReceiver(tuple.getCompany());

        //TODO: calculate real position. for now - random
        Random rnd = new Random(DateTime.now().getMillis());

        es.upm.ontology.ReleaseCapsule releaseCapsule = new es.upm.ontology.ReleaseCapsule();
        Location location = new Location();
        location.setX(rnd.nextInt(5));
        location.setY(rnd.nextInt(5));
        releaseCapsule.setLocation(location);
        try {
            agent.getContentManager().fillContent(msg, new Action(agent.getAID(), releaseCapsule));
            agent.send(msg);
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
