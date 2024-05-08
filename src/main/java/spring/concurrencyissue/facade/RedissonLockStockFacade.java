package spring.concurrencyissue.facade;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import spring.concurrencyissue.service.StockService;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockStockFacade {

    private final RedissonClient redissonClient;
    private final StockService stockService;

    public void decrease(final Long key, final Long quantity) {

        RLock lock = redissonClient.getLock(key.toString());

        try {

            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS); // 획득시도 시간, 락 점유 시간, 시간 단위

            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }

            stockService.decrease(key, quantity);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
    }

}