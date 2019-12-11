package com.github.lant.gossip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.lant.gossip.logging.Logg.hostname;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

public class PeriodicPropagator implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PeriodicPropagator.class);

    private static final long TEN_SECONDS = 10_000;
    private final GossipStrategy gossipStrategy;
    private final StateHandler stateHandler;
    private boolean keepRunning = true;

    public PeriodicPropagator(GossipStrategy gossipStrategy, StateHandler stateHandler) {
        this.gossipStrategy = gossipStrategy;
        this.stateHandler = stateHandler;
    }

    @Override
    public void run() {
        while (keepRunning) {
            try {
                Thread.sleep(TEN_SECONDS);
                if (stateHandler.hasCurrentValue()) {
                    log.info("Propagate value {}, ", keyValue("value", stateHandler.getValue()), hostname());
                    gossipStrategy.propagate(stateHandler.getValue());
                } else {
                    log.info("No value yet", keyValue("value", stateHandler.getValue()), hostname());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
    
    public void stop() {
        this.keepRunning = false; 
    }
}
