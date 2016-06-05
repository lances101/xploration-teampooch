package es.upm.platform03.visual;

import es.upm.ontology.Location;
import jade.core.AID;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SimulationView extends JFrame{
    SimulationCanvas canvas;
    public SimulationView(String title, String[][] minerals, HashMap<AID, Location> rovers, HashMap<AID, Location> capsules) throws HeadlessException {
        super(title);
        this.setSize(800, 800);
        canvas = new SimulationCanvas(minerals, rovers, capsules);
        this.add(canvas);
    }
    public void redrawMap()
    {
        canvas.repaint();
    }

    public class SimulationCanvas extends Canvas
    {
        private final HashMap<AID, Location> capsules;
        private final HashMap<AID, Location> rovers;
        private final String[][] minerals;

        private double hexRadius = 15;
        private int scalingFactor = 20;
        public SimulationCanvas(String[][] minerals, HashMap<AID, Location> rovers, HashMap<AID, Location> capsules) {
            this.minerals = minerals;
            this.rovers = rovers;
            this.capsules = capsules;
            HexMech.setXYasVertex(false);
            HexMech.setHeight(60);
            HexMech.setBorders(15);

        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            for(int y = 1; y < minerals.length; y++)
                for(int x = 1; x < minerals[0].length; x++) {
                    if(minerals[y][x] == null) continue;
                    /*
                    Ugly variable HAXX. Needed since we have an abnormal
                    hex grid that does not cover full rows.
                    */
                    HexMech.fillHexCoordinatesAndMineral(g, x-1, y-y/2-1, x, y, minerals[y][x], Color.yellow, Color.black);

                }
            for(Map.Entry<AID, Location> entry : capsules.entrySet())
            {
                Location loc = entry.getValue();
                HexMech.drawHex(g, loc.getX()-1, loc.getY()-loc.getY()/2-1, Color.blue, Color.black);
            }

            for(Map.Entry<AID, Location> entry : rovers.entrySet())
            {
                Location loc = entry.getValue();
                HexMech.drawHex(g, loc.getX()-1, loc.getY()-loc.getY()/2-1, Color.red, Color.black);

            }
        }


    }

}
