package spring.concurrencyissue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import spring.concurrencyissue.entity.Stock;
import spring.concurrencyissue.repository.LockRepository;
import spring.concurrencyissue.repository.StockRepository;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final LockRepository lockRepository;

    /**
     * 재고 감소
     */

    public void decrease(final Long id, final Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }

    // synchronized 키워드
    public synchronized void decreaseWithSynchronized(final Long id, final Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }

    // 비관적 락
    @Transactional
    public void decreaseWithPessimisticLock(final Long id, final Long quantity) {
        Stock stock = stockRepository.findByWithPessimisticLock(id);
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }

    // 낙관적 락
    @Transactional
    public void decreaseWithOptimisticLock(final Long id, final Long quantity){
        Stock stock = stockRepository.findByIdWithOptimisticLock(id);
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }

    // 네임드 락
//    @Transactional
//    public void decreaseWithNamedLock(final Long id, final Long quantity){
//        try {
//            lockRepository.getLock(id.toString()); // 네임드 락 얻어오기
//            decreaseWithTx(id, quantity); // Requires_new로 하지 않으면 무한루프
//        } finally {
//            lockRepository.releaseLock(id.toString()); // 네임드 락 해제
//        }
//    }

//    public synchronized void decreaseWithoutTx(Long id, Long quantity) {
//        // Stock 조회, 재고 감소후 갱신값 저장
//        Stock stock = stockRepository.findById(id).orElseThrow();
//        stock.decrease(quantity);
//        stockRepository.saveAndFlush(stock);
//    }
//    @Transactional
//    public void decreaseWithTx(Long id, Long quantity) {
//        // Stock 조회, 재고 감소후 갱신값 저장
//        Stock stock = stockRepository.findById(id).orElseThrow();
//        stock.decrease(quantity);
//        stockRepository.saveAndFlush(stock);
//    }

    // 네임드락
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 부모 트랜잭션과 별도의 DataSource를 사용
    public void decreaseWithNamedLock(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
