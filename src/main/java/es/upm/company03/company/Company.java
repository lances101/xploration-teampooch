package es.upm.company03.company;

import es.upm.company03.common.RFBAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Company agent. Handles the initialization of other agents of the company.
 * Handles the following behaviors:
 * <br>
 *  - Company registration
 */
public class Company extends RFBAgent {

    final static String companyName = "Company03";

    @Override
    protected void setup() {
        //We define the behaviours outside and then add them.
        //Helps to keep the code more structurized and we can
        //do shit like moving the registration in the DFService
        //after adding the behaviors without much hassle.
        //TODO: remove this comment by 02/05/2016
        registrationBehavior.setAgent(this);
        addBehaviour(registrationBehavior);

        registerSelfWithServices(new String[]{"Company"});
        super.setup();
    }

    /**
     * States for registration behavior.
     * Here because inner classes cannot have
     * static declarations.
     */
    enum REGSTATE{
        START,
        WAITING,
        FAILED,
        END
    }
    /**
     * Registation behavior with States.
     * Ends if succeeds and tries 3 times if it fails.
     */
    SimpleBehaviour registrationBehavior = new SimpleBehaviour() {

        REGSTATE regState = REGSTATE.START;
        int regTries = 0;
        final static int maxRegTries = 3;
        final static String pclNameRegistration = "Registration";

        @Override
        public void action() {
            switch (regState)
            {
                case START:
                    Register();
                    regTries++;
                    break;
                case WAITING:
                    HandleMessages();
                    break;
                case FAILED:
                    if(regTries < maxRegTries)
                    {
                        regState = REGSTATE.START;
                    }
                    else {
                        regState = REGSTATE.END;
                    }
                    break;
            }
        }

        @Override
        public boolean done() {
            return regState == REGSTATE.END;
        }
        void Register()
        {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Spacecraft");
            dfd.addServices(sd);

            DFAgentDescription[] found;
            try {
                found = DFService.search(myAgent, dfd);
                if (found.length == 0) {
                    System.out.println(myAgent.getLocalName() + ": Search yielded nothing. Waiting.");
                    doWait(3000);
                    regTries = 0;
                    return;
                }
                System.out.printf("%s: found %d Spacecrafts.%n", myAgent.getLocalName(), found.length);
                for (DFAgentDescription agent : found) {
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setProtocol(pclNameRegistration);
                    message.setContent(companyName);
                    message.addReceiver(agent.getName());
                    send(message);
                    System.out.println(getLocalName() + ": requesting registration from Spacecraft");
                }
                System.out.println(getLocalName() + ": messages sent. Now waiting. ");
                regState = REGSTATE.WAITING;
                return;
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }
        void HandleMessages()
        {
            ACLMessage msg = receive(MessageTemplate.MatchProtocol(pclNameRegistration));
            if(msg == null){
                block();
                return;
            }
            switch(msg.getPerformative())
            {
                case ACLMessage.AGREE:
                    System.out.println(getLocalName() + ": Spacecraft sent 'AGREE'. Waiting!");
                    break;
                case ACLMessage.REFUSE:
                    System.out.println(getLocalName() + ": Spacecraft sent 'REFUSE'. It appears we are late, tough luck. ");
                    regState = REGSTATE.FAILED;
                    break;
                case ACLMessage.FAILURE:
                    System.out.println(getLocalName() + ": Spacecraft sent 'FAILURE'. We are already registered!");
                    regState = REGSTATE.FAILED;
                    break;
                case ACLMessage.INFORM:
                    System.out.println(getLocalName() + ": Spacecraft sent 'INFORM'. We are registered!");
                    regState = REGSTATE.END;
                    break;
                default:
                    replyWithNotUnderstood(msg);
                    break;
            }
        }
    };
}
