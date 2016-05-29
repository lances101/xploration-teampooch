package es.upm.platform03;

import es.upm.common03.TeamAgent;
import es.upm.common03.behaviors.EmptyTimeoutBehavior;
import es.upm.ontology.Company;
import es.upm.platform03.behaviours.Spacecraft.HandleRegistrationRequest;
import es.upm.platform03.behaviours.Spacecraft.HandleReleaseCapsule;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.ParallelBehaviour;

import java.util.ArrayList;

/**
 * Spacecraft agent. Handles the initialization of other agents of the platform.
 * Handles the following behaviors:
 * <br>
 * - Company registration
 */
public class Spacecraft extends TeamAgent {

    ArrayList<Company> companies = new ArrayList<>();
    int registrationPeriodSeconds = 10;
    public static final class States{
        public static final String START = "START";
        public static final String REGISTRATION_END = "REGISTRATION_END";
    }
    FSMBehaviour mainFSM;

    @Override
    protected void setup() {
        super.setup();

        System.out.printf("%s is starting up!%n", getLocalName());
        setupFSM();


        System.out.printf("%s: registration is up! Ends in %s seconds%n",
                getLocalName(), registrationPeriodSeconds);

        registerSelfWithServices(new String[]{"Spacecraft"});


    }

    private void setupFSM()
    {
        mainFSM = new FSMBehaviour();
        ParallelBehaviour bhvStart = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ANY);
        bhvStart.addSubBehaviour(new HandleRegistrationRequest(this, companies, registrationPeriodSeconds));
        bhvStart.addSubBehaviour(new EmptyTimeoutBehavior(this, registrationPeriodSeconds*1000));
        mainFSM.registerFirstState(bhvStart, States.START);
        mainFSM.registerState(new HandleReleaseCapsule(this, companies), States.REGISTRATION_END);
        mainFSM.registerDefaultTransition(States.START, States.REGISTRATION_END);

        addBehaviour(mainFSM);
    }







}
