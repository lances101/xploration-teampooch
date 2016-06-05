package es.upm.platform03.behaviours.Spacecraft;

import es.upm.common03.TeamAgent;
import es.upm.ontology.Company;
import es.upm.ontology.Finding;
import es.upm.ontology.FindingsMessage;
import es.upm.platform03.XplorationMap;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by borismakogonyuk on 05.06.16.
 */
public class HandleUpdateFindings extends CyclicBehaviour {
    private final TeamAgent agent;
    private final MessageTemplate mtUpdateFindings;
    private final ArrayList<Company> companies;
    private final HashMap<AID, Integer> scores;
    private AID[][] findings;

    public HandleUpdateFindings(TeamAgent agent, ArrayList<Company> companies, HashMap<AID, Integer> scores){
        this.agent = agent;
        this.companies = companies;
        this.scores = scores;
        mtUpdateFindings = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.and(agent.getMtOntoAndCodec(),
                        MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_UPDATE_FINDINGS)));
    }
    @Override
    public void action() {
        ACLMessage msg = agent.receive(mtUpdateFindings);
        if(msg == null){
            block();
            return;
        }
        System.out.println("HANDLING FINDINGS FOR " + msg.getSender().getLocalName());
        if(findings == null) findings = new AID[XplorationMap.getSizeY()+1][XplorationMap.getSizeX()+1];
        try {
            Company company = companies.stream().filter((c)->c.getCapsule().getCapsule_agent() == msg.getSender()).findFirst().get();
            Action act = (Action) agent.getContentManager().extractContent(msg);
            FindingsMessage findingMessage = (FindingsMessage) act.getAction();
            ArrayList<Finding> tempFindings = (ArrayList<Finding>) findingMessage.getFindings().getFinding();
            for(Finding finding : tempFindings){
                String realMineral = XplorationMap.getMineralAtPosition(finding.getLocation());
                if(!realMineral.equalsIgnoreCase(finding.getMineral().getType())){
                    updateScore(company.getCompany_agent(), -1);
                }
                else if (findings[finding.getLocation().getY()][finding.getLocation().getX()] == null){
                    findings[finding.getLocation().getY()][finding.getLocation().getX()] = company.getCompany_agent();
                    updateScore(company.getCompany_agent(), 1);
                }
            }
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
    private void updateScore(AID company, int modifier){
        if(scores.containsKey(company)){
            scores.put(company, scores.get(company) + modifier);
        }
        else{
            scores.put(company, modifier);
        }
    }
}
