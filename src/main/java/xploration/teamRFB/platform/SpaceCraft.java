package xploration.teamRFB.platform;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.DateTime;
import xploration.teamRFB.common.RFBAgent;

import java.util.ArrayList;

public class Spacecraft extends RFBAgent{

    ArrayList<AID> companies = new ArrayList<>();
    DateTime registrationEnd;


    @Override
    protected void setup() {
        super.setup();
        registerSelfWithServices(new String[]{"Spacecraft"});
        registrationEnd = DateTime.now().plusSeconds(10);
        System.out.printf("%s: registration is up. Registration ends at %s%n",
                getLocalName(),
                registrationEnd.toString());

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                if(msg == null) return;
                if (msg.getProtocol().equalsIgnoreCase("Registration")) {
                    ACLMessage reply = msg.createReply();
                    System.out.printf("%s: got new registration request from %s to register '%s'%n",
                            getLocalName(), msg.getSender().getLocalName(), msg.getContent());
                    if(registrationEnd.isBeforeNow()) {
                        reply.setPerformative(ACLMessage.REFUSE);
                        send(reply);
                        return;
                    }
                    reply.setPerformative(ACLMessage.AGREE);
                    send(reply);

                    ACLMessage replyInform = msg.createReply();
                    AID sender = msg.getSender();
                    if(companies.contains(sender)) {
                        replyInform.setPerformative(ACLMessage.FAILURE);
                    }
                    else{
                        System.out.printf("%s: team '%s' registered.%n",
                                getLocalName(), msg.getContent());
                        companies.add(sender);
                        replyInform.setPerformative(ACLMessage.INFORM);
                    }
                    send(replyInform);

                } else {
                    replyWithNotUnderstood(msg);
                }
            }
        });

    }
}
