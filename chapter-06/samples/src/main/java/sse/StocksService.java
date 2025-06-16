package sse;

import reactor.core.publisher.Flux;

public interface StocksService {

    Flux<StockItem> stream();
}