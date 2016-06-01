package es.upm.common03;

import es.upm.ontology.Location;

/**
 * Created by borismakogonyuk on 29.05.16.
 */
public class LocationUtility {
    public LocationUtility() {
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
    private static void applyDirection(Location loc, int modX, int modY, int sizeX, int sizeY){
        Location newLoc = new Location();
        if(loc.getX() + modX > sizeX)
            newLoc.setX(1);
        else if(loc.getX() + modX < 1)
            newLoc.setX(sizeX);
        else
            newLoc.setX(loc.getX()+modX);

        if(loc.getY() + modY > sizeY)
            newLoc.setY(loc.getX() % 2 == 0? 1 : 2);
        else if(loc.getY() + modY < 1)
            newLoc.setY(loc.getX() % 2 == 0? sizeY - 1 : sizeY);
        else
            newLoc.setY(loc.getY()+modY);
        loc.setX(newLoc.getX());
        loc.setY(newLoc.getY());
    }
    public static Location calculateNewLocation(Location location, int dir, int sizeX, int sizeY) {
        Location newLocation = new Location();
        newLocation.setX(location.getX());
        newLocation.setY(location.getY());
        switch(dir) {
            case 1:
                applyDirection(newLocation, 0, -2, sizeX, sizeY);
                break;
            case 2:
                applyDirection(newLocation, 1, -1, sizeX, sizeY );
                break;
            case 3:
                applyDirection(newLocation, 1, 1, sizeX, sizeY);
                break;
            case 4:
                applyDirection(newLocation, 0, 2, sizeX, sizeY);
                break;
            case 5:
                applyDirection(newLocation, -1, 1, sizeX, sizeY);
                break;
            case 6:
                applyDirection(newLocation, -1, -1, sizeX, sizeY);
                break;
        }
        return newLocation;
    }

}
