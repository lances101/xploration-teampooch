package es.upm.platform03.behaviours.Broker;

import es.upm.common03.TeamAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by borismakogonyuk on 25.05.16.
 */
public class HandleMessageProxy extends CyclicBehaviour {
    TeamAgent agent;
    public HandleMessageProxy(TeamAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage message = agent.receive();
        message.removeReceiver(agent.getAID());

    }
}
