package es.upm.platform03.behaviours.Broker;

import es.upm.platform03.World;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleMoveInfoProxy extends CyclicBehaviour {
    World agent;
    MessageTemplate mtMoveInfo;
    public HandleMoveInfoProxy(World agent) {
        this.agent = agent;
        mtMoveInfo = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.and(agent.getMtOntoAndCodec(),
                MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_MOVE_INFO)));
    }

    @Override
    public void action() {
        ACLMessage msg = agent.receive(mtMoveInfo);
        if (msg == null) {
            block();
            return;
        }
        msg.removeReceiver(agent.getAID());


    }
}
