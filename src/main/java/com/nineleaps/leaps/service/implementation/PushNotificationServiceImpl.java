
package com.nineleaps.leaps.service.implementation;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nineleaps.leaps.dto.pushnotification.PushNotificationRequest;
import com.nineleaps.leaps.exceptions.CustomException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class PushNotificationServiceImpl {





    public void sendPushNotificationToToken(PushNotificationRequest request) throws CustomException {
        Message message = createMessage(request.getTitle(), request.getTopic(), request.getToken());

        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("Sent message to token. Device token: {} , response: {}. Title: {}, Topic: {}", request.getToken() , response, request.getTitle(), request.getTopic());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error sending FCM message. Title: {}, Topic: {}. Error: {}", request.getTitle(), request.getTopic(), e.getMessage());
            throw new CustomException("Error sending FCM message");
        }
    }

    private Message createMessage(String title, String body, String token) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(token)
                .build();
    }

    public void sendNotification(String token) {
        String title = "Order info";
        String pretext = "Order has been placed by a customer";
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(pretext)
                        .build())
                .setToken(token)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("Sent message to token. Device token: {}, response: {}", token ,response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("InterruptedException while sending FCM message: {}",e.getMessage());
        } catch (ExecutionException e) {
            log.error("Error sending FCM message: {}",e.getMessage());
        }
    }


}

