package com.acme.notifications.result;

public sealed interface SendError permits ValidationError, ProviderError {
}
