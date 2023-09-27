package com.nineleaps.leaps.dto.checkout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StripeResponseTest {

    @Test
    void stripeResponseCreation() {
        // Sample sessionId
        String sessionId = "sample_session_id";

        // Create a StripeResponse
        StripeResponse stripeResponse = new StripeResponse(sessionId);

        // Verify the sessionId property of the StripeResponse
        assertEquals(sessionId, stripeResponse.getSessionId());
    }
}
