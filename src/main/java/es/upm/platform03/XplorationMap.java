package es.upm.platform03;

import es.upm.ontology.Location;
import jade.core.AID;

import java.util.ArrayList;
import java.util.HashMap;

public class XplorationMap {
    private static XplorationMap _instance = new XplorationMap();
    private HashMap<AID, Location> rovers = new HashMap<AID, Location>();
    private String[][] minerals;

    public XplorationMap() {

        minerals = new String[10][10];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                minerals[i][j] = "A";
            }
    }

    public static void UpdatePosition(AID aid, Location location) {
        _instance.rovers.put(aid, location);
        System.out.printf("New location for %s - %d | %d%n",
                aid.getLocalName(), location.getX(), location.getY());
    }

    public static Location GetPosition(AID aid) {
        return _instance.rovers.get(aid);
    }

    public static String GetMineralAtPosition(Location location) {
        return _instance.minerals[location.getX()][location.getY()];
    }

    public static AID[] GetAgentsInRange(int range, AID agent) {
        return GetAgentsInRange(range, _instance.rovers.get(agent));
    }

    public static AID[] GetAgentsInRange(int range, Location center) {
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
                if (EqualLocations(location, loc))
                {
                    result.add(aid);
                }
            }
        });
        return (AID[]) result.toArray();
    }

    private static boolean EqualLocations(Location a, Location b) {
        if (a.getX() == b.getX() && a.getY() == b.getY())
            return true;
        return false;
    }


}
