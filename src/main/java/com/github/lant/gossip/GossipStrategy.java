package com.github.lant.gossip;

import com.github.lant.gossip.rpc.Ack;
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

import static com.github.lant.gossip.server.GossipServer.RPC_PORT;

public class GossipStrategy {

    private static final String subnet = "172.28.1.";
    private static final Logger log = LoggerFactory.getLogger(GossipServer.class);

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private static final Integer FANOUT = 3;
    private final int totalMachines;

    GossipStrategy(int totalMachines) {
        this.totalMachines = totalMachines;
    }

    // TODO specify why this is threaded
    public void propagate(Value newValue) {
        executor.submit(() -> tryToPropagate(newValue));
    }

    private Boolean tryToPropagate(Value newValue) {
        for (int comms = 0; comms < FANOUT; comms++) {
            try {
                String destinationNode = selectObjectiveNode();
                log.info("Sending value to node: {}", destinationNode);
                if (propagateToNode(destinationNode, newValue)) {
                    log.info("[Machine {}] Successfully propagated new value to {}", InetAddress.getLocalHost().getHostAddress(), destinationNode);
                } else {
                    log.info("[Machine {}] Did not propagate new value to {}", InetAddress.getLocalHost().getHostAddress(), destinationNode);
                }
            } catch (Exception e) {
                // network exception or things like these
                log.error(e.getMessage());
            }
        }
        return true;
    }

    private boolean propagateToNode(String destinationNode, Value newValue) {
        Channel channel = ManagedChannelBuilder.forAddress(destinationNode, RPC_PORT).usePlaintext().build();
        GossipListenerGrpc.GossipListenerBlockingStub client = GossipListenerGrpc.newBlockingStub(channel);
        Ack response = client.receiveValue(newValue);
        return response.getSuccess();
    }

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
