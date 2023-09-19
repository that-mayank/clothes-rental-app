package com.nineleaps.leaps.service.implementation;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nineleaps.leaps.dto.notifications.PushNotificationRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Transactional
public class PushNotificationServiceImpl {
    private final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);



    public void sendPushNotificationToToken(PushNotificationRequest request) {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getMessage())
                        .build())
                .setToken(request.getToken())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            logger.info("Sent message to token. Device token: {} , response: {}", request.getToken() , response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            logger.error("Error sending FCM message: {}", e.getMessage());
        }
    }

public void sendNotification(String token) {
    String title = "Order info";
    String pretext = "Order has been placed by a customer";
    Message message = Message.builder()
//            .setNotification(new Notification(title, pretext))
            .putData("title", title)
            .putData("text", pretext)
            .setToken(token)
            .build();
    try {
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        logger.info("Sent message to token. Device token: {}, response: {}", token ,response);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error("InterruptedException while sending FCM message: {}",e.getMessage());
    } catch (ExecutionException e) {
        logger.error("Error sending FCM message: {}",e.getMessage());
    }
}

}
