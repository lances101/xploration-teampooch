package es.upm.common03.behaviors;

import es.upm.common03.TeamAgent;
import jade.core.behaviours.TickerBehaviour;
import org.joda.time.DateTime;

/**
 * Created by borismakogonyuk on 25.05.16.
 */
public class EmptyTimeoutBehavior extends TickerBehaviour {

    DateTime endTime;
    public EmptyTimeoutBehavior(TeamAgent teamAgent, int timeoutMillis)
    {
        super(teamAgent, 250);
        endTime = DateTime.now().plusMillis(timeoutMillis);

    }
    @Override
    protected void onTick() {
        if(endTime.isBeforeNow())
        {
            this.stop();
        }
    }
}
