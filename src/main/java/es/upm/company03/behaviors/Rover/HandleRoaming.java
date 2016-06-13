package es.upm.company03.behaviors.Rover;

import es.upm.common03.LocationUtility;
import es.upm.common03.pathFinding.LocationNode;
import es.upm.company03.Rover;
import es.upm.company03.behaviors.Rover.strategies.BaseStrategy;
import es.upm.company03.behaviors.Rover.strategies.CircleStrategy;
import es.upm.ontology.Location;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by borismakogonyuk on 31.05.16.
 */

//TODO: Implement
public class HandleRoaming extends OneShotBehaviour {
    private final HashMap<AID, Location> otherRovers;

    public static class EndCodes {
        public static final int TO_ROAMING = 0;
        public static final int TO_DELIVERING = 4;
        public static final int TO_MOVING = 2;
        public static final int TO_ANALYZING = 3;
    }

    Rover agent;
    int lastEndCode = 0;
    BaseStrategy currentStrategy;
    List<LocationNode> currentPath;
    ArrayList<BaseStrategy> strategies;
    public HandleRoaming(Rover rover, HashMap<AID, Location> otherRovers) {
        this.agent = rover;
        this.otherRovers = otherRovers;
        strategies = new ArrayList<>();
        strategies.add(new CircleStrategy(agent, rover.getCapsuleLocation(), 1));
        strategies.add(new CircleStrategy(agent, rover.getCapsuleLocation(), 2));
        strategies.add(new CircleStrategy(agent, rover.getCapsuleLocation(), 3));
        strategies.add(new CircleStrategy(agent, rover.getCapsuleLocation(), 4));
        strategies.add(new CircleStrategy(agent, rover.getCapsuleLocation(), 5));
        currentStrategy = strategies.get(0);
    }

    @Override
    public void action() {
        lastEndCode = 0;
        agent.setCurrentJob(Rover.RoverJobs.ROAMING);
        if(!agent.isLocationAnalyzed(agent.getRoverLocation())){
            lastEndCode = EndCodes.TO_ANALYZING;
            return;
        }
        //For fast informs
        if(isInDeliveryRange() && agent.getFindingsCount() > 0){
            //agent.informFindings();
        }
        if(agent.getFindingsCount() > 3){
            System.out.println("DELIVERING");
            lastEndCode = EndCodes.TO_DELIVERING;
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
        if (currentPath == null || currentPath.isEmpty() || agent.isPathInvalidated()) {
            calculatePathToStrategyLocation();
            return;
        }
        iteratePath();
        lastEndCode = EndCodes.TO_MOVING;


    }
    private void updateRoversOnNavMap(){
        agent.getPathMap().resetAllWalkable();
        for(Map.Entry<AID, Location> pair : otherRovers.entrySet()){
            agent.getPathMap().setWalkable(pair.getValue().getX(), pair.getValue().getY(), false);
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
        if(agent.isPathInvalidated()) {
            updateRoversOnNavMap();
            agent.setPathInvalidated(false);
        }
        currentPath = agent.getPathMap().findPath(agent.getRoverLocation().getX(), agent.getRoverLocation().getY(),
                currentStrategy.getCurrentCalculatedLocation().getX(), currentStrategy.getCurrentCalculatedLocation().getY());
    }
    private boolean isInDeliveryRange(){
        return LocationUtility.calculateDistance(agent.getRoverLocation(), agent.getCapsuleLocation())<3;
    }


    @Override
    public int onEnd() {
        return lastEndCode;
    }
}
