package com.github.lant.gossip.server;

import com.github.lant.gossip.GossipStrategy;
import com.github.lant.gossip.StateHandler;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GossipServer {
    private static final Logger log = LoggerFactory.getLogger(GossipServer.class);

    private final Server server;

    public GossipServer(int port, StateHandler stateHandler, GossipStrategy gossipStrategy) {
        server = ServerBuilder.forPort(port).addService(new GossipService(stateHandler, gossipStrategy))
                .build();
    }

    public void start() throws IOException, InterruptedException {
        server.start();
        log.info("Server started");
        server.awaitTermination();
    }

}
