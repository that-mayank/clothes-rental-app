package com.nineleaps.leaps.common;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
@Tag("unit_tests")
@DisplayName("Api Response Test Class")
class ApiResponseTest {
    @DisplayName("Api Response Test")
    @Test
    void testApiResponse() {
        boolean success = true;
        String message = "Test message";

        ApiResponse apiResponse = new ApiResponse(success, message);

        assertEquals(success, apiResponse.isSuccess());
        assertEquals(message, apiResponse.getMessage());

        // Verify the timestamp format (e.g., "2023-09-27T15:30:45.123456")
        String timestamp = apiResponse.getTimestamp();
        assertNotNull(timestamp);

        // The format can vary slightly, so we'll validate the general structure
        assertTrue(timestamp.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+"));
    }
}
