package com.nineleaps.leaps.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    private ApiResponse apiResponse;

    @BeforeEach
    void setUp() {
        apiResponse = new ApiResponse(true, "Success");
    }

    @Test
    void getTimestamp() {
        assertNotNull(apiResponse.getTimestamp());
    }

    @Test
    void isSuccess() {
        assertTrue(apiResponse.isSuccess());
    }

    @Test
    void getMessage() {
        assertEquals("Success", apiResponse.getMessage());
    }
}
