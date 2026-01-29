package com.acme.notifications.apiapp.web;

import com.acme.notifications.api.NotificationClient;
import com.acme.notifications.model.NotificationMessage;
import com.acme.notifications.result.SendResult;
import com.acme.notifications.apiapp.web.dto.NotificationRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    private final NotificationClient client;

    public NotificationsController(NotificationClient client) {
        this.client = client;
    }

    @PostMapping("/send")
    public SendResult send(@Valid @RequestBody NotificationRequest request) {
        NotificationMessage msg = NotificationMapper.toDomain(request);
        return client.send(msg);
    }

    @PostMapping("/send-async")
    public CompletableFuture<SendResult> sendAsync(@Valid @RequestBody NotificationRequest request) {
        NotificationMessage msg = NotificationMapper.toDomain(request);
        return client.sendAsync(msg);
    }

    @PostMapping("/send-batch-async")
    public CompletableFuture<List<SendResult>> sendBatchAsync(@Valid @RequestBody List<NotificationRequest> requests) {
        var msgs = requests.stream().map(NotificationMapper::toDomain).toList();
        return client.sendBatchAsync(msgs);
    }
}
