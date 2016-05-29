package es.upm.common03;

import es.upm.ontology.Location;

/**
 * Created by borismakogonyuk on 29.05.16.
 */
public class LocationCalculator {
    public LocationCalculator() {
    }

    public static boolean areColliding(Location lvalue, Location rvalue) {
        boolean retValue = false;
        if(lvalue.getX() == rvalue.getX() && lvalue.getY() == rvalue.getY()) {
            retValue = true;
        }

        return retValue;
    }

    public static int calculateDistance(Location lvalue, Location rvalue) {
        int xDeltaAbs = Math.abs(lvalue.getX() - rvalue.getX());
        int yDeltaAbs = Math.abs(lvalue.getY() - rvalue.getY());
        return (int)Math.floor(Math.sqrt((double)(xDeltaAbs + yDeltaAbs)));
    }

    public static Location calculateNewLocation(Location loc, int dir) {
        switch(dir) {
            case 1:
                loc.setX(loc.getX() - 2);
                break;
            case 2:
                loc.setX(loc.getX() - 1);
                loc.setY(loc.getY() + 1);
                break;
            case 3:
                loc.setX(loc.getX() + 1);
                loc.setY(loc.getY() + 1);
                break;
            case 4:
                loc.setY(loc.getX() + 2);
                break;
            case 5:
                loc.setX(loc.getX() + 1);
                loc.setY(loc.getY() - 1);
                break;
            case 6:
                loc.setX(loc.getX() - 1);
                loc.setY(loc.getY() - 1);
                break;
            default:
                return loc;
        }

        return loc;
    }
}
