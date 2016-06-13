package es.upm.company03.behaviors.Rover;

import es.upm.common03.LocationUtility;
import es.upm.common03.pathFinding.LocationNode;
import es.upm.company03.Rover;
import es.upm.ontology.Location;
import jade.core.behaviours.OneShotBehaviour;

import java.util.List;

//TODO: Implement
public class HandleDelivering extends OneShotBehaviour {
    Location capsuleLocation;
    Rover agent;
    public HandleDelivering(Rover rover) {
        this.agent = rover;
        capsuleLocation = this.agent.getCapsuleLocation();
    }

    List<LocationNode> pathToCapsule;
    int endCode;
    @Override
    public void action() {
        System.out.println("NOW DELIVERING");
        endCode = 0;
        agent.setCurrentJob(Rover.RoverJobs.DELIVERING);
        if(pathToCapsule == null || pathToCapsule.size() == 0){
            pathToCapsule = agent.getPathMap().findPath(agent.getRoverLocation().getX(), agent.getRoverLocation().getY(),
                    capsuleLocation.getX(), capsuleLocation.getY());
        }
        if(LocationUtility.calculateRange(agent.getRoverLocation(), capsuleLocation, agent.getMapSizeX(), agent.getMapSizeY()) <= 3){
            agent.informFindings();
            endCode = EndCodes.TO_ROAMING;
        }else{
            iteratePath();
            endCode = EndCodes.TO_MOVING;
        }

    }
    private void iteratePath(){
        if(!LocationUtility.areColliding(agent.getRoverLocation(), pathToCapsule.get(0).getLocation())){
            int dir = LocationUtility.calculateDirection(agent.getRoverLocation(), pathToCapsule.get(0).getLocation(), agent.getMapSizeX(), agent.getMapSizeY());
            if(dir == 0) { pathToCapsule = agent.getPathMap().findPath(agent.getRoverLocation().getX(), agent.getRoverLocation().getY(),
                            capsuleLocation.getX(), capsuleLocation.getY());
                return;
            }
            agent.setNextDirection(dir);
        }
        else{
            pathToCapsule.remove(0);
        }
    }
    @Override
    public void reset() {
        super.reset();
        if(pathToCapsule!=null)
            pathToCapsule.clear();
    }

    @Override
    public int onEnd() {
        return endCode;
    }


    public class EndCodes {
        public static final int TO_ROAMING = 1;
        public static final int TO_MOVING = 2;

    }
}
