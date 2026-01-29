package com.acme.notifications.api;

import com.acme.notifications.model.NotificationMessage;
import com.acme.notifications.result.SendResult;

import java.util.concurrent.CompletableFuture;

public interface NotificationSender<M extends NotificationMessage> {
    SendResult send(M message);

    CompletableFuture<SendResult> sendAsync(M message);
}
