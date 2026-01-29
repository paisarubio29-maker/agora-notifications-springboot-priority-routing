package com.acme.notifications.providers.routing;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProviderSelectorTest {

    @Test
    void default_is_used_when_key_is_null_or_blank() {
        var selector = new ProviderSelector<String, Integer>(
                Map.of("A", 1, "B", 2),
                "A",
                s -> s
        );

        assertEquals(1, selector.select(null));
        assertEquals(1, selector.select(""));
    }

    @Test
    void provider_is_selected_case_insensitive() {
        var selector = new ProviderSelector<String, Integer>(
                Map.of("SENDGRID", 10, "MAILGUN", 20),
                "SENDGRID",
                s -> s
        );

        assertEquals(20, selector.select("mailgun"));
    }
}
