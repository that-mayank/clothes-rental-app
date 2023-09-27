package com.nineleaps.leaps.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PaymentsRecordTest {

    @Test
    void testPaymentsRecord() {
        // Create a PaymentsRecord instance
        PaymentsRecord paymentsRecord = new PaymentsRecord();

        // Verify the ID is initially 0 (default value)
        assertEquals(0, paymentsRecord.getId());

        // Set an ID and verify it
        long id = 12345;
        paymentsRecord.setId(id);
        assertEquals(id, paymentsRecord.getId());
    }
}
