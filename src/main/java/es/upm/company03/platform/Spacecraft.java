package es.upm.company03.platform;

import es.upm.company03.common.RFBAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.DateTime;

import java.util.ArrayList;

public class Spacecraft extends RFBAgent {

    ArrayList<AID> companies = new ArrayList<AID>();
    DateTime registrationEnd;


    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchProtocol("Registration"));
                if(msg == null){
                    block();
                    return;
                }
                if (msg.getPerformative() == ACLMessage.REQUEST) {
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

        super.setup();
        registrationEnd = DateTime.now().plusSeconds(60);
        System.out.printf("%s: registration is up! Registration ends at %s%n", getLocalName(), registrationEnd.toString("HH:mm:ss"));

        registerSelfWithServices(new String[]{"SPACECRAFT"});

    }
}
