package com.github.lant.gossip;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
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

    private void run() throws IOException, InterruptedException {
        System.out.printf("Hi, I'm %s\n", InetAddress.getLocalHost().getHostAddress());
        StateHandler stateHandler = new StateHandler();
        GossipStrategy gossipStrategy = new GossipStrategy(totalMachines);
        PeriodicPropagator periodicPropagator = new PeriodicPropagator(gossipStrategy, stateHandler);

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
