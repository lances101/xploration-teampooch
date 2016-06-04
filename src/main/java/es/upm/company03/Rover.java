package es.upm.company03;

import es.upm.common03.TeamAgent;
import es.upm.common03.ontology.InformAID;
import es.upm.company03.behaviors.Rover.*;
import es.upm.ontology.Finding;
import es.upm.ontology.Location;
import es.upm.ontology.Mineral;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by borismakogonyuk on 30.04.16.
 */
public class Rover extends TeamAgent {
    public enum RoverJobs{
        STARTING,
        ROAMING,
        DELIVERING
    }
    private static class FSMStates {
        public static final String START = "START";
        public static final String ROAMING = "ROAMING";
        public static final String DELIVERING = "DELIVERING";
        public static final String MOVING = "MOVING";
        public static final String ANALYZING = "ANALYZING";
        public static final String SYNCING = "SYNCING";
    }
    FSMBehaviour fsm;
    private Location myLocation, capsuleLocation;
    private int nextDirection;
    private AID worldAID, brokerAID;
    private HandleMovement bhvMovement;
    private HandleAnalysis bhvAnalysis;
    private int mapSizeX;
    private int mapSizeY;
    private ArrayList<Finding> findings = new ArrayList<>();
    private RoverJobs currentJob = RoverJobs.STARTING;
    public void addFinding(Location roverLocation, Mineral mineral) {
        Finding finding = new Finding();
        finding.setLocation(roverLocation);
        finding.setMineral(mineral);
        findings.add(finding);
    }
    public RoverJobs getCurrentJob() {
        return currentJob;
    }
    public void setCurrentJob(RoverJobs currentJob) {
        this.currentJob = currentJob;
    }
    public int getNextDirection() {return nextDirection;}
    public void setNextDirection(int value) { this.nextDirection = value; }
    public Location getRoverLocation() {
        return myLocation;
    }
    public void setRoverLocation(Location myLocation) {
        this.myLocation = myLocation;
    }
    public int getMapSizeX() {
        return mapSizeX;
    }
    public int getMapSizeY() {
        return mapSizeY;
    }

    @Override
    protected void setup() {
        AID companyAID = null;
        Object[] arguments = getArguments();
        if (arguments.length >= 4 && arguments[0] != null) {
            companyAID = arguments[0] instanceof AID ? (AID) arguments[0] : null;
            myLocation = arguments[1] instanceof Location ? (Location) arguments[1] : null;
            capsuleLocation = arguments[1] instanceof Location ? (Location) arguments[1] : null;
            mapSizeX = arguments[2] instanceof Integer ? (Integer) arguments[2] : null;
            mapSizeY = arguments[3] instanceof Integer ? (Integer) arguments[3] : null;
        }
        if (myLocation == null) {
            logger.log(Level.SEVERE, "Tried to instantiate Rover without location.");
            return;
        }
        System.out.printf("%s: dropped at %d,%d%n", getLocalName(), myLocation.getX(), myLocation.getY());
        super.setup();
        worldAID = findService("World");
        brokerAID = findService("Broker");

        if (worldAID == null || brokerAID == null) {
            System.out.println("CRITICAL. World or Broker missing");
            this.doDelete();
            return;
        }
        super.setup();
        informCompany(companyAID);
        setupFSM();
    }

    private void setupFSM() {
        fsm = new FSMBehaviour();

        fsm.registerFirstState(new HandleInitialCapsuleHandshake(this, brokerAID), FSMStates.START);
        fsm.registerState(new HandleRoaming(this), FSMStates.ROAMING);
        fsm.registerState(new HandleDelivering(this), FSMStates.DELIVERING);
        fsm.registerState(new HandleMovement(this, worldAID), FSMStates.MOVING);
        fsm.registerState(new HandleAnalysis(this, worldAID), FSMStates.ANALYZING);
        fsm.registerDefaultTransition(FSMStates.START, FSMStates.ROAMING);
        fsm.registerTransition(FSMStates.ROAMING, FSMStates.DELIVERING, HandleRoaming.EndCodes.TO_DELIVERING, new String[]{FSMStates.DELIVERING});
        fsm.registerTransition(FSMStates.ROAMING, FSMStates.MOVING, HandleRoaming.EndCodes.TO_MOVING, new String[]{FSMStates.MOVING});
        fsm.registerTransition(FSMStates.ROAMING, FSMStates.ANALYZING, HandleRoaming.EndCodes.TO_ANALYZING, new String[]{FSMStates.ANALYZING});
        fsm.registerTransition(FSMStates.MOVING, FSMStates.ROAMING, HandleMovement.EndCodes.TO_ROAMING);
        fsm.registerTransition(FSMStates.MOVING, FSMStates.DELIVERING, HandleMovement.EndCodes.TO_DELIVERING);
        fsm.registerTransition(FSMStates.ANALYZING, FSMStates.ROAMING, 0);
        addBehaviour(fsm);
    }

    private void informCompany(AID company) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setLanguage(codec.getName());
        msg.setOntology(teamOntology.getName());
        msg.setProtocol(teamOntology.PROTOCOL_INFORM_AID);
        msg.addReceiver(company);
        InformAID informAID = new InformAID();
        es.upm.common03.ontology.Rover rover = new es.upm.common03.ontology.Rover();
        rover.setName("Name");
        rover.setRover_agent(getAID());
        informAID.setSubject(rover);
        try {
            getContentManager().fillContent(msg, new Action(getAID(), informAID));
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
        send(msg);
    }
}

