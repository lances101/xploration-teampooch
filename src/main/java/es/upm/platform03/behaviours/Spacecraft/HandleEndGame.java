package es.upm.platform03.behaviours.Spacecraft;

import es.upm.common03.TeamAgent;
import es.upm.ontology.Company;
import es.upm.platform03.Spacecraft;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.HashMap;

public class HandleEndGame extends OneShotBehaviour {

    private final TeamAgent agent;
    private final ArrayList<Company> companies;
    private final HashMap<AID, Integer> scores;

    public HandleEndGame(Spacecraft spacecraft, ArrayList<Company> companies, HashMap<AID, Integer> scores) {
        this.agent = spacecraft;
        this.companies = companies;
        this.scores = scores;
    }

    @Override
    public void action() {
        //Now kill all
        try {
            agent.getContainerController().kill();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        System.out.println(" = = = Now printing scores = = = ");
        for (Company comp : companies){
            System.out.println(String.format("%s - %d%n", comp.getCompany_agent().getLocalName(), scores.get(comp.getCompany_agent())));
        }
        System.out.println(" = = = = = = = = = = = = = = = = ");


    }
}
