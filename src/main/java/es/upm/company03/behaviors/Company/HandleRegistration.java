package es.upm.company03.behaviors.Company;

import es.upm.common03.TeamAgent;
import es.upm.ontology.RegistrationRequest;
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

/**
 * Created by borismakogonyuk on 08.05.16.
 */
public class HandleRegistration extends SimpleBehaviour {
    /**
     * Registation behavior with States.
     * Ends if succeeds and tries 3 times if it fails.
     */
    final static int maxRegTries = 3;
    final static String companyName = "Company";
    REGSTATE regState = REGSTATE.START;
    int regTries = 0;
    TeamAgent agent;
    MessageTemplate matchReplies;

    public HandleRegistration(TeamAgent agent) {
        super(agent);
        this.agent = agent;
    }

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
        if(regState == REGSTATE.END)
        {
            agent.removeBehaviour(this);
            return true;
        }
        return false;
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
                agent.doWait(3000);
                regTries = 0;
                return;
            }
            System.out.printf("%s: found %d Spacecrafts.%n",
                    myAgent.getLocalName(), found.length);
            for (DFAgentDescription ag : found) {
                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                message.setProtocol(agent.getxOntology().PROTOCOL_REGISTRATION);
                message.setOntology(agent.getxOntology().getName());
                message.setLanguage(agent.getCodec().getName());
                message.addReceiver(ag.getName());

                RegistrationRequest regReq = new RegistrationRequest();
                regReq.setCompany(companyName);

                agent.getContentManager().fillContent(message, new Action(agent.getAID(), regReq));

                agent.send(message);
                System.out.printf("%s: requesting registration from Spacecraft%n",
                        agent.getLocalName());
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
        if(matchReplies == null)
        {
            matchReplies = MessageTemplate.and(agent.getMtOntoAndCodec(), MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_REGISTRATION));
        }
        ACLMessage msg = agent.receive(matchReplies);
        if (msg == null) {
            block();
            return;
        }

        switch (msg.getPerformative()) {
            case ACLMessage.AGREE:
                System.out.printf("%s: Spacecraft sent 'AGREE'. Waiting!%n",
                        agent.getLocalName());
                break;
            case ACLMessage.REFUSE:
                System.out.printf("%s: Spacecraft sent 'REFUSE'. It appears we are late, tough luck. %n",
                        agent.getLocalName());
                regState = REGSTATE.FAILED;
                break;
            case ACLMessage.FAILURE:
                System.out.printf("%s: Spacecraft sent 'FAILURE'. We are already registered!%n",
                        agent.getLocalName());
                regState = REGSTATE.FAILED;
                break;
            case ACLMessage.INFORM:
                System.out.printf("%s: Spacecraft sent 'INFORM'. We are registered!%n",
                        agent.getLocalName());
                regState = REGSTATE.END;
                break;
            default:
                agent.replyWithNotUnderstood(msg);
                break;
        }
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
