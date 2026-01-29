package com.acme.notifications.result;

import java.util.List;

public record ValidationError(List<String> problems) implements SendError {
}
