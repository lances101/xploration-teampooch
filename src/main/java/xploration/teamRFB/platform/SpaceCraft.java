package xploration.teamRFB.platform;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.DateTime;
import xploration.teamRFB.common.RFBAgent;

import java.util.ArrayList;

public class Spacecraft extends RFBAgent{

    ArrayList<AID> companies = new ArrayList<AID>();
    DateTime registrationEnd;


    @Override
    protected void setup() {
        super.setup();
        registerSelf("Spacecraft");
        registrationEnd = DateTime.now().plusSeconds(60);
        System.out.printf("%s: registration is up. Registration ends at %s%n",
                getName(),
                registrationEnd.toString());

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                if(msg == null) return;
                if (msg.getProtocol().equalsIgnoreCase("Registration")) {
                    ACLMessage reply = msg.createReply();
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
