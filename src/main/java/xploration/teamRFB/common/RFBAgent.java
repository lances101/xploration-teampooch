package xploration.teamRFB.common;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RFBAgent extends Agent{

    @Override
    protected void setup() {
        addBehaviour(NotUnderStoodBehavior);
    }

    protected CyclicBehaviour NotUnderStoodBehavior = new CyclicBehaviour() {
        @Override
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
            if(msg == null) return;
            System.out.println(getName() + ": got [NOT_UNDERSTOOD] from " + msg.getSender());
        }
    };

    protected void registerSelf(String type)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.getName());
        sd.setType(type);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        System.out.println(getName() + ": registered as " + type);
        dfd = null;
        sd = null;
    }

    protected void replyWithNotUnderstood(ACLMessage msg)
    {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
        reply.setContent("'"+msg.getProtocol() + "' protocol not understood");
        send(reply);
        System.out.printf("%s could not understand message from [%s] with protocol '%s'%n",
                getName(),
                msg.getSender(),
                msg.getProtocol()
        );
    }

}
