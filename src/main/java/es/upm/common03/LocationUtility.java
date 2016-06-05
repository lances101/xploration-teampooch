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

        /*
        var point = startingPoint.inDir(N, Direction.North)
        var dir = Direction.SouthEast.
        for d = 0..Direction.count():
            for i = 0..N-1:
                result.add(point)
                point = point.inDir(1, dir);
            dir = nextDirection(dir);
         */

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
}
