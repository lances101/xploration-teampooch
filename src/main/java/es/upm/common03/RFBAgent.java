package es.upm.common03;

import es.upm.common03.ontology.RFBOntology;
import es.upm.ontology.XplorationOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 *  RFB agent does not have much for itself, it inherits Agent.
 *  It implements a behavior that handles not_understood messages
 *  It has a couple of helper methods, but that's about it.
 */
public abstract class RFBAgent extends Agent {

    public static String getTeamPrefix(){return "03";};
    protected Logger logger = Logger.getJADELogger(this.getClass().getName());
    protected Codec codec = new SLCodec();
    protected XplorationOntology xOntology = (XplorationOntology) XplorationOntology.getInstance();
    protected RFBOntology rfbOntology = (RFBOntology) RFBOntology.getInstance();

    protected MessageTemplate mtOntoAndCodec = MessageTemplate.and(MessageTemplate.or(MessageTemplate.MatchOntology(xOntology.getName()), MessageTemplate.MatchOntology(rfbOntology.getName())), MessageTemplate.MatchLanguage(codec.getName()));

    public Logger getLogger() {
        return logger;
    }

    public Codec getCodec() {
        return codec;
    }

    public XplorationOntology getxOntology() {
        return xOntology;
    }

    public RFBOntology getRFBOntology() { return rfbOntology; }

    public MessageTemplate getMtOntoAndCodec() {
        return mtOntoAndCodec;
    }

    /**
     * Simple behaviors that catches NOT_UNDERSTOOD's and outputs them to console.
     */
    protected CyclicBehaviour NotUnderStoodBehavior = new CyclicBehaviour() {
        @Override
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
            if (msg != null) {
                System.out.println(getName() + ": got [NOT_UNDERSTOOD] from " + msg.getSender());
            }
            doWait(500);
        }
    };

    /**
     * Just adds the NOT_UNDERSTOOD behavior.
     */
    @Override
    protected void setup() {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(xOntology);
        getContentManager().registerOntology(rfbOntology);
        addBehaviour(NotUnderStoodBehavior);
    }

    /**
     * Registers the agent in the DFService with the
     * services that are provided as a string.
     * @param types services to register it with
     */
    protected void registerSelfWithServices(String[] types)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(this.getName());
        for (String type : types) {
            sd.setType(type);
        }
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        System.out.println(getLocalName() + ": registered in the DFServices.");
    }

    /**
     * Allows to handle a message by replying to it with a
     * NOT_UNDERSTOOD performative. Use this for debugging in
     * other behaviors.
     * @param msg the ACLMessage received
     */
    public void replyWithNotUnderstood(ACLMessage msg)
    {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
        reply.setContent("'"+msg.getProtocol() + "' protocol not understood");
        send(reply);
        System.out.printf("%s could not understand message from [%s] with protocol '%s'%n",
                getName(),
                msg.getSender(),
                msg.getProtocol()
        );
    }

}
