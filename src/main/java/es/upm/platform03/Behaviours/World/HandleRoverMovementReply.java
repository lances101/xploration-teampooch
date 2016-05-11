package es.upm.platform03.behaviours.World;

import es.upm.platform03.World;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;
import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by borismakogonyuk on 10.05.16.
 */
public class HandleRoverMovementReply extends TickerBehaviour {
    AID mapAID;
    World agent;

    ArrayList<Pair<ACLMessage, DateTime>> moveConvo;
    public HandleRoverMovementReply(World a, long period, AID mapAID, ArrayList<Pair<ACLMessage, DateTime>> moveConvo) {
        super(a, period);
        this.agent = a;
        this.mapAID = mapAID;
        this.moveConvo = moveConvo;

    }

    @Override
    protected void onTick() {
        for(int i = 0; i < moveConvo.size(); i++)
        {
            if(moveConvo.get(i).getValue().isAfterNow()) continue;
            ACLMessage msg = moveConvo.get(i).getKey();
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            agent.send(reply);
            moveConvo.remove(i);
            i--;
            System.out.printf("Rover %s has finished movement%n", msg.getSender().getLocalName());
        }
    }
}
