package es.upm.company03.behaviors.Rover;

import es.upm.company03.Rover;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Created by borismakogonyuk on 31.05.16.
 */

//TODO: Implement
public class HandleRoaming extends OneShotBehaviour {
    public static class EndCodes {
        public static final int TO_DELIVERING = 1;
        public static final int TO_MOVING = 2;
        public static final int TO_ANALYZING = 3;
    }
    enum RoamingSubroutines{
        INITIAL_CIRCLE
    }
    RoamingSubroutines subroutine = RoamingSubroutines.INITIAL_CIRCLE;
    Rover agent;
    int lastEndCode = 0;
    public HandleRoaming(Rover rover) {
        this.agent = rover;
    }

    @Override
    public void action() {
        lastEndCode = 0;
        agent.setCurrentJob(Rover.RoverJobs.ROAMING);
        if(!agent.isLocationAnalyzed(agent.getRoverLocation())){
            lastEndCode = EndCodes.TO_ANALYZING;
            return;
        }


    }



    @Override
    public int onEnd() {
        return lastEndCode;
    }
}
