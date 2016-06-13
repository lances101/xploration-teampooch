package es.upm.platform03;

import es.upm.common03.TeamAgent;
import es.upm.common03.TeamConstants;
import es.upm.common03.behaviors.EmptyTimeoutBehavior;
import es.upm.ontology.Company;
import es.upm.platform03.behaviours.Spacecraft.*;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.ParallelBehaviour;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Spacecraft agent. Handles the initialization of other agents of the platform.
 * Handles the following behaviours:
 * <br>
 * - Company registration
 */
public class Spacecraft extends TeamAgent {

    ArrayList<Company> companies = new ArrayList<>();
    HashMap<AID, Integer> scores = new HashMap<>();
    public static final class States{
        public static final String START = "START";
        public static final String REGISTRATION_END = "REGISTRATION_END";
        public static final String END_GAME = "END_GAME";
    }
    FSMBehaviour mainFSM;
    @Override
    protected void setup() {
        super.setup();
        System.out.printf("%s is starting up!%n", getLocalName());
        setupFSM();
        System.out.printf("%s: registration is up! Ends in %s seconds%n",
                getLocalName(), TeamConstants.RegistrationPeriodSeconds);
        registerSelfWithServices(new String[]{"Spacecraft"});
    }

    private void setupFSM()
    {
        mainFSM = new FSMBehaviour();
        ParallelBehaviour bhvStart = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ANY);
        bhvStart.addSubBehaviour(new HandleRegistrationRequest(this, companies, TeamConstants.RegistrationPeriodSeconds));
        bhvStart.addSubBehaviour(new EmptyTimeoutBehavior(this, TeamConstants.RegistrationPeriodSeconds*1000));
        mainFSM.registerFirstState(bhvStart, States.START);
        ParallelBehaviour bhvSimulation = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ANY);
        bhvSimulation.addSubBehaviour(new HandleReleaseCapsule(this, companies));
        bhvSimulation.addSubBehaviour(new HandleCompanyQuery(this, companies));
        bhvSimulation.addSubBehaviour(new HandleUpdateFindings(this, companies, scores));
        bhvSimulation.addSubBehaviour(new EmptyTimeoutBehavior(this, TeamConstants.SimulationPeriodSeconds*1000));
        mainFSM.registerState(bhvSimulation, States.REGISTRATION_END);
        mainFSM.registerState(new HandleEndGame(this, companies, scores), States.END_GAME);
        mainFSM.registerDefaultTransition(States.START, States.REGISTRATION_END);
        mainFSM.registerDefaultTransition(States.REGISTRATION_END, States.END_GAME);

        addBehaviour(mainFSM);
    }







}
