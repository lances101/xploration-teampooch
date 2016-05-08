package es.upm.platform03;

import es.upm.company03.common.CompanyAIDTuple;
import es.upm.company03.common.RFBAgent;
import es.upm.platform03.behaviours.RegistrationBehavior;
import es.upm.platform03.behaviours.ReleaseCapsuleBehavior;
import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Spacecraft agent. Handles the initialization of other agents of the platform.
 * Handles the following behaviors:
 * <br>
 * - Company registration
 */
public class Spacecraft extends RFBAgent {

    ArrayList<CompanyAIDTuple> companies = new ArrayList<CompanyAIDTuple>();
    int registrationPeriodSeconds = 10;
    DateTime registrationDeadline;


    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", getLocalName());

        //See comment in Company.java
        addBehaviour(new RegistrationBehavior(this, registrationPeriodSeconds, companies));
        addBehaviour(new ReleaseCapsuleBehavior(this, registrationPeriodSeconds, companies));

        registrationDeadline = DateTime.now().plusSeconds(10);
        System.out.printf("%s: registration is up! Registration ends at %s%n",
                getLocalName(), registrationDeadline.toString("HH:mm:ss"));

        registerSelfWithServices(new String[]{"Spacecraft"});
        super.setup();
    }


}
