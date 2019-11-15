package com.github.lant.gossip;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.lant.gossip.rpc.Value;
import com.github.lant.gossip.server.GossipServer;

import java.io.IOException;
import java.net.InetAddress;

public class Gossip {

    @Parameter(names={"--machines", "-m"})
    int totalMachines = 3;

    @Parameter(names = "--skip-state", description = "Don't try to recover old state")
    private boolean tryToRecover = true;

    @Parameter(names = "--propagate", description = "Propagate new value")
    private String startPropagation = null;

    private void run() throws IOException, InterruptedException {
        System.out.printf("Hi, I'm %s\n", InetAddress.getLocalHost().getHostAddress());
        StateHandler stateHandler = new StateHandler();
        GossipStrategy gossipStrategy = new GossipStrategy(totalMachines);

        if (tryToRecover) {
            System.out.println("Try to recover state from a previous execution");
            // get the old value from the file.
            stateHandler.recoverFromFile();
        }

        if (startPropagation != null) {
            System.out.printf("[Machine %s] I'm propagating %s to the network\n", InetAddress.getLocalHost().getHostAddress(), startPropagation);
            Value newValue = Value.newBuilder().setValue(startPropagation).setTimestamp(System.currentTimeMillis()).build();
            gossipStrategy.propagate(newValue);
        }

        // just listen to incoming messages
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
