package com.nineleaps.leaps.dto.pushnotification;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PushNotificationRequest {
    private String title = "Order info";
    private String topic;
    private String token;
}

