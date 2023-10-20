package com.nineleaps.leaps.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;


import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@DisplayName("Api Response Test")
class ApiResponseTest {

    @InjectMocks
    private ApiResponse apiResponse;

    @BeforeEach
    void setUp() {
        apiResponse = new ApiResponse(true, "Success");
    }

    @Test
    @DisplayName("Get Timestamp Test")
    void getTimestamp() {
        assertNotNull(apiResponse.getTimestamp());
    }

    @Test
    @DisplayName("Is Success Test")
    void isSuccess() {
        assertTrue(apiResponse.isSuccess());
    }

    @Test
    @DisplayName("Get Message Test")
    void getMessage() {
        assertEquals("Success", apiResponse.getMessage());
    }
}
