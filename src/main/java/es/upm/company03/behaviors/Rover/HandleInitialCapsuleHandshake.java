package es.upm.company03.behaviors.Rover;

import es.upm.company03.Rover;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Created by borismakogonyuk on 31.05.16.
 */
public class HandleInitialCapsuleHandshake extends OneShotBehaviour {
    Rover agent;
    AID broker;

    public HandleInitialCapsuleHandshake(Rover agent, AID broker) {
        super(agent);
        this.agent = agent;
        this.broker = broker;
    }

    @Override
    public void action() {
        System.out.println("CAPSULE HANDSHAKING!");
    }
}
