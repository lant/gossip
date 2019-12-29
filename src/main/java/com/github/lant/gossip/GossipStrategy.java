package com.github.lant.gossip;

import com.github.lant.gossip.rpc.Discovery;
import com.github.lant.gossip.rpc.GossipListenerGrpc;
import com.github.lant.gossip.rpc.Value;
import com.github.lant.gossip.server.GossipServer;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Peers peers;

    public GossipStrategy(Peers peers) {
        this.peers = peers;
    }

    // all the nodes will be in this network
    // private static final String subnet = "172.28.0.";
    private static final Logger log = LoggerFactory.getLogger(GossipServer.class);

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // it will try to propate the value to 3 nodes
    private static final Integer FANOUT = 3;

    // fire and forget. Propagate the value without waiting for the result.
    public void propagate(Value newValue) {
        executor.submit(() -> propagateToNodes(newValue));
    }

    private void propagateToNodes(Value newValue) {
        for (int comms = 0; comms < FANOUT; comms++) {
            String destinationNode = peers.selectObjectiveNode();
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

    public void connectToPeers() {
        // try to find a node from the same network (low ip)
        String destinationNode = peers.selectInitialPeer();
        try {
            Channel channel = ManagedChannelBuilder.forAddress(destinationNode, RPC_PORT).usePlaintext().build();
            GossipListenerGrpc.GossipListenerBlockingStub client = GossipListenerGrpc.newBlockingStub(channel);
            client.hi(Discovery.newBuilder().setMyip(peers.getMyOwnIp()).build());
        } catch (Exception e) {
            log.warn("Random seed peer {} was not available, Reason: {}", destinationNode, e.getMessage(), hostname());
        }
    }
}
