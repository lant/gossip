package com.github.lant.gossip;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.Random;

public class Gossip {

    private static final Integer PROPAGATION_LEVEL = 3;

    @Parameter(names={"--port", "-p"}, required = true)
    int port;

    @Parameter(names={"--machines", "-m"}, required = true)
    int totalMachines;

    @Parameter(names = "--skip-state", description = "Don't try to recover old state")
    private boolean tryToRecover = true;

    @Parameter(names = "--propagate", description = "Propagate new value")
    private String startPropagation = null;


    private void propagateNewValue(String newValue) {
        for (int comms = 0; comms < PROPAGATION_LEVEL; comms++) {
            String destinationNode = getRandomMachine();
            while (!propagateToNode(destinationNode, newValue)) {
                destinationNode = getRandomMachine();
            }
            System.out.printf("[Machine %d] Successfully propagated new value to %s", port, destinationNode);
        }
    }

    private boolean propagateToNode(String destinationNode, String newValue) {
        // try to send data to that node
        return true;
    }

    private String getRandomMachine() {
        int nextMachine;
        do {
            nextMachine = new Random().nextInt(totalMachines);
        } while (nextMachine == port);
        return Integer.toString(nextMachine);
    }

    private void run() {
        System.out.printf("Hi, I'm %d\n", port);

        if (tryToRecover) {
            System.out.println("Try to recover state from a previous execution");
            // get the old value from the file.

        }

        if (startPropagation != null) {
            System.out.printf("[Machine %d] I'm propagating %s to the network\n", port, startPropagation);
            propagateNewValue(startPropagation);
        }

        // just listen to incoming messages
        listenToMessages();

        System.out.printf("[Machine %d] Received termination signal. Bye", port);
    }

    private void listenToMessages() {
    }

    public static void main(String ...args) {
        Gossip gossip = new Gossip();
        JCommander.newBuilder()
                .addObject(gossip)
                .build()
                .parse(args);
        gossip.run();
    }

}
