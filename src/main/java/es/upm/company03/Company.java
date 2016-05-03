package es.upm.company03;

import es.upm.company03.common.RFBAgent;
import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.ReleaseCapsule;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;


/**
 * Company agent. Handles the initialization of other agents of the company.
 * Handles the following behaviors:
 * <br>
 *  - Company registration
 */
public class Company extends RFBAgent {


    final static String companySuffix = "03";
    SimpleBehaviour releaseCapsuleBehavior = new SimpleBehaviour() {
        boolean capsuleReleased = false;

        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg == null) {
                block();
                return;
            }
            MessageTemplate mtAll =
                    MessageTemplate.and(mtOntoAndCodec,
                            MessageTemplate.and(
                                    MessageTemplate.MatchProtocol(ontology.PROTOCOL_RELEASE_CAPSULE),
                                    MessageTemplate.MatchPerformative(ACLMessage.INFORM)
                            )
                    );
            if (!mtAll.match(msg)) {
                replyWithNotUnderstood(msg);
                block();
                return;
            }
            try {
                Action ce = (Action) getContentManager().extractContent(msg);
                ReleaseCapsule releaseCap = (ReleaseCapsule) ce.getAction();
                AgentController ac = myAgent.getContainerController().createNewAgent("Capsule" + companySuffix, "es.upm.company03.Capsule", new Object[]{releaseCap.getLocation(),});
                ac.start();
                capsuleReleased = true;
                System.out.printf("%s: Capsule released!%n", getLocalName());


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
            return capsuleReleased;
        }
    };
    /**
     * Registation behavior with States.
     * Ends if succeeds and tries 3 times if it fails.
     */
    SimpleBehaviour registrationBehavior = new SimpleBehaviour() {

        final static int maxRegTries = 3;
        final static String companyName = "Company" + companySuffix;
        REGSTATE regState = REGSTATE.START;
        int regTries = 0;

        @Override
        public void action() {
            switch (regState) {
                case START:
                    Register();
                    regTries++;
                    break;
                case WAITING:
                    HandleMessages();
                    break;
                case FAILED:
                    if (regTries < maxRegTries) {
                        regState = REGSTATE.START;
                    } else {
                        regState = REGSTATE.END;
                    }
                    break;
            }
        }

        @Override
        public boolean done() {
            return regState == REGSTATE.END;
        }

        void Register() {

            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Spacecraft");
            dfd.addServices(sd);

            DFAgentDescription[] found;
            try {
                found = DFService.search(myAgent, dfd);
                if (found.length == 0) {
                    System.out.printf("%s: Search yielded nothing. Waiting.%n",
                            myAgent.getLocalName());
                    doWait(3000);
                    regTries = 0;
                    return;
                }
                System.out.printf("%s: found %d Spacecrafts.%n",
                        myAgent.getLocalName(), found.length);
                for (DFAgentDescription agent : found) {
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.setProtocol(ontology.PROTOCOL_REGISTRATION);
                    message.setOntology(ontology.getName());
                    message.setLanguage(codec.getName());
                    message.addReceiver(agent.getName());

                    RegistrationRequest regReq = new RegistrationRequest();
                    regReq.setCompany(companyName);

                    getContentManager().fillContent(message, new Action(getAID(), regReq));

                    send(message);
                    System.out.printf("%s: requesting registration from Spacecraft%n",
                            getLocalName());
                }
                regState = REGSTATE.WAITING;
                return;
            } catch (FIPAException e) {
                e.printStackTrace();
            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }
        }

        void HandleMessages() {
            ACLMessage msg = receive();
            if (msg == null) {
                block();
                return;
            }
            MessageTemplate mtAll = MessageTemplate.and(mtOntoAndCodec, MessageTemplate.MatchProtocol(ontology.PROTOCOL_REGISTRATION));
            if (!mtAll.match(msg)) {
                replyWithNotUnderstood(msg);
                block();
                return;
            }

            switch (msg.getPerformative()) {
                case ACLMessage.AGREE:
                    System.out.printf("%s: Spacecraft sent 'AGREE'. Waiting!%n",
                            getLocalName());
                    break;
                case ACLMessage.REFUSE:
                    System.out.printf("%s: Spacecraft sent 'REFUSE'. It appears we are late, tough luck. %n",
                            getLocalName());
                    regState = REGSTATE.FAILED;
                    break;
                case ACLMessage.FAILURE:
                    System.out.printf("%s: Spacecraft sent 'FAILURE'. We are already registered!%n",
                            getLocalName());
                    regState = REGSTATE.FAILED;
                    break;
                case ACLMessage.INFORM:
                    System.out.printf("%s: Spacecraft sent 'INFORM'. We are registered!%n",
                            getLocalName());
                    regState = REGSTATE.END;
                    //TODO: handle this with an FSM
                    addBehaviour(releaseCapsuleBehavior);
                    break;
                default:
                    replyWithNotUnderstood(msg);
                    break;
            }
        }
    };

    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", "Company" + companySuffix);

        //We define the behaviours outside and then add them.
        //Helps to keep the code more structurized and we can
        //do shit like moving the registration in the DFService
        //after adding the behaviors without much hassle.
        //TODO: remove this comment by 02/05/2016
        registrationBehavior.setAgent(this);
        addBehaviour(registrationBehavior);

        releaseCapsuleBehavior.setAgent(this);

        registerSelfWithServices(new String[]{"Company03"});
        super.setup();


    }

    /**
     * States for registration behavior.
     * Here because inner classes cannot have
     * static declarations.
     */
    enum REGSTATE {
        START,
        WAITING,
        FAILED,
        END
    }
}
