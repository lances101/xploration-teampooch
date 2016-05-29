package es.upm.platform03.behaviors.Spacecraft;

import es.upm.common03.TeamAgent;
import es.upm.ontology.Company;
import es.upm.ontology.RegistrationRequest;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by borismakogonyuk on 08.05.16.
 */
public class HandleRegistrationRequest extends CyclicBehaviour {

    TeamAgent agent;
    DateTime registrationDeadline;
    ArrayList<Company> companies;

    public HandleRegistrationRequest(TeamAgent agent, ArrayList<Company> companies, int registrationSeconds) {
        super(agent);
        this.agent = agent;
        this.registrationDeadline = DateTime.now().plusSeconds(registrationSeconds);
        this.companies = companies;
        matchRegRequest =
                MessageTemplate.and(agent.getMtOntoAndCodec(),
                        MessageTemplate.and(
                                MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_REGISTRATION),
                                MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                        )
                );
    }

    MessageTemplate matchRegRequest;
    @Override
    public void action() {
        ACLMessage msg = agent.receive(matchRegRequest);
        if (msg == null) {
            block();
            return;
        }
        ACLMessage reply = msg.createReply();
        System.out.printf("%s: got new reg request from %s%n",
                agent.getLocalName(), msg.getSender().getLocalName());
        if (registrationDeadline.isBeforeNow()) {
            reply.setPerformative(ACLMessage.REFUSE);
            agent.send(reply);
            return;
        }
        reply.setPerformative(ACLMessage.AGREE);
        agent.send(reply);

        ACLMessage replyInform = msg.createReply();
        AID senderID = msg.getSender();

        try {
            Action ac = (Action) agent.getContentManager().extractContent(msg);
            RegistrationRequest regReq = (RegistrationRequest) ac.getAction();

            if (companies.stream().anyMatch(companyTuple -> companyTuple.getCompany_agent() == senderID)) {
                replyInform.setPerformative(ACLMessage.FAILURE);
            } else {
                Company company = new Company();
                company.setCompany_agent(msg.getSender());
                companies.add(company);
                System.out.printf("%s: team '%s' registered, informing...%n", agent.getLocalName(), regReq.getCompany());
                replyInform.setPerformative(ACLMessage.INFORM);
            }

        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }


            /*
                Artificial delay to account for other agents, since some of them
                manage to handle the AGREE message after the inform one.
             */
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        agent.send(replyInform);
                    }
                },
                2000
        );
    }
}
