package com.acme.notifications.providers.simulated;

import com.acme.notifications.model.PushMessage;
import com.acme.notifications.providers.PushProvider;
import com.acme.notifications.result.ProviderError;
import com.acme.notifications.result.SendFailure;
import com.acme.notifications.result.SendResult;
import com.acme.notifications.result.SendSuccess;

import java.time.Duration;
import java.util.UUID;

public final class SimulatedPushProvider implements PushProvider {
    private final String name;
    private final double failureRate;

    public SimulatedPushProvider(String name, double failureRate) {
        this.name = name;
        this.failureRate = failureRate;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SendResult deliver(PushMessage msg) {
        SimulatedProviderSupport.randomDelay(Duration.ofMillis(60), Duration.ofMillis(220));
        var id = UUID.randomUUID().toString();
        if (SimulatedProviderSupport.shouldFail(failureRate)) {
            var ex = new RuntimeException("Simulated Push provider failure");
            return new SendFailure(id, new ProviderError(name, ex.getMessage(), ex));
        }
        return new SendSuccess(id);
    }
}
