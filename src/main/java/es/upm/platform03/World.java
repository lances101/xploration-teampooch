package es.upm.platform03;

import es.upm.common03.RFBAgent;
import es.upm.platform03.behaviours.HandleRoverMovementRequest;
import jade.core.AID;
import jade.core.NotFoundException;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;


public class World extends RFBAgent {

    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", getLocalName());
        registerSelfWithServices(new String[]{"World"});
        super.setup();

        try {
            addBehaviour(new HandleRoverMovementRequest(this, findMapService()));
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
