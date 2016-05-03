package es.upm.platform03;

import es.upm.company03.common.CompanyAIDTuple;
import es.upm.company03.common.RFBAgent;
import es.upm.ontology.Location;
import es.upm.ontology.RegistrationRequest;
import es.upm.ontology.ReleaseCapsule;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Random;

/**
 * Spacecraft agent. Handles the initialization of other agents of the platform.
 * Handles the following behaviors:
 * <br>
 * - Company registration
 */
public class Spacecraft extends RFBAgent {

    ArrayList<CompanyAIDTuple> companies = new ArrayList<CompanyAIDTuple>();
    long registrationPeriodTicks = 10000;
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
            System.out.printf("%s: got new reg request from %s%n",
                    getLocalName(), msg.getSender().getLocalName());
            if (registrationDeadline.isBeforeNow()) {
                reply.setPerformative(ACLMessage.REFUSE);
                send(reply);
                return;
            }
            reply.setPerformative(ACLMessage.AGREE);
            send(reply);

            ACLMessage replyInform = msg.createReply();
            AID senderID = msg.getSender();

            try {
                Action ac = (Action) getContentManager().extractContent(msg);
                RegistrationRequest regReq = (RegistrationRequest) ac.getAction();

                if (companies.stream().anyMatch(companyTuple -> companyTuple.getCompany() == senderID)) {
                    replyInform.setPerformative(ACLMessage.FAILURE);
                } else {
                    companies.add(new CompanyAIDTuple(regReq.getCompany(), msg.getSender()));
                    System.out.printf("%s: team '%s' registered, informing...%n", getLocalName(), regReq.getCompany());
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
                            send(replyInform);
                        }
                    },
                    2000
            );
        }
    };
    WakerBehaviour releaseCapsuleBehavior = new WakerBehaviour(null, registrationPeriodTicks) {
        @Override
        protected void onWake() {
            System.out.printf("%s: sending ReleaseCapsule to %d companies%n",
                    getLocalName(), companies.size());
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setOntology(ontology.getName());
            msg.setLanguage(codec.getName());
            msg.setProtocol(ontology.PROTOCOL_RELEASE_CAPSULE);
            for (CompanyAIDTuple tuple : companies)
                msg.addReceiver(tuple.getCompany());

            //TODO: calculate real position. for now - random
            Random rnd = new Random();

            ReleaseCapsule releaseCapsule = new ReleaseCapsule();
            Location location = new Location();
            location.setX(rnd.nextInt(100));
            location.setY(rnd.nextInt(100));
            releaseCapsule.setLocation(location);
            try {
                getContentManager().fillContent(msg, new Action(getAID(), releaseCapsule));
                send(msg);
            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", getLocalName());

        //See comment in Company.java
        registrationBehavior.setAgent(this);
        addBehaviour(registrationBehavior);

        releaseCapsuleBehavior.setAgent(this);
        releaseCapsuleBehavior.restart();
        addBehaviour(releaseCapsuleBehavior);

        registrationDeadline = DateTime.now().plusSeconds(10);
        System.out.printf("%s: registration is up! Registration ends at %s%n",
                getLocalName(), registrationDeadline.toString("HH:mm:ss"));

        registerSelfWithServices(new String[]{"Spacecraft"});
        super.setup();
    }


}
