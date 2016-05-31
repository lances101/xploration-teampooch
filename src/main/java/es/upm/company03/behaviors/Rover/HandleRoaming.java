package es.upm.company03.behaviors.Rover;

import es.upm.common03.TeamConstants;
import es.upm.company03.Rover;
import jade.core.behaviours.SimpleBehaviour;

/**
 * Created by borismakogonyuk on 31.05.16.
 */

//TODO: Implement
public class HandleRoaming extends SimpleBehaviour {
    public static class EndCodes {
        public static final int TO_DELIVERING = 1;
        public static final int TO_MOVING = 1;
        public static final int TO_RESEARCHING = 1;
    }

    Rover agent;
    public HandleRoaming(Rover rover) {
        this.agent = rover;
    }

    @Override
    public void action() {
        agent.setNextDirection(TeamConstants.Direction.UP_RIGHT);
    }

    @Override
    public int onEnd() {
        //TODO: figure out where actually we transition to.
        return EndCodes.TO_MOVING;
    }

    @Override
    public boolean done() {
        return false;
    }
}
