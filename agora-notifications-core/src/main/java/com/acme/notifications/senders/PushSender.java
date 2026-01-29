package com.acme.notifications.senders;

import com.acme.notifications.model.PushMessage;
import com.acme.notifications.providers.PushProvider;
import com.acme.notifications.result.SendFailure;
import com.acme.notifications.result.SendResult;
import com.acme.notifications.result.ValidationError;
import com.acme.notifications.validation.Validator;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;

public final class PushSender extends AbstractAsyncSender<PushMessage> {
    private final PushProvider provider;
    private final Validator<PushMessage> validator;

    public PushSender(PushProvider provider, Validator<PushMessage> validator, Executor executor) {
        super(executor);
        this.provider = Objects.requireNonNull(provider);
        this.validator = Objects.requireNonNull(validator);
    }

    @Override
    public SendResult send(PushMessage message) {
        var errors = validator.validate(message);
        if (!errors.isEmpty()) {
            return new SendFailure(UUID.randomUUID().toString(), new ValidationError(List.copyOf(errors)));
        }
        return provider.deliver(message);
    }
}
