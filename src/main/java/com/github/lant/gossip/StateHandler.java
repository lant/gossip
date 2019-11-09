package com.github.lant.gossip;

import com.github.lant.gossip.rpc.Value;
import com.github.lant.gossip.rpc.ValueOrBuilder;

public class StateHandler {
    /**
     * Critical operation, if it does not succeed a Runtime Exception that terminates the program is launched.
     */
    public void recoverFromFile() {
    }


    public Value currentOrNull() {
        return null;
    }

    public Value getValue() {
        return null;
    }

    public boolean hasCurrentValue() {
        return false;
    }

    public void updateCurrent(Value request) {

    }
}
