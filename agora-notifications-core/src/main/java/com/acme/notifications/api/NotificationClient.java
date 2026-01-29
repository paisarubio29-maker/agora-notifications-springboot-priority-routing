package com.acme.notifications.api;

import com.acme.notifications.config.NotificationRegistry;
import com.acme.notifications.model.NotificationMessage;
import com.acme.notifications.result.SendResult;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class NotificationClient {
    private final NotificationRegistry registry;

    public NotificationClient(NotificationRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
    }

    public SendResult send(NotificationMessage message) {
        return registry.senderFor(message).send(message);
    }

    public CompletableFuture<SendResult> sendAsync(NotificationMessage message) {
        return registry.senderFor(message).sendAsync(message);
    }

    public CompletableFuture<List<SendResult>> sendBatchAsync(List<? extends NotificationMessage> messages) {
        var ordered = messages.stream()
                .sorted(Comparator.comparing(NotificationMessage::priority).reversed())
                .toList();

        var futures = ordered.stream().map(this::sendAsync).toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).toList());
    }
}
