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

@Service // Marks this class as a Spring service component
@AllArgsConstructor // Lombok's annotation to generate a constructor with all required fields
@Transactional // Marks this class as transactional for database operations
public class PushNotificationServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);

    // Send a push notification to a specific device token
    public void sendPushNotificationToToken(PushNotificationRequest request) {
        // Create a Firebase Cloud Messaging (FCM) message with notification content
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getMessage())
                        .build())
                .setToken(request.getToken()) // Set the recipient's device token
                .build();

        try {
            // Send the FCM message asynchronously and get the response
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            // Log a success message with the device token and FCM response
            logger.info("Sent message to token. Device token: {}, response: {}", request.getToken(), response);
        } catch (InterruptedException | ExecutionException e) {
            // Handle exceptions and log error messages
            Thread.currentThread().interrupt();
            logger.error("Error sending FCM message: {}", e.getMessage());
        }
    }

    // Send a notification with custom data to a specific device token
    public void sendNotification(String deviceToken) {
        String title = "Order info";
        String pretext = "Order has been placed by a customer";
        // Create an FCM message with custom data
        Message message = Message.builder()
                .putData("title", title)
                .putData("text", pretext)
                .setToken(deviceToken) // Set the recipient's device token
                .build();

        try {
            // Send the FCM message asynchronously and get the response
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            // Log a success message with the device token and FCM response
            logger.info("Sent message to token. Device token: {}, response: {}", deviceToken, response);
        } catch (InterruptedException e) {
            // Handle InterruptedException and log error messages
            Thread.currentThread().interrupt();
            logger.error("InterruptedException while sending FCM message: {}", e.getMessage());
        } catch (ExecutionException e) {
            // Handle ExecutionException and log error messages
            logger.error("Error sending FCM message: {}", e.getMessage());
        }
    }
}
