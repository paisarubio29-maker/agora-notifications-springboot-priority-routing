package com.acme.notifications.validation;

import com.acme.notifications.model.EmailMessage;
import com.acme.notifications.model.PushMessage;
import com.acme.notifications.model.SmsMessage;

import java.util.List;
import java.util.regex.Pattern;

public final class CommonValidators {
    private CommonValidators() {
    }

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{7,14}$");

    public static Validator<EmailMessage> emailTo() {
        return msg -> (msg.to() == null || !EMAIL.matcher(msg.to()).matches())
                ? List.of("Invalid email 'to' address")
                : List.of();
    }

    public static Validator<EmailMessage> emailSubject() {
        return msg -> (msg.subject() == null || msg.subject().isBlank())
                ? List.of("Email subject must not be blank")
                : List.of();
    }

    public static Validator<SmsMessage> phoneE164() {
        return msg -> (msg.to() == null || !E164.matcher(msg.to()).matches())
                ? List.of("Invalid phone number (expected E.164 like +593999000111)")
                : List.of();
    }

    public static Validator<PushMessage> deviceToken() {
        return msg -> (msg.to() == null || msg.to().isBlank())
                ? List.of("Push device token must not be blank")
                : List.of();
    }

    public static <T> Validator<T> bodyNotBlank(java.util.function.Function<T, String> bodyFn) {
        return msg -> {
            var body = bodyFn.apply(msg);
            return (body == null || body.isBlank()) ? List.of("Body must not be blank") : List.of();
        };
    }
}
