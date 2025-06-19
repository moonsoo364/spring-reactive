package org.rpis5.chapters.chapter_09;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;


public class DoMyTest {

    @Test
    public void p_436_1(){
        StepVerifier
                .create(Flux.just("foo","bar"))
                .expectSubscription()
                .expectNext("foo")
                .expectNext("bar")
                .expectComplete()
                .verify();
    }

    @Test
    public void p_436_2(){
        StepVerifier
                .create(Flux.range(0, 100))
                .expectSubscription()
                .expectNext(0)
                .expectNextCount(98)
                .expectNext(99)
                .expectComplete()
                .verify();
    }
    @Test
    public void p_438_1(){
        StepVerifier.create(Flux.just("alpha-foo", "betta-bar"))
                .expectSubscription()
                .expectNextMatches(e -> e.startsWith("alpha"))
                .expectNextMatches(e -> e.startsWith("betta"))
                .expectComplete()
                .verify();
    }

    @Test
    public void p_439_1(){
        StepVerifier
                .create(Flux.error(new RuntimeException("Error")))
                .expectError()
                .verify();
    }

    public Flux<String> sendWithInterval(){
        return Flux.interval(Duration.ofSeconds(1))
                .zipWith(Flux.just("a","b","c"))
                .map(Tuple2::getT2);

    }

    @Test
    public void p_442(){
        StepVerifier
                .create(sendWithInterval())
                .expectSubscription()
                .expectNext("a","b","c")
                .expectComplete()
                .verify();
    }


}
