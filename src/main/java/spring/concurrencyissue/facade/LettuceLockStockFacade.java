package spring.concurrencyissue.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.concurrencyissue.repository.RedisLockRepository;
import spring.concurrencyissue.service.StockService;

@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade  {

    private final RedisLockRepository redisLockRepository;
    private final StockService stockService;

    public void decrease(final Long key, final Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(key)) { // Lock 획득 실패시 재시도
            Thread.sleep(50);
        }
        //Lock 획득 성공
        try{
            stockService.decrease(key,quantity);
        }finally {
        //Lock 해제
            redisLockRepository.unlock(key);
        }
    }

}