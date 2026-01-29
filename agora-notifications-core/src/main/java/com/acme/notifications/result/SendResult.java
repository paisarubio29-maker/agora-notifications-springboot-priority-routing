package com.acme.notifications.result;

public sealed interface SendResult permits SendSuccess, SendFailure {
    String notificationId();

    boolean isSuccess();
}
