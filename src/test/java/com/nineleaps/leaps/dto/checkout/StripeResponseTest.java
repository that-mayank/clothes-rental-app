package com.nineleaps.leaps.dto.checkout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("unit_tests")
@DisplayName("StripeResponse Tests")
class StripeResponseTest {

    @Test
    @DisplayName("StripeResponse Creation Test")
    void stripeResponseCreation() {
        // Sample sessionId
        String sessionId = "sample_session_id";

        // Create a StripeResponse
        StripeResponse stripeResponse = new StripeResponse(sessionId);

        // Verify the sessionId property of the StripeResponse
        assertEquals(sessionId, stripeResponse.getSessionId());
    }

    @Test
    @DisplayName("StripeResponse Setters Test")
     void testSetters() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        StripeResponse stripeResponse = new StripeResponse();
        String sessionId = "testSessionId";

        // Act
        Method setSessionId = StripeResponse.class.getDeclaredMethod("setSessionId", String.class);
        setSessionId.invoke(stripeResponse, sessionId);

        // Assert
        assertEquals(sessionId, stripeResponse.getSessionId());
    }
}
