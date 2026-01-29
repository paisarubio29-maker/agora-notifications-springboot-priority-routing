package com.acme.notifications.result;

public record SendFailure(String notificationId, SendError error) implements SendResult {
    @Override
    public boolean isSuccess() {
        return false;
    }

}
