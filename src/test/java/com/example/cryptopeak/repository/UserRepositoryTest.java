package com.example.cryptopeak.repository;

import com.example.cryptopeak.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUserName("Test User");
        testUser.setEmailAddress("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setActive(true);
        testUser.setAccountVerified(true);
        testUser.setBalance(1000.0);
    }

    @Test
    void saveUser_Success() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser.getId());
        assertEquals("test@example.com", savedUser.getEmailAddress());
        assertEquals(1000.0, savedUser.getBalance());
    }

    @Test
    void existsByEmailAddress_ReturnsTrue() {
        userRepository.save(testUser);

        boolean exists = userRepository.existsByEmailAddress("test@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmailAddress_ReturnsFalse() {
        boolean exists = userRepository.existsByEmailAddress("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    void findUserByEmailAddress_Success() {
        userRepository.save(testUser);

        User foundUser = userRepository.findUserByEmailAddress("test@example.com");

        assertNotNull(foundUser);
        assertEquals("Test User", foundUser.getUserName());
        assertEquals(1000.0, foundUser.getBalance());
    }

    @Test
    void findUserByEmailAddress_NotFound_ReturnsNull() {
        User foundUser = userRepository.findUserByEmailAddress("nonexistent@example.com");

        assertNull(foundUser);
    }

    @Test
    void updateUserBalance_Success() {
        User savedUser = userRepository.save(testUser);
        savedUser.setBalance(2000.0);

        User updatedUser = userRepository.save(savedUser);

        assertEquals(2000.0, updatedUser.getBalance());
    }
}
