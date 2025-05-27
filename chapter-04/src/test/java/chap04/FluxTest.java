package chap04;

import chap04.dto.MySubscriber;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class FluxTest {

    @Test
    public void p_139_1(){
        Flux.just("A","B","C").subscribe(
                data -> log.info("onNext : {}", data),
                err -> {},
                () -> log.info("onComplete")
        );
    }

    @Test
    public void p_139_2(){
        Flux.range(1, 100)
                .subscribe(
                        data -> log.info("onNext : {}",data),
                        err -> {},
                        () -> log.info("onComplete"),
                        subscription -> {//3
                            subscription.request(4);
                            subscription.cancel();
                        }
                );
    }
    @Test
    public void p_140() throws InterruptedException { // 1
        Disposable disposable = Flux.interval(Duration.ofMillis(50))
                .subscribe(// 2
                data -> log.info("onNext: {}", data)
        );
        Thread.sleep(200);// 3
        disposable.dispose();// 4
    }

    @Test
    public void p_141(){
        Subscriber<String> subscriber = new Subscriber<String>() {
            volatile Subscription subscription;//1

            @Override
            public void onSubscribe(Subscription s) {//2
                subscription = s;
                log.info("requesting 1 more element");
                subscription.request(1);

            }

            @Override
            public void onNext(String s) {//3
                log.info("onNext : {}", s);
                log.info("requesting 1 more element");
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("onError : {}",t.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };
        Flux<String> stream = Flux.just("Hello","World","!");//4
        stream.subscribe(subscriber);//5
    }
    @Test
    public void p_143(){
        Subscriber<String> subscriber = new MySubscriber<String>();
        Flux<String> stream = Flux.just("Hello","World","!");
        stream.subscribe(subscriber);
    }
}
