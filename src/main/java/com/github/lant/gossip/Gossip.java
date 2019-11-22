package com.github.lant.gossip;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.lant.gossip.rpc.Value;
import com.github.lant.gossip.server.GossipServer;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Main class that starts the server and checks the CLI's
 */
public class Gossip {

    // used to know the maximum IP ranges
    @Parameter(names={"--machines", "-m"})
    int totalMachines = 10;

    // When a server starts it can try to read its old persisted state (if any)
    @Parameter(names = "--skip-state", description = "Don't try to recover old state")
    private boolean tryToRecover = true;

    // If the server has this flag it will propagate a new value to the rest of the
    // cluster. This could be a separate program.
    @Parameter(names = "--propagate", description = "Propagate new value")
    private String startPropagation = null;

    private void run() throws IOException, InterruptedException {
        System.out.printf("Hi, I'm %s\n", InetAddress.getLocalHost().getHostAddress());
        StateHandler stateHandler = new StateHandler();
        GossipStrategy gossipStrategy = new GossipStrategy(totalMachines);
        PeriodicPropagator periodicPropagator = new PeriodicPropagator(gossipStrategy, stateHandler);

        if (tryToRecover) {
            System.out.println("Try to recover state from a previous execution");
            // get the old value from the file.
            stateHandler.recoverFromFile();
        }

        // TODO remove this and put into a client or a specialized role.
        if (startPropagation != null) {
            System.out.printf("[Machine %s] I'm propagating %s to the network\n", InetAddress.getLocalHost().getHostAddress(), startPropagation);
            Value newValue = Value.newBuilder().setValue(startPropagation).setTimestamp(System.currentTimeMillis()).build();
            // update myself
            stateHandler.updateCurrent(newValue);
            gossipStrategy.propagate(newValue);
        }

        // start the periodic propagation thread.
        new Thread(periodicPropagator).start();

        // just listen to incoming messages and block
        listenToMessages(stateHandler, gossipStrategy);
    }

    private void listenToMessages(StateHandler stateHandler, GossipStrategy gossipStrategy) throws IOException, InterruptedException {
        new GossipServer(stateHandler, gossipStrategy).start();
    }

    public static void main(String ...args) throws IOException, InterruptedException {
        Gossip gossip = new Gossip();
        JCommander.newBuilder()
                .addObject(gossip)
                .build()
                .parse(args);
        gossip.run();
    }

}
