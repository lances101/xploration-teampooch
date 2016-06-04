package es.upm.platform03.behaviours.World;

import es.upm.ontology.Location;
import es.upm.ontology.Mineral;
import es.upm.ontology.MineralResult;
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
public class HandleRoverResearchReply extends TickerBehaviour {
    World agent;

    ArrayList<Pair<ACLMessage, DateTime>> researchConvo;

    public HandleRoverResearchReply(World a, long period, ArrayList<Pair<ACLMessage, DateTime>> researchConvo) {
        super(a, period);
        this.agent = a;
        this.researchConvo = researchConvo;

    }

    @Override
    protected void onTick() {
        for (int i = 0; i < researchConvo.size(); i++) {
            if (researchConvo.get(i).getValue().isAfterNow()) continue;
            ACLMessage msg = researchConvo.get(i).getKey();
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            Location loc = XplorationMap.getRoverPosition(msg.getSender());
            String mineralType = XplorationMap.getMineralAtPosition(loc);

            MineralResult minResult = new MineralResult();
            Mineral mineral = new Mineral();
            mineral.setType(mineralType);
            minResult.setMineral(mineral);
            try {
                agent.getContentManager().fillContent(reply, new Action(agent.getAID(), minResult));
                agent.send(reply);
            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }

            researchConvo.remove(i);
            i--;
            System.out.printf("Rover %s has finished research%n", msg.getSender().getLocalName());
        }
    }
}
