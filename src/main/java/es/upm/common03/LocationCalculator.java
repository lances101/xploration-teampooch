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
    private static Location applyDirection(Location loc, int modX, int modY, int sizeX, int sizeY){
        if(loc.getX() + modX > sizeX)
            loc.setX(1);
        else if(loc.getX() + modX < 1)
            loc.setX(sizeX);

        if(loc.getY() + modY > sizeY)
            loc.setY(loc.getX() % 2 == 0? 2 : 1);
        else if(loc.getY() + modY < sizeY)
            loc.setY(loc.getX() % 2 == 0? sizeY : sizeY - 1);
        return loc;
    }
    public static Location calculateNewLocation(Location loc, int dir, int sizeX, int sizeY) {
        switch(dir) {
            case 1:
                applyDirection(loc, 0, -2, sizeX, sizeY);
                break;
            case 2:
                applyDirection(loc, 1, -1, sizeX, sizeY );
                break;
            case 3:
                applyDirection(loc, 1, 1, sizeX, sizeY);
                break;
            case 4:
                applyDirection(loc, 0, 2, sizeX, sizeY);
                break;
            case 5:
                applyDirection(loc, -1, 1, sizeX, sizeY);
                break;
            case 6:
                applyDirection(loc, -1, -1, sizeX, sizeY);
                break;
        }
        return loc;
    }



}
