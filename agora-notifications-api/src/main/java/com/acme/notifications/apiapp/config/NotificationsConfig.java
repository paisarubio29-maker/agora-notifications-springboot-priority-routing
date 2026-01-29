package com.acme.notifications.apiapp.config;

import com.acme.notifications.api.NotificationClient;
import com.acme.notifications.config.NotificationRegistry;
import com.acme.notifications.model.EmailMessage;
import com.acme.notifications.model.PushMessage;
import com.acme.notifications.model.SmsMessage;
import com.acme.notifications.providers.EmailProvider;
import com.acme.notifications.providers.PushProvider;
import com.acme.notifications.providers.SmsProvider;
import com.acme.notifications.providers.simulated.SimulatedEmailProvider;
import com.acme.notifications.providers.simulated.SimulatedPushProvider;
import com.acme.notifications.providers.simulated.SimulatedSmsProvider;
import com.acme.notifications.providers.routing.RoutingEmailProvider;
import com.acme.notifications.providers.routing.RoutingSmsProvider;
import com.acme.notifications.providers.routing.RoutingPushProvider;
import com.acme.notifications.providers.routing.PriorityRoutingEmailProvider;
import com.acme.notifications.providers.routing.PriorityRoutingSmsProvider;
import com.acme.notifications.providers.routing.PriorityRoutingPushProvider;
import com.acme.notifications.providers.routing.PriorityRules;
import com.acme.notifications.model.NotificationPriority;


import com.acme.notifications.senders.EmailSender;
import com.acme.notifications.senders.PushSender;
import com.acme.notifications.senders.SmsSender;
import com.acme.notifications.validation.CommonValidators;
import com.acme.notifications.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class NotificationsConfig {


    @Bean(destroyMethod = "close")
    public ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public EmailProvider emailProvider() {
        var providers = java.util.Map.<String, EmailProvider>of(
                "SENDGRID", new SimulatedEmailProvider("SendGridSim", 0.10),
                "MAILGUN", new SimulatedEmailProvider("MailgunSim", 0.12)
        );


        var rules = new PriorityRules(java.util.Map.of(
                NotificationPriority.CRITICAL, "SENDGRID",
                NotificationPriority.HIGH, "SENDGRID",
                NotificationPriority.NORMAL, "MAILGUN",
                NotificationPriority.LOW, "MAILGUN"
        ));

        return new PriorityRoutingEmailProvider(providers, "SENDGRID", rules);
    }

    @Bean
    public SmsProvider smsProvider() {
        var providers = java.util.Map.<String, SmsProvider>of(
                "TWILIO", new SimulatedSmsProvider("TwilioSim", 0.08),
                "NEXMO", new SimulatedSmsProvider("NexmoSim", 0.09)
        );

        var rules = new PriorityRules(java.util.Map.of(
                NotificationPriority.CRITICAL, "TWILIO",
                NotificationPriority.HIGH, "TWILIO",
                NotificationPriority.NORMAL, "NEXMO",
                NotificationPriority.LOW, "NEXMO"
        ));

        return new PriorityRoutingSmsProvider(providers, "TWILIO", rules);
    }

    @Bean
    public PushProvider pushProvider() {
        var providers = java.util.Map.<String, PushProvider>of(
                "FIREBASE", new SimulatedPushProvider("FirebaseSim", 0.12),
                "ONESIGNAL", new SimulatedPushProvider("OneSignalSim", 0.11)
        );

        var rules = new PriorityRules(java.util.Map.of(
                NotificationPriority.CRITICAL, "FIREBASE",
                NotificationPriority.HIGH, "FIREBASE",
                NotificationPriority.NORMAL, "ONESIGNAL",
                NotificationPriority.LOW, "ONESIGNAL"
        ));

        return new PriorityRoutingPushProvider(providers, "FIREBASE", rules);
    }

    @Bean
    public Validator<EmailMessage> emailValidator() {
        return CommonValidators.emailTo()
                .and(CommonValidators.emailSubject())
                .and(CommonValidators.bodyNotBlank(EmailMessage::body));
    }

    @Bean
    public Validator<SmsMessage> smsValidator() {
        return CommonValidators.phoneE164()
                .and(CommonValidators.bodyNotBlank(SmsMessage::body));
    }

    @Bean
    public Validator<PushMessage> pushValidator() {
        return CommonValidators.deviceToken()
                .and(CommonValidators.bodyNotBlank(PushMessage::body));
    }

    @Bean
    public EmailSender emailSender(EmailProvider provider, Validator<EmailMessage> validator, Executor virtualThreadExecutor) {
        return new EmailSender(provider, validator, virtualThreadExecutor);
    }

    @Bean
    public SmsSender smsSender(SmsProvider provider, Validator<SmsMessage> validator, Executor virtualThreadExecutor) {
        return new SmsSender(provider, validator, virtualThreadExecutor);
    }

    @Bean
    public PushSender pushSender(PushProvider provider, Validator<PushMessage> validator, Executor virtualThreadExecutor) {
        return new PushSender(provider, validator, virtualThreadExecutor);
    }

    @Bean
    public NotificationRegistry notificationRegistry(EmailSender emailSender, SmsSender smsSender, PushSender pushSender) {
        return NotificationRegistry.builder()
                .register(EmailMessage.class, emailSender)
                .register(SmsMessage.class, smsSender)
                .register(PushMessage.class, pushSender)
                .build();
    }

    @Bean
    public NotificationClient notificationClient(NotificationRegistry registry) {
        return new NotificationClient(registry);
    }
}
