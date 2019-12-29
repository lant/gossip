package com.github.lant.gossip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class Peers {
    private final Set<String> peers = new HashSet<>();

    public String selectObjectiveNode() {
        return null;
    }

    public String selectInitialPeer() {

    }

    public String getMyOwnIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMyNetwork() {

    }

    public boolean empty() {
        return peers.isEmpty();
    }
}
