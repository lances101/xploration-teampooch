package es.upm.platform03.behaviours.Spacecraft;

import es.upm.common03.TeamAgent;
import es.upm.ontology.Company;
import es.upm.ontology.Location;
import es.upm.ontology.RegisterAgents;
import es.upm.platform03.XplorationMap;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.util.Pair;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Random;

public class HandleReleaseCapsule extends SimpleBehaviour {

    boolean releaseCapsuleSent = false;
    TeamAgent agent;
    ArrayList<Company> companies;
    MessageTemplate mtInform;
    ArrayList<Pair<Company, Location>> tempLocation = new ArrayList<>();

    public HandleReleaseCapsule(TeamAgent a, ArrayList<Company> companies) {
        super(a);
        this.agent = a;
        this.companies = companies;
        mtInform = MessageTemplate.and(agent.getMtOntoAndCodec(),
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
        ACLMessage msg = agent.receive(mtInform);
        if (msg == null) {
            block();
            return;
        }
        try {
            AID sender = msg.getSender();
            Action action = (Action) agent.getContentManager().extractContent(msg);
            RegisterAgents regAgents = (RegisterAgents) action.getAction();
            Company company = companies.stream().filter(c -> c.getCompany_agent().equals(sender)).findFirst().get();
            company.setCapsule(regAgents.getCapsule());
            company.getCapsule().setRover(regAgents.getRover());
            System.out.println("Aware of " + sender.getLocalName() + " rover and capsule. Informing World");
            XplorationMap.UpdatePosition(company.getCapsule().getRover().getRover_agent(),
                    tempLocation.stream().filter(c->c.getKey() == company).findFirst().get().getValue() );
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }

    private void SendReleaseCapsuleMessages() {
        System.out.printf("%s: sending ReleaseCapsuleBehavior to %d companies%n",
                agent.getLocalName(), companies.size());
        for (Company company : companies) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setOntology(agent.getxOntology().getName());
            msg.setLanguage(agent.getCodec().getName());
            msg.setProtocol(agent.getxOntology().PROTOCOL_RELEASE_CAPSULE);
            msg.addReceiver(company.getCompany_agent());

            //TODO: calculate real position. for now - random
            Random rnd = new Random(DateTime.now().getMillis());

            es.upm.ontology.ReleaseCapsule releaseCapsule = new es.upm.ontology.ReleaseCapsule();
            Location location = new Location();
            location.setX(rnd.nextInt(5));
            location.setY(rnd.nextInt(5));
            releaseCapsule.setLocation(location);
            tempLocation.add(new Pair<Company, Location>(company, location));
            try {
                agent.getContentManager().fillContent(msg, new Action(agent.getAID(), releaseCapsule));
                agent.send(msg);
            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
