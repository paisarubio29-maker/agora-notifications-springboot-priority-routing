package com.acme.notifications.validation;

import com.acme.notifications.model.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommonValidatorsTest {

    @Test
    void email_validation() {
        var v = CommonValidators.emailTo();

        assertFalse(v.validate(
                new EmailMessage("bad", "s", "b", NotificationPriority.NORMAL, Map.of())
        ).isEmpty());

        assertTrue(v.validate(
                new EmailMessage("user@example.com", "s", "b", NotificationPriority.NORMAL, Map.of())
        ).isEmpty());
    }

    @Test
    void phone_e164_validation() {
        var v = CommonValidators.phoneE164();

        assertFalse(v.validate(
                new SmsMessage("09999", "b", NotificationPriority.NORMAL, Map.of())
        ).isEmpty());

        assertTrue(v.validate(
                new SmsMessage("+593999000111", "b", NotificationPriority.NORMAL, Map.of())
        ).isEmpty());
    }

    @Test
    void body_not_blank() {
        var v = CommonValidators.bodyNotBlank(EmailMessage::body);

        assertFalse(v.validate(
                new EmailMessage("u@e.com", "s", " ", NotificationPriority.NORMAL, Map.of())
        ).isEmpty());
    }
}
