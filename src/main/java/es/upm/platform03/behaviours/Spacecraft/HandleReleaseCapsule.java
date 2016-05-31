package es.upm.platform03.behaviours.Spacecraft;

import es.upm.common03.TeamAgent;
import es.upm.ontology.Company;
import es.upm.ontology.Location;
import es.upm.ontology.RegisterAgents;
import es.upm.ontology.XplorationOntology;
import es.upm.platform03.XplorationMap;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.util.Pair;

import java.util.ArrayList;

public class HandleReleaseCapsule extends CyclicBehaviour {

    boolean releaseCapsuleSent = false;
    TeamAgent agent;
    ArrayList<Company> companies;
    MessageTemplate mtInform;
    ArrayList<Pair<Company, Location>> tempLocations = new ArrayList<>();

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
    private void fillTempLocations() {
        Location[] locations = XplorationMap.findLocationForRovers(companies.size());
        for(int i = 0; i < this.companies.size(); i++)
            tempLocations.add(new Pair<>(this.companies.get(i), locations[i]));
    }

    @Override
    public void action() {
        if (!releaseCapsuleSent) {
            fillTempLocations();
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
            XplorationMap.UpdatePosition(company.getCapsule().getRover().getRover_agent(),
                    tempLocations.stream().filter(c->c.getKey() == company).findFirst().get().getValue() );
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
            msg.setProtocol(XplorationOntology.PROTOCOL_RELEASE_CAPSULE);
            msg.addReceiver(company.getCompany_agent());
            es.upm.ontology.ReleaseCapsule releaseCapsule = new es.upm.ontology.ReleaseCapsule();
            Location location = tempLocations.stream().filter(c->c.getKey().getCompany_agent().equals(company.getCompany_agent())).findFirst().get().getValue();
            releaseCapsule.setLocation(location);
            releaseCapsule.setSizeX(XplorationMap.getSizeX());
            releaseCapsule.setSizeY(XplorationMap.getSizeY());
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

}
