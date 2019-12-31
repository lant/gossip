package com.github.lant.gossip;

import com.github.lant.gossip.rpc.Discovery;
import com.github.lant.gossip.rpc.DiscoveryResponse;
import com.github.lant.gossip.rpc.GossipListenerGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.lant.gossip.logging.Logg.hostname;
import static com.github.lant.gossip.server.GossipServer.RPC_PORT;

public class Bootstrapper implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Bootstrapper.class);
    private static final long BOOTSTRAP_WAIT = 1000L;
    private final Peers peers;

    public Bootstrapper(Peers peers) {
        this.peers = peers;
    }

    @Override
    public void run() {
        // try to find a node from the same network (low ip)
        while (peers.empty()) {
            String destinationNode = peers.selectInitialPeer();
            try {
                Channel channel = ManagedChannelBuilder.forAddress(destinationNode, RPC_PORT).usePlaintext().build();
                GossipListenerGrpc.GossipListenerBlockingStub client = GossipListenerGrpc.newBlockingStub(channel);
                log.info("Trying to bootstrap using {}", destinationNode, hostname());
                DiscoveryResponse response = client.hi(Discovery.newBuilder().setMyip(peers.getMyOwnIp()).build());
                if (response.getIpsCount() > 0) {
                    for (int idx = 0; idx < response.getIpsCount(); idx++) {
                        peers.addPeer(response.getIps(idx));
                    }
                }
            } catch (Exception e) {
                log.warn("Peer {} was not available for bootstrapping, Reason: {}", destinationNode, e.getMessage(), hostname());
            }
            try {
                Thread.sleep(BOOTSTRAP_WAIT);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
