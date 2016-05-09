package es.upm.company03.behaviors;

import es.upm.common03.RFBAgent;
import es.upm.ontology.ReleaseCapsule;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * Created by borismakogonyuk on 08.05.16.
 */
public class HandleCapsuleReleaseInform extends SimpleBehaviour {
    boolean capsuleReleased = false;
    RFBAgent agent;

    public HandleCapsuleReleaseInform(RFBAgent agent) {
        super(agent);
        this.agent = agent;
    }

    MessageTemplate mtAll;

    @Override
    public void action() {
        if (mtAll == null)
            mtAll = MessageTemplate.and(agent.getMtOntoAndCodec(),
                    MessageTemplate.and(
                            MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_RELEASE_CAPSULE),
                            MessageTemplate.MatchPerformative(ACLMessage.INFORM)
                    )
            );

        ACLMessage msg = agent.receive(mtAll);
        if (msg == null) {
            block();
            return;
        }
        try {
            Action ce = (Action) agent.getContentManager().extractContent(msg);
            ReleaseCapsule releaseCap = (ReleaseCapsule) ce.getAction();
            AgentController ac = myAgent.getContainerController().createNewAgent("Capsule" + "03", "es.upm.company03.Capsule", new Object[]{releaseCap.getLocation(),});
            ac.start();
            capsuleReleased = true;
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
        if (!capsuleReleased)
            return false;
        agent.removeBehaviour(this);
        return true;
    }

}
