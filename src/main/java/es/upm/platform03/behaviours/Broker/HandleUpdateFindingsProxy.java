package es.upm.platform03.behaviours.Broker;

import es.upm.platform03.World;
import es.upm.platform03.XplorationMap;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleUpdateFindingsProxy extends CyclicBehaviour{
    World agent;
    MessageTemplate mtUpdateFindings;
    public HandleUpdateFindingsProxy(World agent) {
        this.agent = agent;
        mtUpdateFindings = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.and(agent.getMtOntoAndCodec(),
                        MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_UPDATE_FINDINGS)));
    }

    @Override
    public void action() {
        ACLMessage msg = agent.receive(mtUpdateFindings);
        if (msg == null) {
            block();
            return;
        }
        msg.removeReceiver(agent.getAID());
        try {
            Action act = (Action) agent.getContentManager().extractContent(msg);
            
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }

        AID[] receivers = XplorationMap.getAgentsInRange(3, msg.getSender());
        if(receivers.length > 0){
            for (AID rec : receivers)
                msg.addReceiver(rec);
            agent.send(msg);
        }
    }
}
