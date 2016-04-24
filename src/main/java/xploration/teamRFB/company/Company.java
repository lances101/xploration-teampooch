package xploration.teamRFB.company;

import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import xploration.teamRFB.common.RFBAgent;

public class Company extends RFBAgent{
    public final static String CompanyName = "Pooch";
    public final static String REGISTRATION = "Registration";
    public boolean regDone = false;
    @Override
    protected void setup() {
        super.setup();
        //registerSelfWithServices(new String[]{"Company"});

        addBehaviour(new OneShotBehaviour(this) {
            @Override
            public void action() {
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("Spacecraft");
                dfd.addServices(sd);

                DFAgentDescription[] found;
                try {
                    found = DFService.search(myAgent, dfd);
                    if(found.length == 0){
                        System.out.println(myAgent.getLocalName() + " - Search yielded nothing. Waiting.");
                        doWait(5000);
                        return;
                    }
                    for(DFAgentDescription agent : found)
                    {
                        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                        message.setProtocol(REGISTRATION);
                        message.setContent(CompanyName);
                        message.addReceiver(agent.getName());
                        send(message);
                        System.out.println(getLocalName() + ": requesting registration from Spacecraft");
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        addBehaviour(new SimpleBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchProtocol(REGISTRATION));
                if(msg == null) return;
                switch(msg.getPerformative())
                {
                    case ACLMessage.AGREE:
                        System.out.println(getLocalName() + ": Spacecraft sent 'AGREE'. Waiting!");
                        break;
                    case ACLMessage.REFUSE:
                        System.out.println(getLocalName() + ": Spacecraft sent 'REFUSE'. It appears we are late, tough luck. ");
                        regDone = true;
                        break;
                    case ACLMessage.FAILURE:
                        System.out.println(getLocalName() + ": Spacecraft sent 'FAILURE'. We are already registered!");
                        regDone = true;
                        break;
                    case ACLMessage.INFORM:
                        System.out.println(getLocalName() + ": Spacecraft sent 'INFORM'. We are registered!");
                        regDone = true;
                        break;
                    default:
                        replyWithNotUnderstood(msg);
                        break;
                }

            }

            @Override
            public boolean done() {
                return regDone;
            }
        });



    }
}
