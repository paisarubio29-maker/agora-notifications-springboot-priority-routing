package com.acme.notifications.validation;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface Validator<M> {
    List<String> validate(M message);

    default Validator<M> and(Validator<M> other) {
        return msg -> {
            var errors = new ArrayList<>(this.validate(msg));
            errors.addAll(other.validate(msg));
            return errors;
        };
    }

    static <M> Validator<M> pass() {
        return msg -> List.of();
    }
}
