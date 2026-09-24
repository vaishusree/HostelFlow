package com.example.hostelflow.service;

import com.example.hostelflow.dto.ActivateRequest;
import com.example.hostelflow.dto.UserProvisionResponse;
import com.example.hostelflow.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Proves the race condition that existed in the old
 * find -> isActivated() check -> save() sequence, and proves the
 * atomic-UPDATE fix closes it.
 *
 * The scenario: a student's activation link/code gets submitted twice at
 * almost the same instant (double click, retried request, two tabs, etc.)
 * with the SAME valid activation code. Exactly one of those concurrent
 * requests should succeed in activating the account; every other one should
 * fail cleanly with "User is already activated" rather than silently
 * re-processing the activation.
 */
@SpringBootTest
class UserServiceConcurrencyTest {

    private static final int CONCURRENT_REQUESTS = 20;

    @Autowired
    private UserService userService;

    private String registrationNumber;
    private String activationCode;

    @BeforeEach
    void provisionStudent() {
        registrationNumber = "REG-CONC-" + System.nanoTime();

        UserRequest request = new UserRequest();
        request.setRegistrationNumber(registrationNumber);
        request.setName("Concurrency Test Student");
        request.setEmail(registrationNumber.toLowerCase() + "@example.com");

        UserProvisionResponse provisioned = userService.provisionStudent(request);
        activationCode = provisioned.getActivationCode();
    }

    @Test
    void onlyOneOfManyConcurrentActivationsWithTheSameCodeSucceeds() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENT_REQUESTS);
        CountDownLatch readyLatch = new CountDownLatch(CONCURRENT_REQUESTS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(CONCURRENT_REQUESTS);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger alreadyActivatedCount = new AtomicInteger(0);
        AtomicInteger unexpectedFailureCount = new AtomicInteger(0);

        for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
            final int requestIndex = i;
            pool.submit(() -> {
                try {
                    ActivateRequest activateRequest = new ActivateRequest();
                    activateRequest.setRegistrationNumber(registrationNumber);
                    activateRequest.setActivationCode(activationCode);
                    activateRequest.setNewPassword("Password-" + requestIndex + "!");

                    // All threads line up here, then get released at once so
                    // they hit activateStudent() as close to simultaneously
                    // as the JVM/DB allow.
                    readyLatch.countDown();
                    startLatch.await();

                    userService.activateStudent(activateRequest);
                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    if ("User is already activated".equals(e.getMessage())) {
                        alreadyActivatedCount.incrementAndGet();
                    } else {
                        unexpectedFailureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    unexpectedFailureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();
        boolean finished = doneLatch.await(20, TimeUnit.SECONDS);
        pool.shutdown();

        assertEquals(true, finished, "Concurrent activation requests did not finish in time");
        assertEquals(1, successCount.get(),
                "Exactly one concurrent activation should succeed");
        assertEquals(CONCURRENT_REQUESTS - 1, alreadyActivatedCount.get(),
                "Every other concurrent request should fail with 'already activated', not silently overwrite");
        assertEquals(0, unexpectedFailureCount.get(),
                "No request should fail for a reason other than losing the race");
    }
}
