package com.acme.notifications.providers;

import com.acme.notifications.model.PushMessage;
import com.acme.notifications.result.SendResult;

public interface PushProvider {
    SendResult deliver(PushMessage msg);

    String name();
}
