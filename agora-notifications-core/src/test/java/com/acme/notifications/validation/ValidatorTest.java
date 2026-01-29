package com.acme.notifications.validation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void pass_validator_has_no_errors() {
        Validator<String> v = Validator.pass();
        assertEquals(List.of(), v.validate("anything"));
    }

    @Test
    void and_composes_errors_in_order() {
        Validator<String> v1 = s -> List.of("e1");
        Validator<String> v2 = s -> List.of("e2", "e3");

        var combined = v1.and(v2);

        assertEquals(List.of("e1", "e2", "e3"), combined.validate("x"));
    }

    @Test
    void and_with_pass_keeps_original() {
        Validator<String> v1 = s -> List.of("e1");
        var combined = v1.and(Validator.pass());
        assertEquals(List.of("e1"), combined.validate("x"));
    }
}
