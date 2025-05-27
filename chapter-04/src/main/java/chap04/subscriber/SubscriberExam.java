package chap04.subscriber;

import chap04.flux.FluxExam;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

@Slf4j
public class SubscriberExam {
    public void p_141(){
        Subscriber<String> subscriber = new Subscriber<String>() {
            volatile Subscription subscription;


            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                log.info("requesting 1 more element");
                subscription.request(1);

            }

            @Override
            public void onNext(String s) {
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
        Flux<String> stream = Flux.just("Hello","World","!");
        stream.subscribe();
    }
}
