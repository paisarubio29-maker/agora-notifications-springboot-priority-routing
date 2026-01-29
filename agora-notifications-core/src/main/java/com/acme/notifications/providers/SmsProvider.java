package com.acme.notifications.providers;

import com.acme.notifications.model.SmsMessage;
import com.acme.notifications.result.SendResult;

public interface SmsProvider {
    SendResult deliver(SmsMessage msg);

    String name();
}
