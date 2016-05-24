package es.upm.company03.behaviors.Company;

import es.upm.common03.RFBAgent;
import es.upm.common03.ontology.Capsule;
import es.upm.common03.ontology.InformAID;
import es.upm.common03.ontology.Rover;
import es.upm.ontology.RegisterAgents;
import es.upm.ontology.ReleaseCapsule;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * Created by borismakogonyuk on 08.05.16.
 */
public class HandleCapsuleReleaseInform extends SimpleBehaviour {

    CapsuleState state = CapsuleState.Release;
    ;
    RFBAgent agent;
    AID roverAID, capsuleAID, spaceAID;
    MessageTemplate mtAIDInform;
    MessageTemplate mtReleaseInform;
    public HandleCapsuleReleaseInform(RFBAgent agent) {
        super(agent);
        this.agent = agent;
        mtReleaseInform = MessageTemplate.and(agent.getMtOntoAndCodec(),
                MessageTemplate.and(
                        MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_RELEASE_CAPSULE),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM)
                )
        );
        mtAIDInform = MessageTemplate.and(agent.getMtOntoAndCodec(),
                MessageTemplate.and(
                        MessageTemplate.MatchProtocol(agent.getTeamOntology().PROTOCOL_INFORM_AID),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM)
                )
        );
    }

    @Override
    public void action() {
        switch (state) {
            case Release:
                HandleReleaseCapsulesInform();
                state = CapsuleState.Gather;
                break;
            case Gather:
                HandleIncomingAID();
                break;
            case Send:
                SendCompanyAIDs();
                break;
        }
    }

    private void SendCompanyAIDs() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setLanguage(agent.getCodec().getName());
        msg.setOntology(agent.getTeamOntology().getName());
        msg.addReceiver(spaceAID);
        RegisterAgents registerAgents = new RegisterAgents();
        es.upm.ontology.Rover rover = new es.upm.ontology.Rover();
        rover.setRover_agent(roverAID);
        es.upm.ontology.Capsule capsule = new es.upm.ontology.Capsule();
        capsule.setCapsule_agent(capsuleAID);
        registerAgents.setRover(rover);
        registerAgents.setCapsule(capsule);
        try {
            agent.getContentManager().fillContent(msg, new Action(agent.getAID(), registerAgents));
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
        agent.send(msg);
    }

    private void HandleIncomingAID() {
        ACLMessage msg = agent.receive(mtAIDInform);
        if (msg == null) {
            block();
            return;
        }
        try {
            Action act = (Action) agent.getContentManager().extractContent(msg);
            InformAID informAID = (InformAID) act.getAction();
            Object subject = informAID.getSubject();
            if (subject instanceof Capsule) {
                Capsule cap = (Capsule) subject;
                capsuleAID = cap.getCapsule_agent();
            } else if (subject instanceof Rover) {
                Rover rover = (Rover) subject;
                roverAID = rover.getRover_agent();
            }
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }

        if (roverAID != null && capsuleAID != null) {
            state = CapsuleState.Send;
            block();
        }
    }

    private void HandleReleaseCapsulesInform() {
        ACLMessage msg = agent.receive(mtReleaseInform);
        if (msg == null) {
            block();
            return;
        }
        try {
            spaceAID = msg.getSender();
            Action ce = (Action) agent.getContentManager().extractContent(msg);
            ReleaseCapsule releaseCap = (ReleaseCapsule) ce.getAction();
            AgentController ac = myAgent.getContainerController().createNewAgent("Capsule" + "03", "es.upm.company03.Capsule", new Object[]{getAgent().getAID(), releaseCap.getLocation(),});
            ac.start();
            System.out.printf("%s: Capsule released!%n", agent.getLocalName());


        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean done() {
        if (state != CapsuleState.Done)
            return false;
        agent.removeBehaviour(this);
        return true;
    }

    enum CapsuleState {
        Release,
        Gather,
        Send,
        Done
    }

}
