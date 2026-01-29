package com.acme.notifications.providers.simulated;

import java.time.Duration;
import java.util.Random;

final class SimulatedProviderSupport {
    private static final Random RND = new Random();

    static void randomDelay(Duration min, Duration max) {
        long minMs = min.toMillis();
        long maxMs = max.toMillis();
        long sleep = minMs + (long) (RND.nextDouble() * (maxMs - minMs + 1));
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static boolean shouldFail(double failureRate) {
        return RND.nextDouble() < failureRate;
    }
}
