package es.upm.company03;

import es.upm.company03.behaviors.HandleRegistration;
import es.upm.company03.behaviors.HandleCapsuleReleaseInform;
import es.upm.common03.RFBAgent;


/**
 * Company agent. Handles the initialization of other agents of the company.
 * Handles the following behaviors:
 * <br>
 *  - Company registration
 */
public class Company extends RFBAgent {


    final static String companySuffix = "03";

    @Override
    protected void setup() {
        System.out.printf("%s is starting up!%n", "Company" + companySuffix);

        //We define the behaviours outside and then add them.
        //Helps to keep the code more structurized and we can
        //do shit like moving the registration in the DFService
        //after adding the behaviors without much hassle.
        //TODO: remove this comment by 02/05/2016
        addBehaviour(new HandleRegistration(this));
        addBehaviour(new HandleCapsuleReleaseInform(this));

        registerSelfWithServices(new String[]{"Company03"});
        super.setup();


    }


}
