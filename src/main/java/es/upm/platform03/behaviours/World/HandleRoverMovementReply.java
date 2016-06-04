package es.upm.platform03.behaviours.World;


import es.upm.common03.LocationUtility;
import es.upm.ontology.Location;
import es.upm.ontology.RequestRoverMovement;
import es.upm.platform03.World;
import es.upm.platform03.XplorationMap;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;
import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by borismakogonyuk on 10.05.16.
 */
public class HandleRoverMovementReply extends TickerBehaviour {
    World agent;

    ArrayList<Pair<ACLMessage, DateTime>> moveConvo;

    public HandleRoverMovementReply(World a, long period, ArrayList<Pair<ACLMessage, DateTime>> moveConvo) {
        super(a, period);
        this.agent = a;
        this.moveConvo = moveConvo;

    }

    @Override
    protected void onTick() {
        for(int i = 0; i < moveConvo.size(); i++)
        {
            if(moveConvo.get(i).getValue().isAfterNow()) continue;
            ACLMessage msg = moveConvo.get(i).getKey();

            Location loc = unpackAndCalculatePosition(msg);
            if(loc == null) {
                agent.replyWithNotUnderstood(msg);
                continue;
            }
            XplorationMap.updateRoverPosition(msg.getSender(), loc);

            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            agent.send(reply);
            moveConvo.remove(i);
            i--;

            System.out.printf("Moved %s to %d|%d%n", msg.getSender().getLocalName(), loc.getX(), loc.getY());
        }
    }

    private Location unpackAndCalculatePosition(ACLMessage msg)
    {
        try {
            Action action = (Action) agent.getContentManager().extractContent(msg);
            RequestRoverMovement movement = (RequestRoverMovement) action.getAction();
            int direction = movement.getDirection().getX();
            Location location = XplorationMap.getRoverPosition(msg.getSender());
            Location newLocation = LocationUtility.calculateNewLocation(location, direction, XplorationMap.getSizeX(), XplorationMap.getSizeY());
            return newLocation;
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
