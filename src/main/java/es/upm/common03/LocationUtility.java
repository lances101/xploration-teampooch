package es.upm.common03;

import es.upm.ontology.Location;

import java.util.ArrayList;

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
            newLoc.setY(newLoc.getX() % 2 == 0? 2 : 1);
        else if(loc.getY() + modY < 1)
            newLoc.setY(newLoc.getX() % 2 == 0? sizeY: sizeY - 1);
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
    public static Location calculateNewLocationMultiple(Location location, int dir, int sizeX, int sizeY, int times){
        Location result = location;
        for(int i = 0; i < times; i++){
            result = calculateNewLocation(result, dir, sizeX, sizeY);
        }
        return result;
    }
    public static int nextDirection(int i){
        if(i == 6) return 1;
        return ++i;
    }
    public static ArrayList<Location> getCellsAtRange(Location center, int range, int sizeX, int sizeY){
        ArrayList<Location> results = new ArrayList<>();
        int currentDirection = 3;

        if(range == 0){
            results.add(center);
            return results;
        }
        Location point = calculateNewLocationMultiple(center, 1, sizeX, sizeY, range);
        for (int dir = 0; dir < 6; dir++) {
            for (int i = 0; i < range; i++) {
                results.add(point);
                point = calculateNewLocationMultiple(point, currentDirection, sizeX, sizeY, 1);
            }
            currentDirection = nextDirection(currentDirection);
        }
        return results;
    }
    public static ArrayList<Location> getCellsInRange(Location center, int range, int sizeX, int sizeY){
        ArrayList<Location> results = new ArrayList<>();
        for(int currentRange = 0; currentRange <= range; currentRange++) {
            results.addAll(getCellsAtRange(center, currentRange, sizeX, sizeY));
        }
        return results;
    }
    public static int calculateRange(Location start, Location end, int sizeX, int sizeY){
        int limit = (sizeX > sizeY? sizeX : sizeY) / 2;
        for(int currentRange = 0; currentRange <= limit; currentRange++) {
            int currentDirection = 3;
            if(currentRange == 0){
                if(areColliding(start, end)) return currentRange;
                continue;
            }
            Location point = calculateNewLocationMultiple(start, 1, sizeX, sizeY, currentRange);
            for (int dir = 0; dir < 6; dir++) {
                for (int i = 0; i < currentRange; i++) {
                    point = calculateNewLocationMultiple(point, currentDirection, sizeX, sizeY, 1);
                    if(areColliding(end,point)) return currentRange;
                }
                currentDirection = nextDirection(currentDirection);
            }
        }
        return 0;
    }
    public static int calculateDirection(Location start, Location end, int sizeX, int sizeY){
        for(int i = 1; i <= 6; i++){
            if(areColliding(end, calculateNewLocation(start, i, sizeX, sizeY))) return i;
        }
        return 0;
    }


}
