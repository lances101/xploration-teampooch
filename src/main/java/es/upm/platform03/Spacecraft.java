package es.upm.platform03;

import es.upm.common03.CompanyAIDTuple;
import es.upm.common03.RFBAgent;
import es.upm.ontology.Mineral;
import es.upm.ontology.MineralResult;
import es.upm.platform03.behaviours.Spacecraft.HandleRegistrationRequest;
import es.upm.platform03.behaviours.Spacecraft.HandleReleaseCapsule;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;

/**
 * Spacecraft agent. Handles the initialization of other agents of the platform.
 * Handles the following behaviors:
 * <br>
 * - Company registration
 */
public class Spacecraft extends RFBAgent {

    ArrayList<CompanyAIDTuple> companies = new ArrayList<CompanyAIDTuple>();
    int registrationPeriodSeconds = 10;


    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", getLocalName());



        //See comment in Company.java
        addBehaviour(new HandleRegistrationRequest(this, registrationPeriodSeconds, companies));
        addBehaviour(new HandleReleaseCapsule(this, registrationPeriodSeconds, companies));

        System.out.printf("%s: registration is up! Ends in %s seconds%n",
                getLocalName(), registrationPeriodSeconds);

        registerSelfWithServices(new String[]{"Spacecraft"});
        super.setup();
        createAndWakeupPlatform();


        ACLMessage mes = new ACLMessage(ACLMessage.INFORM);
        mes.setOntology(xOntology.getName());
        mes.setLanguage(codec.getName());
        MineralResult mr = new MineralResult();
        Mineral min = new Mineral();
        min.setType("A");
        mr.setMineral(min);
        try {
            getContentManager().fillContent(mes, new Action(getAID(), mr));

        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
        send(mes);
    }

    void createAndWakeupPlatform()
    {
        ContainerController container = getContainerController();
        try {
            //AgentController agWorld = container.createNewAgent("World", "es.upm.platform03.World", null);
            //agWorld.start();
            AgentController agMap = container.createNewAgent("Map", "es.upm.platform03.Map", null);
            agMap.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }


}
