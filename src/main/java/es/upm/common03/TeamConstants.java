package es.upm.common03;

/**
 * Created by borismakogonyuk on 08.05.16.
 */
public class TeamConstants {

    public static class Settings {
        public static final int RegistrationPeriodSeconds = 10;
        public static final int SimulationPeriodSeconds = 180;
        public static final int MovementPeriodSeconds = 1;
        public static final int ResearchPeriodSeconds = 1;
    }

    public static final class Direction {
        public final static int CANCEL = -1;
        public final static int UP = 1;
        public final static int UP_RIGHT = 2;
        public final static int DOWN_RIGHT = 3;
        public final static int DOWN = 4;
        public final static int DOWN_LEFT = 5;
        public final static int UP_LEFT = 6;
    }


}
