package com.nineleaps.leaps.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class ApiResponseTest {

    @Test
    void getTimestamp_ReturnsCurrentTimestamp() {
        // Arrange
        ApiResponse apiResponse = new ApiResponse(true, "Success");

        // Act
        String timestamp = apiResponse.getTimestamp();

        // Assert
        Assertions.assertNotNull(timestamp);
//     Verify that the timestamp is within a reasonable range (e.g., not too far in the future or past)
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime parsedTimestamp = LocalDateTime.parse(timestamp);
        Assertions.assertTrue(parsedTimestamp.isBefore(currentDateTime.plusSeconds(1)));
        Assertions.assertTrue(parsedTimestamp.isAfter(currentDateTime.minusSeconds(1)));
    }
}

