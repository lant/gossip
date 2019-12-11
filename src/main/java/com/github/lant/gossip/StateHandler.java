package com.github.lant.gossip;

import com.github.lant.gossip.rpc.Value;

/**
 * This class needs to be implemented if we want the system to be able to persist the state between reboots.
 */
public class StateHandler {
    private static final Value NONE = Value.newBuilder().setValue("NONE").setTimestamp(0L).build();

    private Value current = NONE;

    public Value getValue() {
        return current;
    }

    public boolean hasCurrentValue() {
        return (current.getTimestamp() != 0L);
    }

    public void updateCurrent(Value request) {
        current = request;
    }
}
