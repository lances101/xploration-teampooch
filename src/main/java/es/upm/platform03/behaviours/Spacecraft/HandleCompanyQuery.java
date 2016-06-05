package es.upm.platform03.behaviours.Spacecraft;

import es.upm.common03.TeamAgent;
import es.upm.common03.ontology.CompanyInfoQuery;
import es.upm.common03.ontology.CompanyInfoResult;
import es.upm.ontology.Company;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;


public class HandleCompanyQuery extends CyclicBehaviour {
    TeamAgent agent;
    ArrayList<Company> companies;
    MessageTemplate mtCompanyQuery;
    public HandleCompanyQuery(TeamAgent agent, ArrayList<Company> companies) {
        this.agent = agent;
        this.companies = companies;
        mtCompanyQuery = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
                MessageTemplate.and(agent.getMtOntoAndCodec(),
                        MessageTemplate.MatchProtocol(agent.getTeamOntology().PROTOCOL_COMPANY_QUERY)));
    }

    @Override
    public void action() {
        ACLMessage msg = agent.receive(mtCompanyQuery);
        if(msg == null){
            block();
            return;
        }
        try {
            Action act = (Action) agent.getContentManager().extractContent(msg);
            CompanyInfoQuery query = (CompanyInfoQuery) act.getAction();
            String toFind = String.format("Company0%d", query.getCompany_number());
            Company result = null;
            for(Company comp : companies){
                if(comp.getCompany_agent().getLocalName().equalsIgnoreCase(toFind)) {
                    result = comp;
                    break;
                }
            }
            if(result == null){
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.FAILURE);
                agent.send(reply);
            }
            else{
                CompanyInfoResult resultAct = new CompanyInfoResult();
                resultAct.setResult(result);
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                agent.getContentManager().fillContent(reply, new Action(agent.getAID(), resultAct));
                agent.send(reply);
            }
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
