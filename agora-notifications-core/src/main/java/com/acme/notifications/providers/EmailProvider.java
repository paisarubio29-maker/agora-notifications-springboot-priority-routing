package com.acme.notifications.providers;

import com.acme.notifications.model.EmailMessage;
import com.acme.notifications.result.SendResult;

public interface EmailProvider {
    SendResult deliver(EmailMessage msg);

    String name();
}
