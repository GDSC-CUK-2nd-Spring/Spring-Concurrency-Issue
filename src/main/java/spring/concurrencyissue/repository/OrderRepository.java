package spring.concurrencyissue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.concurrencyissue.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
