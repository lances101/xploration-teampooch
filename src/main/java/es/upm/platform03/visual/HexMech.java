package es.upm.platform03.visual;

import java.awt.*;

public class HexMech {

    //Constants
    public final static boolean orFLAT = true;
    public final static boolean orPOINT = false;
    public static boolean ORIENT = orFLAT;  //this is not used. We're never going to do pointy orientation

    public static boolean XYVertex = true;    //true: x,y are the co-ords of the first vertex.
    //false: x,y are the co-ords of the top left rect. co-ord.

    private static int BORDERS = 50;    //default number of pixels for the border.

    private static int s = 0;    // length of one side
    private static int t = 0;    // short side of 30o triangle outside of each hex
    private static int r = 0;    // radius of inscribed circle (centre to middle of each side). r= h/2
    private static int h = 0;    // height. Distance between centres of two adjacent hexes. Distance between two opposite sides in a hex.

    public static void setXYasVertex(boolean b) {
        XYVertex = b;
    }

    public static void setBorders(int b) {
        BORDERS = b;
    }

    public static void setHeight(int height) {
        h = height;            // h = basic dimension: height (distance between two adj centresr aka size)
        r = h / 2;            // r = radius of inscribed circle
        s = (int) (h / 1.73205);    // s = (h/2)/cos(30)= (h/2) / (sqrt(3)/2) = h / sqrt(3)
        t = (int) (r / 1.73205);    // t = (h/2) tan30 = (h/2) 1/sqrt(3) = h / (2 sqrt(3)) = r / sqrt(3)
    }

    public static Polygon hex(int x0, int y0) {

        int y = y0 + BORDERS;
        int x = x0 + BORDERS;
        if (s == 0 || h == 0) {
            System.out.println("ERROR: size of hex has not been set");
            return new Polygon();
        }

        int[] cx, cy;

        if (XYVertex)
            cx = new int[]{x, x + s, x + s + t, x + s, x, x - t};  //this is for the top left vertex being at x,y. Which means that some of the hex is cutoff.
        else
            cx = new int[]{x + t, x + s + t, x + s + t + t, x + s + t, x + t, x};    //this is for the whole hexagon to be below and to the right of this point

        cy = new int[]{y, y, y + r, y + r + r, y + r + r, y + r};
        return new Polygon(cx, cy, 6);

    }

    public static void drawHex(Graphics g, int i, int j, Color background, Color foreground) {
        int x = i * (s + t);
        int y = j * h + (i % 2) * h / 2;
        Polygon poly = hex(x, y);
        g.setColor(background);
        g.fillPolygon(poly);
        g.setColor(foreground);
        g.drawPolygon(poly);
    }

    public static void fillHexCoordinatesAndMineral(Graphics g, int i, int j, int minX, int minY, String n, Color background, Color foreground) {
        int x = i * (s + t);
        int y = j * h + (i % 2) * h / 2;
        g.setColor(background);
        g.fillPolygon(hex(x, y));
        g.setColor(foreground);
        g.drawPolygon(hex(x,y));
        g.drawString(n, x + r + BORDERS, y + r + BORDERS + 4); //FIXME: handle XYVertex
        String toDraw = String.format("%s|%s", minX, minY);
        g.drawString(toDraw, x + r, y + r ); //FIXME: handle XYVertex


    }

}