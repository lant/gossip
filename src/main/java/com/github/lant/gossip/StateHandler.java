package com.github.lant.gossip;

import com.github.lant.gossip.rpc.Value;

/**
 * This class needs to be implemented if we want the system to be able to persist the state between reboots.
 */
public class StateHandler {
    private Value current = null;

    public Value currentOrNull() {
        return current;
    }

    public Value getValue() {
        return current;
    }

    public boolean hasCurrentValue() {
        return current != null;
    }

    public void updateCurrent(Value request) {
        current = request;
    }
}
