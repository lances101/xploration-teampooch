package es.upm.platform03;

import es.upm.ontology.Location;
import jade.core.AID;
import org.joda.time.DateTime;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class XplorationMap {
    private static XplorationMap _instance = new XplorationMap();
    private HashMap<AID, Location> rovers = new HashMap<AID, Location>();
    private String[][] minerals;

    public static int getSizeX() {
        return _instance.sizeX;
    }

    public static int getSizeY() {
        return _instance.sizeY;
    }

    private int sizeX = 10, sizeY = 10;
    public XplorationMap() {
        minerals = generateRandomMaterials(sizeX, sizeY);
        printMinerals();
    }

    public XplorationMap(String[][] minerals) {
        sizeY = minerals.length;
        sizeX = minerals[0].length;
        printMinerals();
    }

    private static String[][] generateRandomMaterials(int sizeX, int sizeY) {
        String[][] minerals = new String[sizeX+1][sizeY+1];
        for (int y = 1; y < sizeX+1; y++)
            for (int x = 1; x < sizeY+1; x++) {
                if((x % 2 == 1 && y%2 == 0) ||
                   (x % 2 == 0 && y%2 == 1))
                    continue;
                Random r = new Random();
                switch (r.nextInt(5))
                {
                    case 0:
                        minerals[y][x] = "A";
                        break;
                    case 1:
                        minerals[y][x] = "B";
                        break;
                    case 2:
                        minerals[y][x] = "C";
                        break;
                    case 3:
                        minerals[y][x] = "D";
                        break;
                    case 4:
                        minerals[y][x] = "E";
                        break;
                    default:
                        minerals[y][x] = "A";
                        break;
                }

            }

        return minerals;
    }
    private void printMinerals() {
        System.out.printf("Printing map%n==================%n");
        for (int y = 1; y < sizeX; y++) {
            for (int x = 1; x < sizeY; x++) {
                System.out.print(minerals[y][x] == null? "-" : minerals[y][x]);
            }
            System.out.printf("%n");
        }
        System.out.printf("==================%n");
    }
    public static void UpdatePosition(AID aid, Location location) {
        _instance.rovers.put(aid, location);
    }

    public static Location getPosition(AID aid) {
        return _instance.rovers.get(aid);
    }

    public static String getMineralAtPosition(Location location) {
        return _instance.minerals[location.getY()][location.getX()];
    }

    public static AID[] getAgentsInRange(int range, AID agent) {
        return getAgentsInRange(range, _instance.rovers.get(agent));
    }

    public static AID[] getAgentsInRange(int range, Location center) {
        ArrayList<Location> validLocations = new ArrayList<>();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                Location loc = new Location();
                loc.setX(center.getX() + x);
                loc.setY(center.getY() + y);
                validLocations.add(loc);
            }
        }

        ArrayList<AID> result = new ArrayList<>();
        _instance.rovers.forEach((aid, location) -> {
            for (Location loc : validLocations) {
                if (equalLocations(location, loc))
                {
                    result.add(aid);
                }
            }
        });
        if(result.size() == 0) return null;
        return (AID[]) result.toArray();
    }

    private static boolean equalLocations(Location a, Location b) {
        if (a.getX() == b.getX() && a.getY() == b.getY())
            return true;
        return false;
    }

    public static Location[] findLocationForRovers(int count)
    {
        ArrayList<Location> result = new ArrayList<>();
        Random rand = new Random(DateTime.now().getMillis());
        Point center = new Point();
        center.x = getSizeX()/2;
        center.y = getSizeY()/2;
        double radius = (getSizeX()<getSizeY()?getSizeX():getSizeY())*0.35;
        for(int i = 0; i < count; i++) {
            Location loc = new Location();
            double angle = 2.0d * Math.PI * rand.nextDouble();
            loc.setX((int) (radius * Math.cos(angle) + center.x+1));
            loc.setY((int) (radius * Math.sin(angle) + center.y+1));
            while(!isValidPosition(loc.getX(), loc.getY()))
                loc.setY(loc.getY()+1);
            result.add(loc);
        }
        return result.toArray(new Location[result.size()]);
    }

    public static boolean isValidPosition(int pointX, int pointY){
        if(pointX < getSizeX() && pointY < getSizeY() && pointX > 0 && pointY > 0)
            if(_instance.minerals[pointY][pointX] != null)
                return true;
        return false;
    }

}
