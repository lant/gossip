package com.github.lant.gossip;

import com.github.lant.gossip.rpc.Value;
import com.github.lant.gossip.rpc.ValueOrBuilder;

public class StateHandler {
    private Value current = null;

    /**
     * Critical operation, if it does not succeed a Runtime Exception that terminates the program is launched.
     */
    public void recoverFromFile() {
    }


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
