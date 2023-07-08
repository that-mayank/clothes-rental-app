package com.nineleaps.leaps.dto.checkout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StripeResponseTest {

    private StripeResponse stripeResponse;

    @BeforeEach
    void setUp() {
        stripeResponse = new StripeResponse();
    }

    @Test
    void getSessionId() {
        // Prepare
        String sessionId = "example-session-id";
        stripeResponse.setSessionId(sessionId);

        // Execute
        String result = stripeResponse.getSessionId();

        // Verify
        assertEquals(sessionId, result);
    }

    @Test
    void setSessionId() {
        // Prepare
        String sessionId = "example-session-id";

        // Execute
        stripeResponse.setSessionId(sessionId);

        // Verify
        assertEquals(sessionId, stripeResponse.getSessionId());
    }
}
