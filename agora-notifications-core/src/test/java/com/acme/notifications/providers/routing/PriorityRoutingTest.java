package com.acme.notifications.providers.routing;

import com.acme.notifications.model.*;
import com.acme.notifications.providers.EmailProvider;
import com.acme.notifications.result.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PriorityRoutingTest {

    private static EmailProvider provider(String id) {
        return new EmailProvider() {
            @Override public SendResult deliver(EmailMessage msg) {
                return new SendSuccess(id);
            }
            @Override public String name() { return id; }
        };
    }

    @Test
    void routes_by_priority_when_no_override() {
        var rules = new PriorityRules(Map.of(
                NotificationPriority.CRITICAL, "SENDGRID",
                NotificationPriority.LOW, "MAILGUN"
        ));

        var router = new PriorityRoutingEmailProvider(
                Map.of("SENDGRID", provider("sg"), "MAILGUN", provider("mg")),
                "SENDGRID",
                rules
        );

        var msg = new EmailMessage("u@e.com", "s", "b",
                NotificationPriority.LOW, Map.of());

        assertEquals("mg",
                ((SendSuccess) router.deliver(msg)).notificationId()
        );
    }

    @Test
    void metadata_override_wins() {
        var rules = new PriorityRules(Map.of(
                NotificationPriority.CRITICAL, "SENDGRID"
        ));

        var router = new PriorityRoutingEmailProvider(
                Map.of("SENDGRID", provider("sg"), "MAILGUN", provider("mg")),
                "SENDGRID",
                rules
        );

        var msg = new EmailMessage("u@e.com", "s", "b",
                NotificationPriority.CRITICAL,
                Map.of(ProviderKey.METADATA_KEY, "MAILGUN")
        );

        assertEquals("mg",
                ((SendSuccess) router.deliver(msg)).notificationId()
        );
    }
}
