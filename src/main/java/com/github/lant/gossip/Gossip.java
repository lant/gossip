package com.github.lant.gossip;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.lant.gossip.server.GossipServer;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Span;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Main class that starts the server and checks the CLI's
 */
public class Gossip {

    private JaegerTracer initTracer(String service) {
        Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
        Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
        Configuration config = new Configuration(service).withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }

    // used to know the maximum IP ranges
    @Parameter(names={"--machines", "-m"})
    int totalMachines = 10;

    @Parameter(names = "--skip-state", description = "Don't try to recover old state")
    private boolean tryToRecover = true;

    private void run() throws IOException, InterruptedException {
        System.out.printf("Hi, I'm %s\n", InetAddress.getLocalHost().getHostAddress());
        StateHandler stateHandler = new StateHandler();
        GossipStrategy gossipStrategy = new GossipStrategy(totalMachines);
        PeriodicPropagator periodicPropagator = new PeriodicPropagator(gossipStrategy, stateHandler);

        JaegerTracer tracer = initTracer("gossip");

        Span span = tracer.buildSpan("doing nothing really").start();
        Thread.sleep(1000);
        span.finish();

        if (tryToRecover) {
            System.out.println("Try to recover state from a previous execution");
            // get the old value from the file.
            stateHandler.recoverFromFile();
        }

        // start the periodic propagation thread.
        new Thread(periodicPropagator).start();

        // just listen to incoming messages and block
        listenToMessages(stateHandler, gossipStrategy);
    }

    private void listenToMessages(StateHandler stateHandler, GossipStrategy gossipStrategy) throws IOException, InterruptedException {
        new GossipServer(stateHandler, gossipStrategy).start();
    }

    public static void main(String ...args) throws IOException, InterruptedException {
        Gossip gossip = new Gossip();
        JCommander.newBuilder()
                .addObject(gossip)
                .build()
                .parse(args);
        gossip.run();
    }

}
