package es.upm.platform03;

import es.upm.common03.ontology.Location;
import jade.core.AID;

import java.util.HashMap;

public class XplorationMap {
    private HashMap<AID, Location> rovers = new HashMap<AID, Location>();
    private static XplorationMap _instance;
    public static void UpdatePosition(AID aid, Location location)
    {
        _instance.rovers.put(aid, location);
    }

    public static Location GetPosition(AID aid)
    {
        return _instance.rovers.get(aid);
    }


}
