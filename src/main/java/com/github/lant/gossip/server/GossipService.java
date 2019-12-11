package com.github.lant.gossip.server;

import com.github.lant.gossip.GossipStrategy;
import com.github.lant.gossip.StateHandler;
import com.github.lant.gossip.rpc.GossipListenerGrpc;
import com.github.lant.gossip.rpc.Value;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.lant.gossip.logging.Logg.hostname;


public class GossipService extends GossipListenerGrpc.GossipListenerImplBase {
    private static final Logger log = LoggerFactory.getLogger(GossipService.class);
    private final StateHandler stateHandler;
    private final GossipStrategy gossipStrategy;

    GossipService(StateHandler stateHandler, GossipStrategy gossipStrategy) {
        super();
        this.stateHandler = stateHandler;
        this.gossipStrategy = gossipStrategy;
    }

    @Override
    public void getLatestValue(Empty request, StreamObserver<Value> responseObserver) {
        responseObserver.onNext(stateHandler.getValue());
        responseObserver.onCompleted();
    }

    @Override
    public void receiveValue(Value request, StreamObserver<Empty> responseObserver) {
        // 3 things could happen here:

        // This node does not have any value at all yet (first branch)
        if (!stateHandler.hasCurrentValue()) {
            log.info("I did not have any value, now I received one ({})", request, hostname());
            stateHandler.updateCurrent(request);
            responseObserver.onNext(Empty.newBuilder().build());
            gossipStrategy.propagate(request);
        // This node has that same value (or a new one) (second branch)
        } else if (stateHandler.getValue().getTimestamp() >= request.getTimestamp()) {
            // my current value is the same or newer than one sent by another node. Ignore.
            log.info("Got a repeated value ({}). Ignoring!", request.getValue(), hostname());
            responseObserver.onNext(Empty.newBuilder().build());
        // This node has an outdated value (third branch)
        } else {
            log.info("Updated my value, now I have a new one ({})", request.getValue(), hostname());
            stateHandler.updateCurrent(request);
            responseObserver.onNext(Empty.newBuilder().build());
            gossipStrategy.propagate(request);
        }
        responseObserver.onCompleted();
    }
}
