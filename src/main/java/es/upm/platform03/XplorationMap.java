package es.upm.platform03;

import es.upm.common03.LocationUtility;
import es.upm.ontology.Location;
import es.upm.platform03.visual.SimulationView;
import jade.core.AID;
import org.joda.time.DateTime;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XplorationMap {
    private static XplorationMap _instance = new XplorationMap();
    private HashMap<AID, Location> rovers = new HashMap<AID, Location>();
    private HashMap<AID, Location> capsules = new HashMap<>();
    private ArrayList<Location> debugs = new ArrayList<>();
    private String[][] minerals;
    private int sizeX = 10, sizeY = 10;
    SimulationView simView;

    public static int getSizeX() {
        return _instance.sizeX;
    }
    public static int getSizeY() {
        return _instance.sizeY;
    }
    public XplorationMap() {
    }
    public static void initMapFromFile(String path) throws IOException {
        if (!Files.exists(Paths.get(path))) {
            throw new FileNotFoundException("Map file not found. Will not continue");
        }
        readMapFromFile(Paths.get(path));
        _instance.initializeForm();
    }
    private void initializeForm() {
        simView = new SimulationView("Simulation View", minerals, rovers, capsules, debugs);
        simView.show();
    }
    private static void readMapFromFile(Path path) throws IOException {
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(path);
        Pattern patMapSize = Pattern.compile("[(](\\d{1,2})\\s*[,]\\s*(\\d{1,2})");
        Pattern patMineral = Pattern.compile("\\s");
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if(i == 0) {
                Matcher matcher = patMapSize.matcher(line);
                matcher.find();
                _instance.sizeX = Integer.parseInt(matcher.group(1));
                _instance.sizeY = Integer.parseInt(matcher.group(2));
                _instance.minerals = new String[_instance.sizeY+1][_instance.sizeX+1];
                continue;
            }

            String[] results = patMineral.split(line);
            int stringIndex = 0;
            for (int x = 1; x < _instance.sizeX+1; x++) {
                if ((x % 2 == 1 && i % 2 == 0) ||
                        (x % 2 == 0 && i % 2 == 1))
                    continue;
                _instance.minerals[i][x] = results[stringIndex];
                stringIndex++;
            }
        }
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
    public static void updateRoverPosition(AID aid, Location location) {
        _instance.rovers.put(aid, location);
        _instance.simView.redrawMap();

    }
    public static Location getRoverPosition(AID aid) {
        if(!_instance.rovers.containsKey(aid)) return null;
        return _instance.rovers.get(aid);
    }
    public static String getMineralAtPosition(Location location) {
        return _instance.minerals[location.getY()][location.getX()];
    }
    public static AID[] getAgentsInRange(int range, AID agent) {
        return getAgentsInRange(range, _instance.rovers.get(agent));
    }
    public static AID[] getAgentsInRange(int range, Location center) {
        ArrayList<Location> validLocations = LocationUtility.getCellsInRange(center, range, getSizeX(), getSizeY());
        ArrayList<AID> result = new ArrayList<>();

        _instance.rovers.forEach((aid, location) -> {
            for (Location loc : validLocations) {
                if (LocationUtility.areColliding(location, loc))
                {
                    result.add(aid);
                }
            }
        });
        if(result.size() == 0) return null;
        return (AID[]) result.toArray();
    }
    public static Location[] findLocationForRovers(int count) {
        ArrayList<Location> result = new ArrayList<>();
        Random rand = new Random(DateTime.now().getMillis());
        Point center = new Point();
        center.x = getSizeX()/2;
        center.y = getSizeY()/2;
        double radius = (getSizeX()<getSizeY()?getSizeX():getSizeY())*0.35;
        double initialAngle = rand.nextInt(360);
        double angle = initialAngle;
        double angleIncrement = 360/count;
        for(int i = 0; i < count; i++) {
            Location loc = new Location();
            loc.setX((int) (radius * Math.cos(angle) + center.x+1));
            loc.setY((int) (radius * Math.sin(angle) + center.y+1));
            while(!isValidPosition(loc.getX(), loc.getY()))
                loc.setY(loc.getY()+1);
            angle += angleIncrement;
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
