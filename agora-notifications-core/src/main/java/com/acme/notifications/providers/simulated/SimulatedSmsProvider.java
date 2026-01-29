package com.acme.notifications.providers.simulated;

import com.acme.notifications.model.SmsMessage;
import com.acme.notifications.providers.SmsProvider;
import com.acme.notifications.result.ProviderError;
import com.acme.notifications.result.SendFailure;
import com.acme.notifications.result.SendResult;
import com.acme.notifications.result.SendSuccess;

import java.time.Duration;
import java.util.UUID;

public final class SimulatedSmsProvider implements SmsProvider {
    private final String name;
    private final double failureRate;

    public SimulatedSmsProvider(String name, double failureRate) {
        this.name = name;
        this.failureRate = failureRate;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SendResult deliver(SmsMessage msg) {
        SimulatedProviderSupport.randomDelay(Duration.ofMillis(50), Duration.ofMillis(180));
        var id = UUID.randomUUID().toString();
        if (SimulatedProviderSupport.shouldFail(failureRate)) {
            var ex = new RuntimeException("Simulated SMS provider failure");
            return new SendFailure(id, new ProviderError(name, ex.getMessage(), ex));
        }
        return new SendSuccess(id);
    }
}
