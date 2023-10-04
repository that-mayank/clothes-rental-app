package com.nineleaps.leaps.service.implementation;
import com.nineleaps.leaps.service.SmsSender;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Map;
import static org.mockito.Mockito.*;


 class SmsServiceImplTest {

    @Mock
    private SmsSender smsSender;

    @Mock
    private Helper helper;

    @InjectMocks
    private SmsServiceImpl smsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

     @Test
      void testSend() {
         // Stubbing generateOtp to return a fixed OTP
         when(smsService.helper.generateOtp()).thenReturn(1234);

         // Mock the smsSender to capture the arguments passed to sendSms
         doNothing().when(smsService.smsSender).sendSms(anyString(), anyString());

         // Call the send method
         smsService.send("1234567890");

         // Verify that generateOtp was called
         verify(smsService.helper).generateOtp();

         // Verify that smsSender.sendSms was called with the correct arguments
         verify(smsService.smsSender).sendSms("+911234567890", "Your OTP - 1234 please verify this otp");

         // Access and verify otpMap directly
         Map<String, Integer> otpMap = smsService.otpMap;
         Assertions.assertEquals(Integer.valueOf(1234), otpMap.get("1234567890"));
     }

     @Test
      void testVerifyOtpValid() {
         // Set up test data
         String phoneNumber = "1234567890";
         int storedOtp = 1234;
         int enteredOtp = 1234;

         // Set the stored OTP in otpMap
         Map<String, Integer> otpMap = smsService.otpMap;
         otpMap.put(phoneNumber, storedOtp);


         // Call the method to be tested
         boolean result = smsService.verifyOtp(phoneNumber, enteredOtp);

         // Verify the result
         Assertions.assertTrue(result);
     }

     @Test
      void testVerifyOtpInvalid() {
// Set up test data
         String phoneNumber = "1234567890";
         int storedOtp = 1234;
         int enteredOtp = 4321; // Different from storedOtp

         // Set the stored OTP in otpMap
         Map<String, Integer> otpMap = smsService.otpMap;
         otpMap.put(phoneNumber, storedOtp);


         // Call the method to be tested
         boolean result = smsService.verifyOtp(phoneNumber, enteredOtp);

         // Verify the result
         Assertions.assertFalse(result);
     }

     @Test
      void testVerifyOtpNullStoredOtp() {
         // Set up test data
         String phoneNumber = "1234567890";
         int enteredOtp = 1234;

         // Call the method to be tested with null stored OTP
         boolean result = smsService.verifyOtp(phoneNumber, enteredOtp);

         // Verify the result
         Assertions.assertFalse(result);
     }




}
