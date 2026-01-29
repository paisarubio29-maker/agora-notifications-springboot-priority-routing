package com.acme.notifications.config;

import com.acme.notifications.api.NotificationSender;
import com.acme.notifications.model.NotificationMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class NotificationRegistry {
    private final Map<Class<? extends NotificationMessage>, NotificationSender<? extends NotificationMessage>> senders;

    private NotificationRegistry(Map<Class<? extends NotificationMessage>, NotificationSender<? extends NotificationMessage>> senders) {
        this.senders = Map.copyOf(senders);
    }

    @SuppressWarnings("unchecked")
    public <M extends NotificationMessage> NotificationSender<M> senderFor(M msg) {
        Objects.requireNonNull(msg, "message");
        var sender = (NotificationSender<M>) senders.get(msg.getClass());
        if (sender == null) throw new IllegalArgumentException("No sender registered for: " + msg.getClass().getName());
        return sender;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<Class<? extends NotificationMessage>, NotificationSender<? extends NotificationMessage>> map = new HashMap<>();

        public <M extends NotificationMessage> Builder register(Class<M> type, NotificationSender<M> sender) {
            map.put(type, sender);
            return this;
        }

        public NotificationRegistry build() {
            return new NotificationRegistry(map);
        }
    }
}
