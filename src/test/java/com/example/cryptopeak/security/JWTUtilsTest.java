package com.example.cryptopeak.security;

import com.example.cryptopeak.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilsTest {

    private JWTUtils jwtUtils;
    private MyUserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = new JWTUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKeyForJWTTokenGenerationInTestEnvironment");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000);

        User user = new User();
        user.setId(1L);
        user.setEmailAddress("test@example.com");
        user.setPassword("password");
        user.setActive(true);
        user.setAccountVerified(true);

        userDetails = new MyUserDetails(user);
    }

    @Test
    void generateJwtToken_Success() {
        String token = jwtUtils.generateJwtToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void validateJwtToken_ValidToken_ReturnsTrue() {
        String token = jwtUtils.generateJwtToken(userDetails);

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateJwtToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.jwt.token";

        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void getUserNameFromJwtToken_Success() {
        String token = jwtUtils.generateJwtToken(userDetails);

        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void validateJwtToken_ExpiredToken_ReturnsFalse() {
        // Create JWT utils with very short expiration
        JWTUtils shortExpirationJwtUtils = new JWTUtils();
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "jwtSecret", "testSecretKeyForJWTTokenGenerationInTestEnvironment");
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "jwtExpirationMs", -1000); // Already expired

        String expiredToken = shortExpirationJwtUtils.generateJwtToken(userDetails);

        boolean isValid = shortExpirationJwtUtils.validateJwtToken(expiredToken);

        assertFalse(isValid);
    }
}
