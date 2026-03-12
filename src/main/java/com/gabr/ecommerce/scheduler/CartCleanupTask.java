package com.gabr.ecommerce.scheduler;

import com.gabr.ecommerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CartCleanupTask {

    private final CartRepository cartRepository;

    @Scheduled(fixedRate = 1000 * 60 * 60)
    @Transactional
    public void cleanupGuestCarts() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24); // تمسح بعد 24 ساعة
        cartRepository.deleteOldGuestCarts(threshold);
    }

}
