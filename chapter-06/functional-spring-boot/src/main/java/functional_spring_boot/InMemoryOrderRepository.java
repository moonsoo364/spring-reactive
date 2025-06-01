package functional_spring_boot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryOrderRepository implements OrderRepository {

    final Map<String, Order> ordersMap;

    public InMemoryOrderRepository() {
        ordersMap = new ConcurrentHashMap<>();
    }

    @Override
    public Mono<Order> findById(String id) {
        return Mono.justOrEmpty(ordersMap.get(id));
    }

    @Override
    public Mono<Order> save(Order order) {
        ordersMap.put(order.getId(), order);
        log.info("save: {}",order.getId());

        return Mono.just(order);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return null;
    }
}
