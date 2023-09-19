package com.nineleaps.leaps.dto.notifications;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PushNotificationRequest {
    private String title = "Order info";
    private String message = "Order has been placed by a customer";
    private String topic;
    private String token;
}

