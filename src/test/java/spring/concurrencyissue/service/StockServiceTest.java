package spring.concurrencyissue.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.concurrencyissue.entity.Stock;
import spring.concurrencyissue.facade.NamedLockStockFacade;
import spring.concurrencyissue.facade.OptimisticLockStockFacade;
import spring.concurrencyissue.repository.StockRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;

    @Autowired
    private NamedLockStockFacade namedLockStockFacade;

    @BeforeEach
    void before() { //quantity가 100인 Stock 객체를 생성
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @Test
    public void 재고감소() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 스레드풀, 스레드 생성
        CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서의 작업이 완료될 때까지 대기할 수 있게 도와주는 클래스

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown(); // CountDownLatch에 파라미터로 넘겨준 숫자에서 -1씩 한다.
                }
            });
        }

        latch.await(); // CountDownLatch를 생성할 때 파라미터로 넘겨준 숫자가 0이 될 때까지 대기한다.

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertThat(stock.getQuantity()).isEqualTo(0);
    }

    @Test
    public void 재고감소_Synchronized() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 스레드풀, 스레드 생성
        CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서의 작업이 완료될 때까지 대기할 수 있게 도와주는 클래스

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseWithSynchronized(1L, 1L);
                } finally {
                    latch.countDown(); // CountDownLatch에 파라미터로 넘겨준 숫자에서 -1씩 한다.
                }
            });
        }

        latch.await(); // CountDownLatch를 생성할 때 파라미터로 넘겨준 숫자가 0이 될 때까지 대기한다.

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertThat(stock.getQuantity()).isEqualTo(0);
    }


    @Test
    public void 재고감소_비관적락() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 스레드풀, 스레드 생성
        CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서의 작업이 완료될 때까지 대기할 수 있게 도와주는 클래스

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseWithPessimisticLock(1L, 1L);
                } finally {
                    latch.countDown(); // CountDownLatch에 파라미터로 넘겨준 숫자에서 -1씩 한다.
                }
            });
        }

        latch.await(); // CountDownLatch를 생성할 때 파라미터로 넘겨준 숫자가 0이 될 때까지 대기한다.

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertThat(stock.getQuantity()).isEqualTo(0);
    }

    @Test
    public void 재고감소_낙관적락() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 스레드풀, 스레드 생성
        CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서의 작업이 완료될 때까지 대기할 수 있게 도와주는 클래스

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    optimisticLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown(); // CountDownLatch에 파라미터로 넘겨준 숫자에서 -1씩 한다.
                }
            });
        }

        latch.await(); // CountDownLatch를 생성할 때 파라미터로 넘겨준 숫자가 0이 될 때까지 대기한다.

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertThat(stock.getQuantity()).isEqualTo(0);
    }

    @Test
    public void 재고감소_네임드락() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 스레드풀, 스레드 생성
        CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서의 작업이 완료될 때까지 대기할 수 있게 도와주는 클래스

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    namedLockStockFacade.decrease(1L, 1L);
                } finally {
                    latch.countDown(); // CountDownLatch에 파라미터로 넘겨준 숫자에서 -1씩 한다.
                }
            });
        }

        latch.await(); // CountDownLatch를 생성할 때 파라미터로 넘겨준 숫자가 0이 될 때까지 대기한다.

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertThat(stock.getQuantity()).isEqualTo(0);
    }

}