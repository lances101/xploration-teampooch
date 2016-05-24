package es.upm.platform03;

import es.upm.ontology.Location;
import jade.core.AID;

import java.util.HashMap;

public class XplorationMap {
    private static XplorationMap _instance;
    private HashMap<AID, Location> rovers = new HashMap<AID, Location>();
    private String[][] minerals;

    public XplorationMap() {
        minerals = new String[5][5];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                minerals[i][j] = "A";
            }
    }

    public static void UpdatePosition(AID aid, Location location)
    {
        _instance.rovers.put(aid, location);
    }

    public static Location GetPosition(AID aid)
    {
        return _instance.rovers.get(aid);
    }

    public static String GetMineralAtPosition(Location location) {
        return _instance.minerals[location.getX()][location.getY()];
    }


}
