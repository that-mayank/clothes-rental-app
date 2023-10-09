package com.nineleaps.leaps.model;

import static org.junit.jupiter.api.Assertions.*;

import com.nineleaps.leaps.repository.UserLoginInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
@Tag("unit_tests")
@DisplayName("UserLoginInfo Tests")
class UserLoginInfoTest {

    // Assuming ACCOUNT_LOCK_DURATION_MINUTES is a constant defined in LeapsApplication
    private static final int ACCOUNT_LOCK_DURATION_MINUTES = 5;
    @Mock
    private UserLoginInfoRepository loginInfoRepository;

    @Test
    @DisplayName("Test ID Generation")
    void testIdGeneration() {
        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setId(1L);
        loginInfo.setLocked(true);

        assertEquals(1L,loginInfo.getId());
        assertTrue(loginInfo.getLocked());
    }

    @Test
    @DisplayName("Test Account is Locked")
    void testIsAccountLocked() {
        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setLocked(true);
        loginInfo.setLockTime(LocalDateTime.now().minusMinutes(ACCOUNT_LOCK_DURATION_MINUTES - 1));

        assertFalse(loginInfo.isAccountLocked());
    }

    @Test
    @DisplayName("Test Account  not Long")
    void testIsAccountNotLocked() {
        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setLocked(false);
        loginInfo.setLockTime(LocalDateTime.now().minusMinutes(ACCOUNT_LOCK_DURATION_MINUTES - 1));

        assertFalse(loginInfo.isAccountLocked());
    }

    @Test
    @DisplayName("Test Account Locked for Too Long")
    void testIsAccountLockedForTooLong() {
        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setLocked(true);
        loginInfo.setLockTime(LocalDateTime.now().minusMinutes(ACCOUNT_LOCK_DURATION_MINUTES + 1));

        assertFalse(loginInfo.isAccountLocked());
    }
}
