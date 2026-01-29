package com.acme.notifications.senders;

import com.acme.notifications.api.NotificationSender;
import com.acme.notifications.model.NotificationMessage;
import com.acme.notifications.result.SendResult;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class AbstractAsyncSender<M extends NotificationMessage> implements NotificationSender<M> {
    private final Executor executor;

    protected AbstractAsyncSender(Executor executor) {
        this.executor = Objects.requireNonNull(executor);
    }

    @Override
    public CompletableFuture<SendResult> sendAsync(M message) {
        return CompletableFuture.supplyAsync(() -> send(message), executor);
    }
}
