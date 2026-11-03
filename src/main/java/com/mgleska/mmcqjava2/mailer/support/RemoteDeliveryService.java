package com.mgleska.mmcqjava2.mailer.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class RemoteDeliveryService {

    private final Random random = new Random();

    public boolean deliver(String recipient, String message) throws InterruptedException {

        Thread.sleep(3000);

        // simulated fail for 25% of deliveries
        var number = random.nextInt(1, 9);
        if (number <= 2) {
            log.warn("Delivery fail for receiver '{}' and message '{}'", recipient, message);
            return false;
        }

        return true;
    }
}
