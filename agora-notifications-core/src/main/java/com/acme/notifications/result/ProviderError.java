package com.acme.notifications.result;

public record ProviderError(String provider, String message, Throwable cause) implements SendError {
}
