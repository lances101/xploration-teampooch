package es.upm.platform03;

import com.sun.tools.javac.util.Pair;
import es.upm.common03.RFBAgent;
import es.upm.platform03.behaviours.World.HandleRoverMovementReply;
import es.upm.platform03.behaviours.World.HandleRoverMovementRequest;
import jade.core.AID;
import jade.core.NotFoundException;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.joda.time.DateTime;

import java.util.ArrayList;


public class World extends RFBAgent {

    ArrayList<Pair<ACLMessage, DateTime>> moveConvo = new ArrayList<>();
    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", getLocalName());
        registerSelfWithServices(new String[]{"World"});
        super.setup();

        try {
            addBehaviour(new HandleRoverMovementRequest(this, moveConvo));
            addBehaviour(new HandleRoverMovementReply(this, 250, findMapService(), moveConvo));
        } catch (NotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not find map. SUDOKU!");
            doDelete();
        }

    }

    AID findMapService() throws NotFoundException {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Map");
        dfd.addServices(sd);

        DFAgentDescription[] found;
        try {
            found = DFService.search(this, dfd);
            if (found.length == 0) {
                System.out.printf("%s: Search yielded nothing. Waiting.%n",
                        this.getLocalName());
                throw new NotFoundException("Could not find World.");
            }
            return found[0].getName();

        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return null;
    }
}
