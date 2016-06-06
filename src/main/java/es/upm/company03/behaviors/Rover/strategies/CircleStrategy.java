package es.upm.company03.behaviors.Rover.strategies;

import es.upm.common03.LocationUtility;
import es.upm.company03.Rover;
import es.upm.ontology.Location;
import es.upm.platform03.XplorationMap;

import java.util.ArrayList;

/**
 * Created by borismakogonyuk on 06.06.16.
 */
public class CircleStrategy extends BaseStrategy {

    private final Location center;
    private final int range;
    public CircleStrategy(Rover agent, Location center, int range) {
        super(agent);
        this.center = center;
        this.range = range;
        ArrayList<Location> cells = LocationUtility.getCellsAtRange(center, range, XplorationMap.getSizeX(), XplorationMap.getSizeY());
        for (Location cell : cells) {
            addNewStep(new BaseStep() {
                @Override
                public Location calculateLocation() {
                    return cell;
                }
            });
        }
    }

}
