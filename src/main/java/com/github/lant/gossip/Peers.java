package com.github.lant.gossip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Peers {
    private final Set<String> peers = new HashSet<>();
    private int seedIdx = 0;
    private Random rd = new Random();

    public Optional<String> selectObjectiveNode() {
        if (peers.size() == 0) {
            return Optional.empty();
        }
       int idx = rd.nextInt(peers.size());
       String rdPeer = null;
       Iterator<String> peersIterator = peers.iterator();
       for (int ct = 0; ct < idx; ct++) {
           rdPeer = peersIterator.next();
       }
       return Optional.ofNullable(rdPeer);
    }

    public String selectInitialPeer() {
        return getMyNetwork() + "." + (++seedIdx);
    }

    public String getMyOwnIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMyNetwork() {
        String myIp = getMyOwnIp();
        return myIp.substring(0, myIp.lastIndexOf("."));
    }

    public boolean empty() {
        return peers.isEmpty();
    }

    public void addPeer(String newPeer) {
        peers.add(newPeer);
    }

    public Set<String> getPeers() {
        Set<String> peersToReturn = new HashSet<>();
        peersToReturn.add(getMyOwnIp());
        // This is far too much, needs to be a sample.
        peersToReturn.addAll(peers);
        return peersToReturn;
    }
}
