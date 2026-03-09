package com.example.cryptopeak.service;

import com.example.cryptopeak.model.Deposit;
import com.example.cryptopeak.model.User;
import com.example.cryptopeak.model.request.DepositRequest;
import com.example.cryptopeak.repository.DepositRepository;
import com.example.cryptopeak.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentService paymentService;

    private User testUser;
    private DepositRequest depositRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmailAddress("test@example.com");
        testUser.setBalance(1000.0);

        depositRequest = new DepositRequest();
        depositRequest.setAmount(500.0);
        depositRequest.setCurrency("USD");
    }

    @Test
    void createVirtualDeposit_Success() {
        Deposit savedDeposit = new Deposit();
        savedDeposit.setId(1L);
        savedDeposit.setAmount(500.0);
        savedDeposit.setStatus("COMPLETED");

        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(depositRepository.save(any(Deposit.class))).thenReturn(savedDeposit);

        Deposit result = paymentService.createVirtualDeposit(testUser, depositRequest);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        assertEquals(1500.0, testUser.getBalance()); // 1000 + 500
        verify(userRepository).save(testUser);
        verify(depositRepository).save(any(Deposit.class));
    }

    @Test
    void getUserDeposits_Success() {
        Deposit deposit1 = new Deposit();
        deposit1.setId(1L);
        deposit1.setAmount(500.0);

        Deposit deposit2 = new Deposit();
        deposit2.setId(2L);
        deposit2.setAmount(1000.0);

        List<Deposit> deposits = Arrays.asList(deposit1, deposit2);
        when(depositRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(deposits);

        List<Deposit> result = paymentService.getUserDeposits(testUser);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(depositRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    void getDepositById_Success() {
        Deposit deposit = new Deposit();
        deposit.setId(1L);
        deposit.setAmount(500.0);

        when(depositRepository.findById(1L)).thenReturn(Optional.of(deposit));

        Deposit result = paymentService.getDepositById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(500.0, result.getAmount());
    }

    @Test
    void getDepositById_NotFound_ThrowsException() {
        when(depositRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            paymentService.getDepositById(999L);
        });
    }

    @Test
    void resetBalance_Success() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = paymentService.resetBalance(testUser);

        assertNotNull(result);
        assertEquals(0.0, result.getBalance());
        verify(userRepository).save(testUser);
    }
}
