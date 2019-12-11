package com.github.lant.gossip.logging;

import net.logstash.logback.argument.StructuredArgument;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

public class Logg {

    /**
     * Get the host ip
     */
    public static StructuredArgument hostname() {
        try {
            return keyValue("server", InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Indicate that a message has been sent over the network.
     */
    public static StructuredArgument message() {
        return keyValue("messageSent", true);
    }
}
