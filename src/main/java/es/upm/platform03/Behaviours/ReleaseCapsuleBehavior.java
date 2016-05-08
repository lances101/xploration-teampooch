package es.upm.platform03.behaviours;

import es.upm.company03.common.CompanyAIDTuple;
import es.upm.company03.common.RFBAgent;
import es.upm.ontology.Location;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Random;

public class ReleaseCapsuleBehavior extends WakerBehaviour {

    RFBAgent agent;
    ArrayList<CompanyAIDTuple> companies;

    public ReleaseCapsuleBehavior(RFBAgent a, long timeout, ArrayList<CompanyAIDTuple> companies) {
        super(a, timeout * 1000);
        this.agent = a;
        this.companies = companies;
    }

    @Override
    protected void onWake() {
        System.out.printf("%s: sending ReleaseCapsuleBehavior to %d companies%n",
                agent.getLocalName(), companies.size());
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setOntology(agent.getOntology().getName());
        msg.setLanguage(agent.getCodec().getName());
        msg.setProtocol(agent.getOntology().PROTOCOL_RELEASE_CAPSULE);
        for (CompanyAIDTuple tuple : companies)
            msg.addReceiver(tuple.getCompany());

        //TODO: calculate real position. for now - random
        Random rnd = new Random(DateTime.now().getMillis());

        es.upm.ontology.ReleaseCapsule releaseCapsule = new es.upm.ontology.ReleaseCapsule();
        Location location = new Location();
        location.setX(rnd.nextInt(100));
        location.setY(rnd.nextInt(100));
        releaseCapsule.setLocation(location);
        try {
            agent.getContentManager().fillContent(msg, new Action(agent.getAID(), releaseCapsule));
            agent.send(msg);
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
