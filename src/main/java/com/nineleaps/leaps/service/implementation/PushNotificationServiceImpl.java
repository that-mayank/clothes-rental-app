//package com.nineleaps.leaps.service.implementation;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//import com.nineleaps.leaps.dto.pushNotification.PushNotificationRequest;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.util.concurrent.ExecutionException;
//
//@Service
//@AllArgsConstructor
//@Transactional
//@Slf4j
//public class PushNotificationServiceImpl {
//
//    private final FirebaseMessaging firebaseMessaging;
//
//    public void sendPushNotificationToToken(PushNotificationRequest request) {
//        Message message = buildMessage(request);
//        sendFirebaseMessage(message, request.getToken());
//    }
//
//    public void sendNotification(String token) {
//        String title = "Order info";
//        String topic = "Regarding orders";
//        PushNotificationRequest request = new PushNotificationRequest(title, topic,token);
//        Message message = buildMessage(request);
//        sendFirebaseMessage(message, token);
//    }
//
//    private Message buildMessage(PushNotificationRequest request) {
//        return Message.builder()
//                .setNotification(Notification.builder()
//                        .setTitle(request.getTitle())
//                        .setBody(request.getTopic())
//                        .build())
//                .setToken(request.getToken())
//                .build();
//    }
//
//    private void sendFirebaseMessage(Message message, String token) {
//        try {
//            String response = firebaseMessaging.sendAsync(message).get();
//            log.info("Sent message to token. Device token: {}, response: {}", token, response);
//        } catch (InterruptedException | ExecutionException e) {
//            Thread.currentThread().interrupt();
//            log.error("Error sending FCM message: {}", e.getMessage());
//        }
//    }
//}


package com.nineleaps.leaps.service.implementation;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nineleaps.leaps.dto.pushNotification.PushNotificationRequest;
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


    public void sendPushNotificationToToken(PushNotificationRequest request) {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getTopic())
                        .build())
                .setToken(request.getToken())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("Sent message to token. Device token: {} , response: {}", request.getToken() , response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error sending FCM message: {}", e.getMessage());
        }
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