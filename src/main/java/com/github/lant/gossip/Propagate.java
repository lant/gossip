package com.github.lant.gossip;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

import static com.github.lant.gossip.server.GossipServer.RPC_PORT;

public class Propagate {

    @Parameter(names={"--ip", "-i"}, required = true)
    String ip;

    @Parameter(names={"--value", "-v"}, required = true)
    String value;

    public static void main(String[] args) {
        Propagate propagate = new Propagate();
        JCommander.newBuilder()
                .addObject(propagate)
                .build()
                .parse(args);
        propagate.run();
    }

    private void run() {
        Channel channel = ManagedChannelBuilder.forAddress(ip, RPC_PORT).usePlaintext().build();
        com.github.lant.gossip.rpc.GossipListenerGrpc.GossipListenerBlockingStub client = com.github.lant.gossip.rpc.GossipListenerGrpc.newBlockingStub(channel);
        client.receiveValue(com.github.lant.gossip.rpc.Value.newBuilder().setTimestamp(System.currentTimeMillis()).setValue(value).build());
    }
}
