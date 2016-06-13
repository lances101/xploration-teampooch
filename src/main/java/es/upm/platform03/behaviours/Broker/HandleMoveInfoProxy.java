package es.upm.platform03.behaviours.Broker;

import es.upm.platform03.World;
import es.upm.platform03.XplorationMap;
import jade.core.AID;
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
        if(XplorationMap.getRoverPosition(msg.getSender()) == null) return;
        AID[] receivers = XplorationMap.getAgentsInRange(3, msg.getSender());
        if(receivers.length > 0){
            for (AID rec : receivers)
                if(rec != msg.getSender())
                    msg.addReceiver(rec);
            agent.send(msg);
        }
    }
}
