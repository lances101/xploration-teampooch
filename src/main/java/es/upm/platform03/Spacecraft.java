package es.upm.platform03;

import es.upm.company03.common.RFBAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Spacecraft agent. Handles the initialization of other agents of the platform.
 * Handles the following behaviors:
 * <br>
 * - Company registration
 */
public class Spacecraft extends RFBAgent {

    ArrayList<AID> companies = new ArrayList<AID>();
    DateTime registrationDeadline;
    /**
     * Handles the registration.
     * Accepts only REQUEST with `Registration` protocol.
     */
    CyclicBehaviour registrationBehavior = new CyclicBehaviour() {
        @Override
        public void action() {
            //jade sometimes fires messages on nulls. cool.
            ACLMessage msg = receive();
            if (msg == null) {
                block();
                return;
            }
            /*
            extensive template. you should only ever need to change the
            protocol and the performative
             */
            MessageTemplate mtAll =
                    MessageTemplate.and(mtOntoAndCodec,
                            MessageTemplate.and(
                                    MessageTemplate.MatchProtocol(ontology.PROTOCOL_REGISTRATION),
                                    MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                            )
                    );

            if (!mtAll.match(msg)) {
                replyWithNotUnderstood(msg);
                block();
                return;
            }
            //TODO:code above will be redundant for many behaviors. consider extracting


            ACLMessage reply = msg.createReply();
            System.out.printf("%s: got new reg request from %s for '%s'%n",
                    getLocalName(), msg.getSender().getLocalName(), msg.getContent());
            if (registrationDeadline.isBeforeNow()) {
                reply.setPerformative(ACLMessage.REFUSE);
                send(reply);
                return;
            }
            reply.setPerformative(ACLMessage.AGREE);
            send(reply);

            ACLMessage replyInform = msg.createReply();
            AID senderID = msg.getSender();

            if (companies.contains(senderID)) {
                replyInform.setPerformative(ACLMessage.FAILURE);
            } else {
                System.out.printf("%s: team '%s' registered, informing...%n", getLocalName(), msg.getContent());
                companies.add(senderID);
                replyInform.setPerformative(ACLMessage.INFORM);
            }

            /*
                Artificial delay to account for other agents, since some of them
                manage to handle the AGREE message after the inform one.
             */
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            send(replyInform);
                        }
                    },
                    2000
            );
        }
    };

    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", "Spacecraft03");

        //See comment in Company.java
        registrationBehavior.setAgent(this);
        addBehaviour(registrationBehavior);

        registrationDeadline = DateTime.now().plusSeconds(60);
        System.out.printf("%s: registration is up! Registration ends at %s%n",
                getLocalName(), registrationDeadline.toString("HH:mm:ss"));

        registerSelfWithServices(new String[]{ontology.PROTOCOL_REGISTRATION});
        super.setup();
    }
}
