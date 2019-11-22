package com.github.lant.gossip;

public class PeriodicPropagator implements Runnable {

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
                System.out.println("I'm going to propagate my value JYI");
                if (stateHandler.hasCurrentValue()) {
                    gossipStrategy.propagate(stateHandler.getValue());
                } else {
                    System.out.println("Bah, I don't have any value, skipping.");
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
