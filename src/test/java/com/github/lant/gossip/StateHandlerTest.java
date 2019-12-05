package com.github.lant.gossip;

import com.github.lant.gossip.rpc.Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateHandlerTest {

    @Test
    void currentOrNullWithEmptyValueIsNull() {
        StateHandler stateHandler = new StateHandler();
        assertNull(stateHandler.currentOrNull());
    }

    @Test
    void currentOrNullWithValueIsNotNull() {
        StateHandler stateHandler = new StateHandler();
        stateHandler.updateCurrent(getAValue());
        assertNotNull(stateHandler.currentOrNull());
    }

    @Test
    void getValue() {
        StateHandler stateHandler = new StateHandler();
        stateHandler.updateCurrent(getAValue());
        assertEquals(stateHandler.getValue(), getAValue());
    }

    @Test
    void hasCurrentValueWithEmptyValue() {
        StateHandler stateHandler = new StateHandler();
        assertFalse(stateHandler.hasCurrentValue());
    }

    @Test
    void hasCurrentValueWithValue() {
        StateHandler stateHandler = new StateHandler();
        stateHandler.updateCurrent(getAValue());
        assertTrue(stateHandler.hasCurrentValue());
    }

    private Value getAValue() {
        return Value.newBuilder().setValue("hi").setTimestamp(System.currentTimeMillis()).build();
    }
}