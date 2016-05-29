package es.upm.company03.behaviors.Rover;

import es.upm.common03.TeamAgent;
import es.upm.ontology.MineralResult;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Created by borismakogonyuk on 25.05.16.
 */
public class HandleResearch extends SimpleBehaviour {
    enum State {
        Send,
        Receive,
        End
    }

    private TeamAgent agent;
    private AID world;
    private State state = State.Send;
    private MessageTemplate mtResearch;

    public void startResearch()
    {
        state = State.Send;
        this.reset();
    }
    public HandleResearch(TeamAgent agent, AID world) {
        this.agent = agent;
        this.world = world;
        this.state = State.End;
        mtResearch = MessageTemplate.and(agent.getMtOntoAndCodec(),
                                MessageTemplate.MatchProtocol(agent.getxOntology().PROTOCOL_ANALYZE_MINERAL)
        );

    }

    @Override
    public void action() {
        switch (state) {
            case Send:
                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                message.setProtocol(agent.getxOntology().PROTOCOL_ANALYZE_MINERAL);
                message.setOntology(agent.getxOntology().getName());
                message.setLanguage(agent.getCodec().getName());
                message.addReceiver(world);
                agent.send(message);
                System.out.printf("%s: requesting analysis%n",
                        agent.getLocalName());
                state = State.Receive;
                break;
            case Receive:
                ACLMessage msg = agent.receive(mtResearch);
                if(msg == null)
                {
                    block();
                    return;
                }
                switch(msg.getPerformative())
                {
                    case ACLMessage.REFUSE:
                        //TODO: handle this somehow
                        state = State.End;
                        break;
                    case ACLMessage.AGREE:
                        //just wait then
                        break;
                    case ACLMessage.INFORM:
                        try {
                            Action action = (Action) agent.getContentManager().extractContent(msg);
                            MineralResult result = (MineralResult) action.getAction();
                            System.out.println("Got mineral " + result.getMineral().getType());
                        } catch (Codec.CodecException e) {
                            e.printStackTrace();
                        } catch (OntologyException e) {
                            e.printStackTrace();
                        }
                        state = State.End;
                        break;
                }

                break;
        }
    }

    @Override
    public boolean done() {
        return state == State.End;
    }
}
