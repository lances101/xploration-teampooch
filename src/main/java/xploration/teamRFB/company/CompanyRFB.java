package xploration.teamRFB.company;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class CompanyRFB extends Agent{

    public void registerSelf()
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.getName());
        sd.setType("COMPANY");
        dfd.addServices(sd);
        // Registers its description in the DF
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println(getLocalName()+": registered in the DF");
        dfd = null;
        sd = null;
    }
    @Override
    protected void setup() {
        System.out.println("BOOM SHAKALAKA, I'M ONLINE");
        registerSelf();

        addBehaviour(new SimpleBehaviour(this) {



            @Override
            public void action() {
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("COMPANY");
                dfd.addServices(sd);

                DFAgentDescription[] found;
                try {
                    found = DFService.search(myAgent, dfd);
                    if(found.length == 0){
                        System.out.println(myAgent.getName() + " - Search yielded nothing. Waiting.");
                        doWait(5000);
                        return;
                    }
                    for(DFAgentDescription agent : found)
                    {
                        if(!agent.getName().equals(myAgent.getName()))
                            System.out.println(myAgent.getName() + " - Found " + agent.getName());

                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
                doWait(3000);
            }

            @Override
            public boolean done() {
                return false;
            }
        });

    }
}
