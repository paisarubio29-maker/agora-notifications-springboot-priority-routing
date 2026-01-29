package com.acme.notifications.senders;

import com.acme.notifications.model.*;
import com.acme.notifications.providers.*;
import com.acme.notifications.result.*;
import com.acme.notifications.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class SendersTest {

    private static final Executor DIRECT = Runnable::run;

    @Test
    void validation_error_prevents_provider_call() {
        EmailProvider provider = new EmailProvider() {
            @Override public SendResult deliver(EmailMessage msg) {
                fail("Should not be called");
                return null;
            }
            @Override public String name() { return "test"; }
        };

        Validator<EmailMessage> validator = msg -> List.of("error");

        var sender = new EmailSender(provider, validator, DIRECT);

        var result = sender.send(new EmailMessage(
                "x", "s", "b", NotificationPriority.NORMAL, Map.of()
        ));

        assertTrue(result instanceof SendFailure);
    }

    @Test
    void async_sender_works() {
        SmsProvider provider = new SmsProvider() {
            @Override public SendResult deliver(SmsMessage msg) {
                return new SendSuccess("sms-1");
            }
            @Override public String name() { return "sms"; }
        };

        var sender = new SmsSender(provider, msg -> List.of(), DIRECT);

        var result = sender.sendAsync(
                new SmsMessage("+593999000111", "hi", NotificationPriority.NORMAL, Map.of())
        ).join();

        assertEquals("sms-1", ((SendSuccess) result).notificationId());
    }
}
