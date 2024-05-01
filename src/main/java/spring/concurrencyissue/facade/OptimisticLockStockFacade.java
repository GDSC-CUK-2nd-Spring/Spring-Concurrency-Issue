package spring.concurrencyissue.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.concurrencyissue.service.StockService;

@Service
@Slf4j
@RequiredArgsConstructor
public class OptimisticLockStockFacade {  // 낙관적락에서 version에 변경이 감지됐을 때 재요청을 하므로 재요청하는 로직

    private final StockService stockService;

    public void decrease(final Long id, final Long quantity) throws InterruptedException {
        while (true) {
            try {
                stockService.decreaseWithOptimisticLock(id, quantity);
                break;

            } catch (Exception e) {
                log.error("error : ", e);
                Thread.sleep(50); // Thread.sleep을 사용해 데이터베이스의 부담을 줄임, (스레드가 50밀리초 동안 일시 중지됨)
            }
        }
    }
}