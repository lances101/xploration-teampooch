package es.upm.company03.behaviors.Capsule;

import es.upm.common03.TeamAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Created by borismakogonyuk on 05.06.16.
 */
public class HandleUpdateFindings extends CyclicBehaviour {
    private final TeamAgent agent;
    private final AID spacecraft;
    private final MessageTemplate mtUpdateFindings;
    public HandleUpdateFindings(TeamAgent agent, AID spacecraft){
        this.agent = agent;
        this.spacecraft = spacecraft;
        mtUpdateFindings = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.and(agent.getMtOntoAndCodec(),
                    MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_UPDATE_FINDINGS)));
    }
    @Override
    public void action() {
        ACLMessage msg = agent.receive(mtUpdateFindings);
        if(msg == null){
            block();
            return;
        }
        msg.removeReceiver(agent.getAID());
        msg.addReceiver(spacecraft);
        agent.send(msg);
        System.out.println("Capsule pushed findings to spacecraft");
    }
}
