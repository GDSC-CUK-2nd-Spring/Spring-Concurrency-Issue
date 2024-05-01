package spring.concurrencyissue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.concurrencyissue.entity.Stock;

public interface LockRepository extends JpaRepository<Stock, Long> {
    //락
    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)// timeout: 3000ms(3초) 동안 지정된 키를 사용하여 잠금을 얻으려고 시도
    void getLock(String key);

    //락 해제
    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
}
