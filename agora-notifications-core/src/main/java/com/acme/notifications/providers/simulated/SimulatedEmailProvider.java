package com.acme.notifications.providers.simulated;

import com.acme.notifications.model.EmailMessage;
import com.acme.notifications.providers.EmailProvider;
import com.acme.notifications.result.ProviderError;
import com.acme.notifications.result.SendFailure;
import com.acme.notifications.result.SendResult;
import com.acme.notifications.result.SendSuccess;

import java.time.Duration;
import java.util.UUID;

public final class SimulatedEmailProvider implements EmailProvider {
    private final String name;
    private final double failureRate;

    public SimulatedEmailProvider(String name, double failureRate) {
        this.name = name;
        this.failureRate = failureRate;
    }

    @Override public String name() { return name; }

    @Override
    public SendResult deliver(EmailMessage msg) {
        SimulatedProviderSupport.randomDelay(Duration.ofMillis(80), Duration.ofMillis(250));
        var id = UUID.randomUUID().toString();
        if (SimulatedProviderSupport.shouldFail(failureRate)) {
            var ex = new RuntimeException("Simulated email provider failure");
            return new SendFailure(id, new ProviderError(name, ex.getMessage(), ex));
        }
        return new SendSuccess(id);
    }
}
