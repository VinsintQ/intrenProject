package com.example.novie.service;

import com.example.novie.model.Deposit;
import com.example.novie.model.User;
import com.example.novie.model.request.DepositRequest;
import com.example.novie.model.response.PaymentIntentResponse;
import com.example.novie.repository.DepositRepository;
import com.example.novie.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Value("${stripe.api.key:sk_test_your_stripe_secret_key}")
    private String stripeApiKey;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private UserRepository userRepository;

    public PaymentIntentResponse createPaymentIntent(User user, DepositRequest request) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        // Convert amount to cents (Stripe uses smallest currency unit)
        long amountInCents = (long) (request.getAmount() * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(request.getCurrency().toLowerCase())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Save deposit record
        Deposit deposit = new Deposit();
        deposit.setUser(user);
        deposit.setAmount(request.getAmount());
        deposit.setCurrency(request.getCurrency());
        deposit.setStatus("PENDING");
        deposit.setPaymentIntentId(paymentIntent.getId());
        depositRepository.save(deposit);

        return new PaymentIntentResponse(
                paymentIntent.getClientSecret(),
                paymentIntent.getId(),
                deposit.getId(),
                request.getAmount(),
                request.getCurrency()
        );
    }

    public Deposit confirmPayment(String paymentIntentId) {
        Deposit deposit = depositRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Deposit not found"));

        deposit.setStatus("COMPLETED");
        
        // Update user balance
        User user = deposit.getUser();
        user.setBalance(user.getBalance() + deposit.getAmount());
        userRepository.save(user);
        
        return depositRepository.save(deposit);
    }

    public List<Deposit> getUserDeposits(User user) {
        return depositRepository.findByUser(user);
    }

    public Deposit getDepositById(Long id) {
        return depositRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deposit not found"));
    }
}
