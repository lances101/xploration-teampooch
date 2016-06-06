package es.upm.company03.behaviors.Rover;

import es.upm.common03.LocationUtility;
import es.upm.common03.pathFinding.LocationNode;
import es.upm.company03.Rover;
import es.upm.company03.behaviors.Rover.strategies.BaseStrategy;
import es.upm.company03.behaviors.Rover.strategies.CircleStrategy;
import jade.core.behaviours.OneShotBehaviour;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by borismakogonyuk on 31.05.16.
 */

//TODO: Implement
public class HandleRoaming extends OneShotBehaviour {
    public static class EndCodes {
        public static final int TO_ROAMING = 0;
        public static final int TO_DELIVERING = 1;
        public static final int TO_MOVING = 2;
        public static final int TO_ANALYZING = 3;
    }

    Rover agent;
    int lastEndCode = 0;
    BaseStrategy currentStrategy;
    List<LocationNode> currentPath;
    ArrayList<BaseStrategy> strategies;
    public HandleRoaming(Rover rover) {
        this.agent = rover;
        strategies = new ArrayList<>();
        strategies.add(new CircleStrategy(agent, rover.getCapsuleLocation(), 1));
        strategies.add(new CircleStrategy(agent, rover.getCapsuleLocation(), 2));
        currentStrategy = strategies.get(0);
    }

    @Override
    public void action() {
        lastEndCode = 0;
        agent.setCurrentJob(Rover.RoverJobs.ROAMING);
        if(currentPath == null){
            calculatePathToStrategyLocation();
        }
        if(!agent.isLocationAnalyzed(agent.getRoverLocation())){
            lastEndCode = EndCodes.TO_ANALYZING;
            return;
        }

        if(LocationUtility.areColliding(agent.getRoverLocation(), currentStrategy.getCurrentCalculatedLocation())){
            if(currentStrategy.hasNextStep()){
                currentStrategy.nextStep();
                calculatePathToStrategyLocation();
            }
            else{
                setNextStrategy();
                calculatePathToStrategyLocation();
            }
        }
        if(!currentPath.isEmpty()) {
            iteratePath();
            lastEndCode = EndCodes.TO_MOVING;
        }

    }

    private void setNextStrategy(){
        int index = strategies.indexOf(currentStrategy);
        if(++index < strategies.size()){
            currentStrategy = strategies.get(index);
        }
    }
    private void iteratePath(){
        if(!LocationUtility.areColliding(agent.getRoverLocation(), currentPath.get(0).getLocation())){
            int dir = LocationUtility.calculateDirection(agent.getRoverLocation(), currentPath.get(0).getLocation(), agent.getMapSizeX(), agent.getMapSizeY());
            if(dir == 0) { calculatePathToStrategyLocation(); return; }
            agent.setNextDirection(dir);
        }
        else{
            currentPath.remove(0);
        }
    }
    private void calculatePathToStrategyLocation(){
        currentPath = agent.getPathMap().findPath(agent.getRoverLocation().getX(), agent.getRoverLocation().getY(),
                currentStrategy.getCurrentCalculatedLocation().getX(), currentStrategy.getCurrentCalculatedLocation().getY());
    }


    @Override
    public int onEnd() {
        return lastEndCode;
    }
}
