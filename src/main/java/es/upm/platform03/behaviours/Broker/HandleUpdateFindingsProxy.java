package es.upm.platform03.behaviours.Broker;

import es.upm.common03.ontology.CompanyInfoQuery;
import es.upm.common03.ontology.CompanyInfoResult;
import es.upm.ontology.FindingsMessage;
import es.upm.platform03.World;
import es.upm.platform03.XplorationMap;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class HandleUpdateFindingsProxy extends CyclicBehaviour{
    World agent;
    AID spacecraftAID;
    MessageTemplate mtUpdateFindings;
    MessageTemplate mtCompanyQueryResult;
    MessageTemplate mtAll;
    HashMap<String, ACLMessage> messageQueue = new HashMap<>();
    public HandleUpdateFindingsProxy(World agent, AID spacecraft) {
        this.agent = agent;
        spacecraftAID = spacecraft;
        mtUpdateFindings = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.and(agent.getMtOntoAndCodec(),
                        MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_UPDATE_FINDINGS)));
        mtCompanyQueryResult = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.and(agent.getMtOntoAndCodec(),
                        MessageTemplate.MatchProtocol(agent.getTeamOntology().PROTOCOL_COMPANY_QUERY)));
        mtAll = MessageTemplate.or(mtUpdateFindings, mtCompanyQueryResult);
    }

    @Override
    public void action() {
        ACLMessage msg = agent.receive(mtAll);
        if (msg == null) {
            block();
            return;
        }
        if(msg.getProtocol() == agent.getxOntology().PROTOCOL_UPDATE_FINDINGS){
            try {
                Action act = (Action) agent.getContentManager().extractContent(msg);
                FindingsMessage findingsMessage = (FindingsMessage) act.getAction();
                int channel = findingsMessage.getFrequency().getChannel();
                CompanyInfoQuery infoQuery = new CompanyInfoQuery();
                infoQuery.setCompany_number(channel);
                ACLMessage queryMsg = new ACLMessage(ACLMessage.QUERY_REF);
                queryMsg.setOntology(agent.getTeamOntology().getName());
                queryMsg.setLanguage(agent.getCodec().getName());
                queryMsg.setProtocol(agent.getTeamOntology().PROTOCOL_COMPANY_QUERY);
                queryMsg.setConversationId(UUID.randomUUID().toString());
                queryMsg.addReceiver(spacecraftAID);
                agent.getContentManager().fillContent(queryMsg, new Action(agent.getAID(), infoQuery));
                agent.send(queryMsg);
                messageQueue.put(queryMsg.getConversationId(), msg);
            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }

        }
        else if(msg.getProtocol() == agent.getTeamOntology().PROTOCOL_COMPANY_QUERY){
            try {
                ACLMessage initialMessage = messageQueue.get(msg.getConversationId());
                if(initialMessage == null){
                    return;
                }
                messageQueue.remove(msg.getConversationId());
                Action act = (Action) agent.getContentManager().extractContent(msg);
                CompanyInfoResult result = (CompanyInfoResult) act.getAction();
                String capsuleName = result.getResult().getCapsule().getCapsule_agent().getLocalName();

                AID[] receivers = XplorationMap.getAgentsInRange(3, initialMessage.getSender());
                if(receivers.length > 0){
                    for (AID rec : receivers)
                        if(rec.getLocalName().equalsIgnoreCase(capsuleName))
                            initialMessage.addReceiver(rec);
                    agent.send(initialMessage);
                }

            } catch (Codec.CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }

        }
        else{
            agent.getLogger().log(Level.SEVERE, "Critical proxy handling error");
        }

    }
}
