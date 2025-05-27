package chap04.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class SubscribeReactiveStream {

    public void p_138() {
        // 기본 구독 (onNext만 처리)
        Flux<Integer> flux = Flux.range(1, 5);  // 1~5까지의 숫자 emit
        flux.subscribe(); // 아무 처리도 안 함
        // 구독하면서 데이터 처리
        flux.subscribe();
        //flux.subscribe(Consumer<T> dataConsumer);
        //flux.subscribe(Consumer<T> dataConsumer, Consumer<Throwable> errorConsumer);
        //flux.subscribe(Consumer<T> dataConsumer, Consumer<Throwable> errorConsumer, Runnable completeConsumer);
        //flux.subscribe(Consumer<T> dataConsumer, Consumer<Throwable> errorConsumer, Runnable completeConsumer, Consumer<Subscription> subscriptionConsumer);
        //flux.subscribe(Subscriber<T> subscriber);
    }
    public void p_139(){
        Flux.just("A","B","C").subscribe(
                data -> log.info("onNext : {}", data),
                err -> {},
                () -> log.info("onComplete")
        );
    }
}

