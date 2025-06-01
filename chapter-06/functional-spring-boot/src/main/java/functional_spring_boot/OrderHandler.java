package functional_spring_boot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class OrderHandler {// 1

    final OrderRepository orderRepository;

    public Mono<ServerResponse> create(ServerRequest request){// 2
        return request
                .bodyToMono(
                        Order.class
                )// 2.1
                .flatMap(
                        orderRepository::save
                )
                .flatMap(o ->
                        ServerResponse.created(URI.create("/orders"+o.getId()))// 2.2
                                .build()
                );
    }

    public Mono<ServerResponse> get(ServerRequest request){
        return orderRepository.findById(request.pathVariable("id"))
                .flatMap(order ->
                        ServerResponse
                                .ok()
                                .syncBody(order)
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return null;
    }

}
