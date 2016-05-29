package es.upm.company03;

import es.upm.common03.TeamAgent;
import es.upm.company03.behaviors.Company.HandleCapsuleReleaseInform;
import es.upm.company03.behaviors.Company.HandleRegistration;


/**
 * Company agent. Handles the initialization of other agents of the company.
 * Handles the following behaviours:
 * <br>
 *  - Company registration
 */
public class Company extends TeamAgent {


    final static String companySuffix = "03";

    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", "Company" + companySuffix);

        addBehaviour(new HandleRegistration(this));
        addBehaviour(new HandleCapsuleReleaseInform(this));

        registerSelfWithServices(new String[]{"Company03"});
        super.setup();


    }


}
