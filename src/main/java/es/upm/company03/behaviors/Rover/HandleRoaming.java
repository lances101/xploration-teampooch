package es.upm.company03.behaviors.Rover;

import es.upm.company03.Rover;
import jade.core.behaviours.SimpleBehaviour;

import java.util.Random;

/**
 * Created by borismakogonyuk on 31.05.16.
 */

//TODO: Implement
public class HandleRoaming extends SimpleBehaviour {
    public static class EndCodes {
        public static final int TO_DELIVERING = 1;
        public static final int TO_MOVING = 2;
        public static final int TO_ANALYZING = 3;
    }
    Random random = new Random();
    Rover agent;
    public HandleRoaming(Rover rover) {
        this.agent = rover;
        agent.setCurrentJob(Rover.RoverJobs.ROAMING);
    }

    boolean findingFlag = false;
    @Override
    public void action() {
        if(agent.getFindingsCount() == 1 && !findingFlag){
            agent.informFindings();
            findingFlag = true;
        }

    }

    @Override
    public int onEnd() {
        //TODO: figure out where actually we transition to.
        return EndCodes.TO_ANALYZING;
    }

    @Override
    public boolean done() {
        return true;
    }
}
