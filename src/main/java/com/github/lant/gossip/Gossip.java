package com.github.lant.gossip;

import com.beust.jcommander.JCommander;
import com.github.lant.gossip.server.GossipServer;

import java.io.IOException;

/**
 * Main class that starts the server and checks the CLI's
 */
public class Gossip {

    private void run() throws IOException, InterruptedException {
        StateHandler stateHandler = new StateHandler();
        Peers peers = new Peers();
        GossipStrategy gossipStrategy = new GossipStrategy(peers);
        PeriodicPropagator periodicPropagator = new PeriodicPropagator(gossipStrategy, stateHandler);
        Bootstrapper bootstrapper = new Bootstrapper(peers);

        // try to find other peers
        new Thread(bootstrapper).start();

        // start the periodic propagation thread.
        new Thread(periodicPropagator).start();

        // just listen to incoming messages and block
        listenToMessages(stateHandler, gossipStrategy, peers);
    }

    private void listenToMessages(StateHandler stateHandler, GossipStrategy gossipStrategy, Peers peers) throws IOException, InterruptedException {
        new GossipServer(stateHandler, gossipStrategy, peers).start();
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
