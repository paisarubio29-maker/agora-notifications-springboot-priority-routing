package com.acme.notifications.result;

public record SendSuccess(String notificationId) implements SendResult {
    @Override
    public boolean isSuccess() {
        return true;
    }
}
