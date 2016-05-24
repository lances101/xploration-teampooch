package es.upm.platform03;

import es.upm.common03.CompanyAIDTuple;
import es.upm.common03.RFBAgent;
import es.upm.platform03.behaviours.Spacecraft.HandleRegistrationRequest;
import es.upm.platform03.behaviours.Spacecraft.HandleReleaseCapsule;

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


    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", getLocalName());



        //See comment in Company.java
        addBehaviour(new HandleRegistrationRequest(this, registrationPeriodSeconds, companies));
        addBehaviour(new HandleReleaseCapsule(this, companies));

        System.out.printf("%s: registration is up! Ends in %s seconds%n",
                getLocalName(), registrationPeriodSeconds);

        registerSelfWithServices(new String[]{"Spacecraft"});
        super.setup();

    }



}
