package com.github.lant.gossip;

import com.github.lant.gossip.rpc.GossipListenerGrpc;
import com.github.lant.gossip.rpc.Value;
import com.github.lant.gossip.server.GossipServer;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.lant.gossip.logging.Logg.hostname;
import static com.github.lant.gossip.logging.Logg.message;
import static com.github.lant.gossip.server.GossipServer.RPC_PORT;

/**
 * This class implements the gossip protocol per se. It takes the decision to send a propagation value to
 * another host. It needs to react to failures.
 */
public class GossipStrategy {

    // all the nodes will be in this network
    private static final String subnet = "172.28.0.";
    private static final Logger log = LoggerFactory.getLogger(GossipServer.class);

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // it will try to propate the value to 3 nodes
    private static final Integer FANOUT = 3;
    private final int totalMachines;

    GossipStrategy(int totalMachines) {
        this.totalMachines = totalMachines;
    }

    // fire and forget. Propagate the value without waiting for the result.
    public void propagate(Value newValue) {
        executor.submit(() -> propagateToNodes(newValue));
    }

    private void propagateToNodes(Value newValue) {
        for (int comms = 0; comms < FANOUT; comms++) {
            String destinationNode = selectObjectiveNode();
            try {
                propagateToSingleNode(destinationNode, newValue);
            } catch (Exception e) {
                // network exception or things like these
                log.error("Could not propagate to {}, Reason: {}", destinationNode, e.getMessage(), hostname());
            }
        }
    }

    // RPC operation to send the vale to the other node.
    private void propagateToSingleNode(String destinationNode, Value newValue) {
        log.info("Sending value to node: {}", destinationNode, hostname(), message());
        Channel channel = ManagedChannelBuilder.forAddress(destinationNode, RPC_PORT).usePlaintext().build();
        GossipListenerGrpc.GossipListenerBlockingStub client = GossipListenerGrpc.newBlockingStub(channel);
        client.receiveValue(newValue);
    }

    /**
     * Selects another node in the cluster
     * This implementation is pretty naive, just gets a random one.
     */
    private String selectObjectiveNode() {
        int nextMachine;
        int hostIp = 0;
        try {
            hostIp = Integer.parseInt(InetAddress.getLocalHost().getHostAddress().split("\\.")[3]);
        } catch (UnknownHostException e) {
            log.error("Could not find my own IP :(");
        }
        do {
            nextMachine = new Random().nextInt(totalMachines);
        } while (nextMachine == hostIp);
        return subnet + nextMachine;
    }

}
