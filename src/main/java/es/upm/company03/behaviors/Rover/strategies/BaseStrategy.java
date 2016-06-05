package es.upm.company03.behaviors.Rover.strategies;

import es.upm.company03.Rover;
import es.upm.ontology.Location;

import java.util.ArrayList;

/**
 * Created by borismakogonyuk on 06.06.16.
 */
public abstract class BaseStrategy {
    private Rover agent;
    private Location currentCalculatedLocation;
    protected BaseStrategy(Rover agent){
        this.agent = agent;
    }
    private int currentStepIndex = 0;
    private ArrayList<BaseStep> steps = new ArrayList<>();
    public void addNewStep(BaseStep step){
        steps.add(step);
    }
    public boolean hasNextStep(){
        return currentStepIndex+1 < steps.size();
    }
    public void nextStep(){
        if(!hasNextStep()) currentCalculatedLocation = null;
        else{
            currentCalculatedLocation = steps.get(++currentStepIndex).calculateLocation();
        }
    }
}
