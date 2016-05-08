package es.upm.platform03.behaviours;

import es.upm.company03.common.CompanyAIDTuple;
import es.upm.company03.common.RFBAgent;
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
public class RegistrationBehavior extends CyclicBehaviour {

    RFBAgent agent;
    DateTime registrationDeadline;
    ArrayList<CompanyAIDTuple> companies;

    public RegistrationBehavior(RFBAgent agent, int registrationSeconds, ArrayList<CompanyAIDTuple> companies) {
        super(agent);
        this.agent = agent;
        this.registrationDeadline = DateTime.now().plusSeconds(registrationSeconds);
        this.companies = companies;
    }

    @Override
    public void action() {
        //jade sometimes fires messages on nulls. cool.
        ACLMessage msg = myAgent.receive();
        if (msg == null) {
            block();
            return;
        }
            /*
            extensive template. you should only ever need to change the
            protocol and the performative
             */
        MessageTemplate mtAll =
                MessageTemplate.and(agent.getMtOntoAndCodec(),
                        MessageTemplate.and(
                                MessageTemplate.MatchProtocol(agent.getOntology().PROTOCOL_REGISTRATION),
                                MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                        )
                );

        if (!mtAll.match(msg)) {
            agent.replyWithNotUnderstood(msg);
            block();
            return;
        }
        //TODO:code above will be redundant for many behaviors. consider extracting

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

            if (companies.stream().anyMatch(companyTuple -> companyTuple.getCompany() == senderID)) {
                replyInform.setPerformative(ACLMessage.FAILURE);
            } else {
                companies.add(new CompanyAIDTuple(regReq.getCompany(), msg.getSender()));
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
