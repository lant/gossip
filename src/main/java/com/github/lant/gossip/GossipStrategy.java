package com.github.lant.gossip;

import com.github.lant.gossip.rpc.Ack;
import com.github.lant.gossip.rpc.GossipListenerGrpc;
import com.github.lant.gossip.rpc.Value;
import com.github.lant.gossip.server.GossipServer;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GossipStrategy {

    private static final Logger log = LoggerFactory.getLogger(GossipServer.class);

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private static final Integer BASE_PORT = 7000;
    private static final Integer PROPAGATION_LEVEL = 3;
    private final int port;
    private final int totalMachines;

    GossipStrategy(int port, int totalMachines) {
        this.port = port;
        this.totalMachines = totalMachines;
    }

    // TODO specify why this is threaded
    public Future<Boolean> propagate(Value newValue) {
        return executor.submit(() -> tryToPropagate(newValue));
    }

    private Boolean tryToPropagate(Value newValue) {
        for (int comms = 0; comms < PROPAGATION_LEVEL; comms++) {
            try {
                int destinationNode = selectObjectiveNode();
                if (propagateToNode(destinationNode, newValue)) {
                    System.out.printf("[Machine %d] Successfully propagated new value to %s", port, destinationNode);
                } else {
                    System.out.printf("[Machine %d] Did not propagate new value to %s", port, destinationNode);
                }
            } catch (Exception e) {
                // network exception or things like these
                log.error(e.getMessage());
            }
        }
        return true;
    }

    private boolean propagateToNode(int destinationNode, Value newValue) {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", destinationNode).usePlaintext().build();
        GossipListenerGrpc.GossipListenerBlockingStub client = GossipListenerGrpc.newBlockingStub(channel);
        Ack response = client.receiveValue(newValue);
        return response.getSuccess();
    }

    private int selectObjectiveNode() {
        int nextMachine;
        do {
            nextMachine = BASE_PORT + new Random().nextInt(totalMachines);
        } while (nextMachine == port);
        return nextMachine;
    }

}
