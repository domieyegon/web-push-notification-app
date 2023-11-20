package ke.unify.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Value("${vapid.public.key}")
    private String publicKey;
    @Value("${vapid.private.key}")
    private String privateKey;

    private PushService pushService;
    private List<Subscription> subscriptions = new ArrayList<>();

    @PostConstruct
    public void init() throws GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        pushService = new PushService(publicKey, privateKey);
    }

    public String getPublicKey(){
        return publicKey;
    }

    public void subscribe(Subscription subscription){
        System.out.println("Subscribed to " + subscription.endpoint);
        subscriptions.add(subscription);
    }

    public void unsubscribe(String endpoint) {
        System.out.println("Unsubscribed from " + endpoint);
        subscriptions = subscriptions.stream().filter(s -> !endpoint.equals(s.endpoint))
                .collect(Collectors.toList());
    }

    public void sendNotification(Subscription subscription, String messageJson)  {
        System.out.println("Request to send web push notification: "+ messageJson);
        try {
            pushService.send(new Notification(subscription, messageJson));
        } catch (GeneralSecurityException | IOException | JoseException | ExecutionException
                 | InterruptedException  e) {
            e.printStackTrace();
        }
    }



    @Scheduled(fixedRate = 15000)
    private void sendNotifications() {
        System.out.println("Sending notifications to all subscribers");

        var json = """
                        {
                            "notification": {
                                 "data" : {
                                    "onActionClick": {
                                        "default": { "operation": "openWindow" },
                                        "view": {
                                            "operation": "focusLastFocusedOrOpen",
                                            "url": "/view"
                                        },
                                        "like": {
                                            "operation": "navigateLastFocusedOrOpen",
                                            "url": "/like"
                                        }
                                    }
                                 },
                                 "body" : "You have a new order request from Amos",
                                 "title" : "New Order Request",
                                 "icon" : "https://bakeconnect.app/assets/images/bake-connect-logo-dual-tone.png",
                                 "actions": [
                                    { "action": "view", "title": "View" },
                                    { "action": "approve", "title": "Approve" },
                                    { "action": "reject", "title": "Reject" }
                                ]
                               }
                        }
                """;

        subscriptions.forEach(subscription -> {
            try {
                System.out.println(new ObjectMapper().writeValueAsString(subscription));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            sendNotification(subscription, String.format(json, LocalTime.now()));
        });
    }

}
