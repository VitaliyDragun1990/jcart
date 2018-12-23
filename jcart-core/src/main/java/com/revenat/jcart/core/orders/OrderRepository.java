package com.revenat.jcart.core.orders;

import com.revenat.jcart.core.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Gets {@link Order} from database using provided order number.
     * @param orderNumber Order number to look for an {@link Order}
     * @return Found {@link Order} if any, or {@code null}.
     */
    Order findByOrderNumber(String orderNumber);
}
